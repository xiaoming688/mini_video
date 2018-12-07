package com.mini_video.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users_like_videos")
public class UsersLikeVideos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 用户
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 视频
     */
    @Column(name = "video_id")
    private Integer videoId;

}