package com.mini_video.repository;

import com.mini_video.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author MM
 * @create 2018-12-07 14:19
 **/
public interface UserRepository extends JpaRepository<Users, Integer>, QuerydslPredicateExecutor<Users> {

    Users findByUsername(String userName);

    Users findByOpenid(String opendId);

    @Query("select u from Users u where u.id=:id")
    Users findUser(@Param("id") Integer id);

    Users findByUsernameAndPassword(String userName, String passWord);


    @Modifying //说明该操作是修改类型操作，删除或者修改
    @Transactional //因为默认是readOnly=true的，这里必须自己进行声明
    @Query("update Users t set t.receiveLikeCounts=t.receiveLikeCounts+1 where id=:userId")
    public void addReceiveLikeCounts(@Param("userId") Integer userId);


    @Modifying //说明该操作是修改类型操作，删除或者修改
    @Transactional //因为默认是readOnly=true的，这里必须自己进行声明
    @Query("update Users t set t.receiveLikeCounts=t.receiveLikeCounts-1 where id=:userId")
    public void reduceReceiveLikeCounts(@Param("userId") Integer userId);
}
