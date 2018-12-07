package com.mini_video.repository;

import com.mini_video.pojo.UsersLikeVideos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UsersLikeVideosRepository extends JpaRepository<UsersLikeVideos, Integer>, QuerydslPredicateExecutor<UsersLikeVideos> {

    UsersLikeVideos findByUserIdAndVideoId(Integer userId, Integer videoId);
}
