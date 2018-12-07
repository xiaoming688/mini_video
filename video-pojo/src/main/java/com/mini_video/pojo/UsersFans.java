package com.mini_video.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users_fans")
public class UsersFans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 粉丝
     */
    @Column(name = "fan_id")
    private Integer fanId;

}