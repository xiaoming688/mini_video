package com.mini_video.controller;

import com.mini_video.pojo.Users;
import com.mini_video.pojo.UsersReport;
import com.mini_video.pojo.vo.PublisherVideo;
import com.mini_video.pojo.vo.PublisherVo;
import com.mini_video.pojo.vo.UsersVO;
import com.mini_video.service.UserService;
import com.mini_video.utils.AliyunOSSUtil;
import com.mini_video.utils.Constants;
import com.mini_video.utils.MData;
import com.mini_video.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * @author MM
 * @create 2018-12-07 15:31
 **/

@RestController
@RequestMapping("/miniVideo/user")
public class UserController {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private AliyunOSSUtil aliyunOSSUtil;

    @PostMapping("/updateUserTest")
    public MData updateUserTest(@RequestBody Users users) {
        MData result = new MData();

        Users upate = userService.updateUsers(users);
        result.put("data", upate);
        return result;
    }


    @PostMapping("/uploadFace")
    public MData uploadFace(String userId,
                            @RequestParam("file") MultipartFile[] files) throws Exception {
        MData result = new MData();
        if (StringUtils.isBlank(userId)) {
            result.error("用户id不能为空...");
            return result;
        }

        // 保存到数据库中的相对路径
        String uploadPathDB = "";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();
                log.info("fileName: " + fileName);
                if (StringUtils.isNotBlank(fileName)) {

                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));

                    long currentTimeMillis = System.currentTimeMillis();

                    // 文件上传的最终保存路径
                    String key = Constants.OSS_FACE_FOLDER + userId + "/" + currentTimeMillis + fileExtension;
                    log.info("key: " + key);
                    String uploadRes = aliyunOSSUtil.uploadInputStream(key, files[0].getInputStream());
                    result.put("data", key);
                    log.info("uploadRes: " + uploadRes);
                    uploadPathDB = key;
                }

            } else {
                result.error("用户id不能为空...");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error("用户id不能为空...");
            return result;
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        Users user = userService.queryUserInfo(Integer.valueOf(userId));
        user.setId(Integer.valueOf(userId));
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);

        return result;
    }


    @PostMapping("/query")
    public MData query(Integer userId, Integer fanId) throws Exception {
        MData result = new MData();
        if (StringUtil.isBlank(userId)) {
            return result.error("用户id不能为空...");
        }
        long t1 = System.currentTimeMillis();
        Users userInfo = userService.queryUserInfo(Integer.valueOf(userId));
        long t2 = System.currentTimeMillis();
        log.info(t2 - t1);
        if (userInfo == null) {
            return result.error("no data");
        }
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userInfo, userVO);

        userVO.setFollow(userService.queryIfFollow(userId, fanId));
        result.put("data", userVO);
        return result;
    }

    @PostMapping("/queryPublisher")
    public MData queryPublisher(@RequestBody PublisherVo publisherVo) throws Exception {
        MData result = new MData();
        Integer publishUserId = publisherVo.getPublishUserId();
        Integer videoId = publisherVo.getVideoId();
        Integer loginUserId = publisherVo.getLoginUserId();
        if (StringUtil.isBlank(publishUserId)) {
            result.error("publishUserId is null");
            return result;
        }

        // 1. 查询视频发布者的信息
        Users userInfo = userService.queryUserInfo(publishUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo, publisher);

        // 2. 查询当前登录者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);
        result.put("data", bean);
        return result;
    }


    @PostMapping("/beyourfans")
    public MData beyourfans(@RequestBody Map<String, Object> paramsMap) throws Exception {
        MData result = new MData();

        if (StringUtil.isBlank(paramsMap.get("userId")) || StringUtil.isBlank(paramsMap.get("fanId"))) {
            result.error("params is null");
            return result;
        }
        Integer userId = Integer.valueOf(String.valueOf(paramsMap.get("userId")));
        Integer fanId = Integer.valueOf(String.valueOf(paramsMap.get("fanId")));
        userService.saveUserFanRelation(userId, fanId);
        result.ok("关注成功...");
        return result;
    }

    @PostMapping("/dontbeyourfans")
    public MData dontbeyourfans(@RequestBody Map<String, Object> paramsMap) throws Exception {
        MData result = new MData();
        if (StringUtil.isBlank(paramsMap.get("userId")) || StringUtil.isBlank(paramsMap.get("fanId"))) {
            result.error("params is null");
            return result;
        }
        Integer userId = Integer.valueOf(String.valueOf(paramsMap.get("userId")));
        Integer fanId = Integer.valueOf(String.valueOf(paramsMap.get("fanId")));
        userService.deleteUserFanRelation(userId, fanId);

        result.ok("取消关注成功...");
        return result;
    }

    @PostMapping("/reportUser")
    public MData reportUser(@RequestBody UsersReport usersReport) throws Exception {
        MData result = new MData();
        // 保存举报信息
        userService.reportUser(usersReport);

        result.ok("举报成功...");
        return result;
    }
}
