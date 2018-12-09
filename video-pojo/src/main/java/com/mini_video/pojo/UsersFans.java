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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFanId() {
        return fanId;
    }

    public void setFanId(Integer fanId) {
        this.fanId = fanId;
    }
}