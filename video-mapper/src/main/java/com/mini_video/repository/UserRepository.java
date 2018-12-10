package com.mini_video.repository;

import com.mini_video.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

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

}
