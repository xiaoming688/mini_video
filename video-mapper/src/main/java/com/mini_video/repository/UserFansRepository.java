package com.mini_video.repository;

import com.mini_video.pojo.UsersFans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserFansRepository extends JpaRepository<UsersFans, Integer>, QuerydslPredicateExecutor<UsersFans> {

    UsersFans findByUserIdAndFanId(Integer userId, Integer fansId);
}
