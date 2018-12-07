package com.mini_video.repository;

import com.mini_video.pojo.UsersFans;
import com.mini_video.pojo.UsersReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserReportRepository extends JpaRepository<UsersReport, Integer>, QuerydslPredicateExecutor<UsersReport> {
}
