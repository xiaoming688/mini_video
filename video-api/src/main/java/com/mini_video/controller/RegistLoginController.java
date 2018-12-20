package com.mini_video.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mini_video.pojo.Users;
import com.mini_video.pojo.vo.LoginVo;
import com.mini_video.pojo.vo.UserDetailVo;
import com.mini_video.pojo.vo.UsersVO;
import com.mini_video.service.UserService;
import com.mini_video.utils.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/miniVideo/user")
public class RegistLoginController {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private CommonConstants commonConstants;

    @Autowired
    private MpWxMiniProgram mpWxMiniProgram;


    @PostMapping("/regist")
    public MData regist(@RequestBody Users user) throws Exception {
        MData result = new MData();
        // 1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return result.error("用户名和密码不能为空");
        }

        // 2. 判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());

        // 3. 保存用户，注册信息
        if (!usernameIsExist) {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            user = userService.saveUser(user);
        } else {
            return result.error("用户名已经存在，请换一个再试");
        }

        user.setPassword("");

        UsersVO userVO = setUserRedisSessionToken(user);
        result.put("data", userVO);
        return result;
    }

    public UsersVO setUserRedisSessionToken(Users userModel) {
        String uniqueToken = UUID.randomUUID().toString();
        redisOperator.set(Constants.USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);

        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userModel, userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }

    @PostMapping("/login")
    public MData login(@RequestBody Users user) throws Exception {

        MData result = new MData();
        String username = user.getUsername();
        String password = user.getPassword();

        // 1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return result.error("用户名或密码不能为空...");
        }

        // 2. 判断用户是否存在
        Users userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(user.getPassword()));

        // 3. 返回
        if (userResult != null) {
            userResult.setPassword("");
            UsersVO userVO = setUserRedisSessionToken(userResult);
            result.put("data", userVO);
            return result;
        } else {
            result.error("用户名或密码不正确, 请重试...");
            return result;
        }
    }


    @RequestMapping(value = "/onLogin", method = RequestMethod.POST)
    @Transactional
    public MData miniLogin(@RequestBody LoginVo loginForm) {
        MData result = new MData();
        String code = loginForm.getCode();
        //小程序类型
        String appId = loginForm.getAppId();

        if (org.springframework.util.StringUtils.isEmpty(code) || org.springframework.util.StringUtils.isEmpty(appId)) {
            log.error("params has empty value with code:" + code
                    + " appId:" + appId);
            result.error("params has empty value with code:" + code
                    + " appId:" + appId);
            return result;
        }
        try {
            JSONObject userinfoObj = mpWxMiniProgram.getLoginInfo(appId, commonConstants.getAppSecret(), code, "authorization_code");
            //该小程序openid，正常都会返回，不正常不会返回
            Object openid = userinfoObj.get("openid");
            //会话秘钥
            Object sessionKey = userinfoObj.get("session_key");

            //不正常直接返回，重新发起登录
            if (openid == null) {
                String errcode = userinfoObj.get("errcode").toString();
                String errmsg = userinfoObj.get("errmsg").toString();
                log.error(errcode + errmsg);
                result.error(errcode + errmsg);
                return result;
            }
            //save sessionKey to redis
            redisOperator.set(appId + "_" + openid, sessionKey.toString(), 1000 * 30 * 10);

            Users user = userService.queryUserByOpenId(String.valueOf(openid));
            if (user == null) {
                user = new Users();
                user.setOpenid(String.valueOf(openid));
                user.setFansCounts(0);
                user.setReceiveLikeCounts(0);
                user.setFollowCounts(0);
                user.setPassword("4QrcOUm6Wau+VuBX8g+IPg==");
                user.setUsername("test"+ RandomUtils.nextInt(0,10000));
                user = userService.saveUser(user);
            }

            result.put("isValid", true);
            result.put("openid", openid);
            result.put("id", user.getId());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error("program error");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }


    @RequestMapping(value = "/mini/userInfoDetail", method = RequestMethod.POST)
    public MData checkUserInfoDetail(@RequestBody UserDetailVo userDetailForm) {
        MData result = new MData();
        try {
            String encryptedData = userDetailForm.getEncryptedData();
            String iv = userDetailForm.getIv();
            String appId = userDetailForm.getAppId();
            String opendId = userDetailForm.getOpenId();

            if (org.springframework.util.StringUtils.isEmpty(encryptedData) || org.springframework.util.StringUtils.isEmpty(iv)
                    || org.springframework.util.StringUtils.isEmpty(appId) || org.springframework.util.StringUtils.isEmpty(opendId)) {
                result.error("params has empty value UserDetailForm:" + userDetailForm);
                return result;
            }

            String sessionKey = redisOperator.get(appId + "_" + opendId);
            log.info("sessionKey: " + sessionKey);
            //sessionKey不正确，前台重新发起login
            if (org.springframework.util.StringUtils.isEmpty(sessionKey)) {
                log.info("session_key is null");
                result.error("session_key is null");
                return result;
            }
            String decRes = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
            if (!org.springframework.util.StringUtils.isEmpty(decRes)) {
                JSONObject desJson = JSON.parseObject(decRes);
                log.info("userInfoDetail desJson  : " + desJson.toJSONString());
                String openid = String.valueOf(desJson.get("openId"));
                String nickname = String.valueOf(desJson.get("nickName"));
                String avatarUrl = String.valueOf(desJson.get("avatarUrl"));

                Users user = userService.queryUserByOpenId(String.valueOf(openid));
                if (user == null) {
                    user = new Users();
                    user.setOpenid(String.valueOf(openid));
                    user.setFansCounts(0);
                    user.setReceiveLikeCounts(0);
                    user.setFollowCounts(0);
                    user.setNickname(nickname);
                    user.setFaceImage(avatarUrl);
                    user = userService.saveUser(user);
                } else if (user.getNickname() == null || user.getFaceImage() == null) {
                    user = new Users();
                    user.setId(user.getId());
                    user.setNickname(nickname);
                    user.setFaceImage(avatarUrl);
                    user = userService.saveUser(user);
                }
                result.put("id", user.getId());
                result.put("faceImage", avatarUrl);
                result.put("nickName", nickname);
                result.put("openId", opendId);
                UsersVO userVO = setUserRedisSessionToken(user);
                result.put("userToken", userVO.getUserToken());
            } else {
                log.error("decrypt data failed");
                result.error("decrypt data failed");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result.error("an excetion with :" + ex);
        }
        return result;

    }


    @PostMapping("/logout")
    public MData logout(String userId) throws Exception {
        redisOperator.del(Constants.USER_REDIS_SESSION + ":" + userId);
        return new MData();
    }

}
