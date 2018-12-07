package com.mini_video.repository;

import com.mini_video.pojo.Bgm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BgmRepository extends JpaRepository<Bgm, Integer>, QuerydslPredicateExecutor<Bgm> {
}
