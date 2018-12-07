package com.mini_video.pojo.vo;

import java.io.Serializable;

public class PublisherVideo implements Serializable{
    
	public UsersVO publisher;
	public boolean userLikeVideo;
	public UsersVO getPublisher() {
		return publisher;
	}
	public void setPublisher(UsersVO publisher) {
		this.publisher = publisher;
	}
	public boolean isUserLikeVideo() {
		return userLikeVideo;
	}
	public void setUserLikeVideo(boolean userLikeVideo) {
		this.userLikeVideo = userLikeVideo;
	}
}