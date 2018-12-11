package com.mini_video.repository;

import com.mini_video.pojo.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CommentRepository extends JpaRepository<Comments, Integer>, QuerydslPredicateExecutor<Comments> {
}
