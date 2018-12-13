package com.mini_video.service.impl;

import com.mini_video.pojo.Users;
import com.mini_video.pojo.UsersFans;
import com.mini_video.pojo.UsersReport;
import com.mini_video.repository.UserFansRepository;
import com.mini_video.repository.UserReportRepository;
import com.mini_video.repository.UserRepository;
import com.mini_video.repository.UsersLikeVideosRepository;
import com.mini_video.service.UserService;
import com.mini_video.utils.BeanCopyUtil;
import com.mini_video.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * @author MM
 * @create 2018-12-07 14:21
 **/
@Service("userService")
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersLikeVideosRepository usersLikeVideosRepository;

    @Autowired
    private UserFansRepository userFansRepository;

    @Autowired
    private UserReportRepository userReportRepository;

    @Autowired
    private EntityManager entityManager;


    @Override
    public boolean queryUsernameIsExist(String username) {

        return userRepository.findByUsername(username) != null;
    }

    @Override
    public Users queryUserByOpenId(String openId) {

        return userRepository.findByOpenid(openId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public Users queryUserForLogin(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public void updateUserInfo(Users user) {
        userRepository.save(user);
    }


    @Override
    public Users queryUserInfo(Integer userId) {
        return userRepository.findUser(userId);
    }

    @Override
    public boolean isUserLikeVideo(Integer userId, Integer videoId) {

        if (StringUtil.isBlank(userId) || StringUtil.isBlank(videoId)) {
            return false;
        }

        return usersLikeVideosRepository.findByUserIdAndVideoId(userId, videoId) != null;
    }

    @Override
    public void saveUserFanRelation(Integer userId, Integer fanId) {
        UsersFans usersFans = new UsersFans();
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        userFansRepository.save(usersFans);
    }

    @Override
    public void deleteUserFanRelation(Integer userId, Integer fanId) {
        UsersFans usersFans = userFansRepository.findByUserIdAndFanId(userId, fanId);
        userFansRepository.delete(usersFans);
    }

    @Override
    public boolean queryIfFollow(Integer userId, Integer fanId) {
        if (StringUtil.isBlank(userId) || StringUtil.isBlank(fanId)) {
            return false;
        }

        return userFansRepository.findByUserIdAndFanId(userId, fanId) != null;
    }

    @Override
    public void reportUser(UsersReport userReport) {
        userReportRepository.save(userReport);
    }

    @Override
    @Transactional
    public Users updateUsers(Users user) {
        if (user.getId() == null) {
            return null;
        }
        Users saveTask = userRepository.findUser(user.getId());
        BeanCopyUtil.beanCopyWithIngore(user, saveTask, "password");
        return userRepository.saveAndFlush(saveTask);
    }

}
