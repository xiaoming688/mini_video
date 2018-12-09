package com.mini_video.repository;

import com.mini_video.pojo.Videos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface VideosRepository extends JpaRepository<Videos, Integer>, QuerydslPredicateExecutor<Videos> {

}
