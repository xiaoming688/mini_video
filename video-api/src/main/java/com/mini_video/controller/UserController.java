package com.mini_video.controller;

import com.mini_video.pojo.Users;
import com.mini_video.pojo.vo.UsersVO;
import com.mini_video.service.UserService;
import com.mini_video.utils.MData;
import com.mini_video.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/query")
    public MData query(Integer userId, Integer fanId) throws Exception {
        MData result = new MData();
        if (StringUtil.isBlank(userId)) {
            return result.error("用户id不能为空...");
        }
        long t1 = System.currentTimeMillis();
        Users userInfo = userService.queryUserInfo(Integer.valueOf(userId));
        long t2 = System.currentTimeMillis();
        log.info(t2-t1);
        if(userInfo == null){
            return result.error("no data");
        }
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userInfo, userVO);

        userVO.setFollow(userService.queryIfFollow(userId, fanId));
        result.put("data", userVO);
        return result;
    }


}
