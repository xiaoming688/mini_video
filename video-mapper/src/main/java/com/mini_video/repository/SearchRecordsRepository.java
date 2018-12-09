package com.mini_video.repository;

import com.mini_video.pojo.SearchRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SearchRecordsRepository extends JpaRepository<SearchRecords, Integer>, QuerydslPredicateExecutor<SearchRecords> {
}
