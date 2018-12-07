package com.mini_video.controller;

import com.mini_video.pojo.Users;
import com.mini_video.pojo.vo.UsersVO;
import com.mini_video.service.UserService;
import com.mini_video.utils.Constants;
import com.mini_video.utils.MD5Utils;
import com.mini_video.utils.MData;
import com.mini_video.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/miniVideo/user")
public class RegistLoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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
            userService.saveUser(user);
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

    @PostMapping("/logout")
    public MData logout(String userId) throws Exception {
        redisOperator.del(Constants.USER_REDIS_SESSION + ":" + userId);
        return new MData();
    }

}
