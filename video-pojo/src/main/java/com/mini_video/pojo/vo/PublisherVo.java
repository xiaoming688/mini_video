package com.mini_video.pojo.vo;

import lombok.Data;

/**
 * @author MM
 * @create 2018-12-07 17:17
 **/
@Data
public class PublisherVo {
    Integer videoId;
    Integer loginUserId;
    Integer publishUserId;


    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(Integer loginUserId) {
        this.loginUserId = loginUserId;
    }

    public Integer getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(Integer publishUserId) {
        this.publishUserId = publishUserId;
    }
}
