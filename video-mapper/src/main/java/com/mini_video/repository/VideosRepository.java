package com.mini_video.repository;

import com.mini_video.pojo.Videos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface VideosRepository extends JpaRepository<Videos, Integer>, QuerydslPredicateExecutor<Videos> {

    @Modifying //说明该操作是修改类型操作，删除或者修改
    @Transactional //因为默认是readOnly=true的，这里必须自己进行声明
    @Query("update Videos t set t.likeCounts=t.likeCounts+1 where t.id=:videoId")
    void addVideoLikeCounts(@Param("videoId") Integer videoId);


    @Modifying //说明该操作是修改类型操作，删除或者修改
    @Transactional //因为默认是readOnly=true的，这里必须自己进行声明
    @Query("update Videos t set t.likeCounts=t.likeCounts-1 where t.id=:videoId")
    void reduceVideoLikeCounts(@Param("videoId") Integer videoId);
}
