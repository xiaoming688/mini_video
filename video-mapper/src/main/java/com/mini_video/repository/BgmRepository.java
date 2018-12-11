package com.mini_video.repository;

import com.mini_video.pojo.Bgm;
import com.mini_video.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface BgmRepository extends JpaRepository<Bgm, Integer>, QuerydslPredicateExecutor<Bgm> {

    @Query("select u from Bgm u where u.id=:id")
    Bgm findBgm(@Param("id") Integer id);
}
