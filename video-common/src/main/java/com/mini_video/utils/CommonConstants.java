package com.mini_video.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 一些公共参数变量
 * @author MM
 * @create 2018-09-06 13:56
 **/
@Configuration
@PropertySource(value = {"classpath:${spring.profiles.active}/constants-${spring.profiles.active}.properties"}, encoding = "utf-8")
@ConfigurationProperties(prefix = "constants")
public class CommonConstants {

    private String authDomain;

    private String ossHost;

    private String endpoint;

    private String sessionDomain;

    private String bucketName;

    private String accessKey;

    private String accessSecret;

    private String appSecret;

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getOssHost() {
        return ossHost;
    }

    public void setOssHost(String ossHost) {
        this.ossHost = ossHost;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getSessionDomain() {
        return sessionDomain;
    }

    public void setSessionDomain(String sessionDomain) {
        this.sessionDomain = sessionDomain;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAuthDomain() {
        return authDomain;
    }

    public void setAuthDomain(String authDomain) {
        this.authDomain = authDomain;
    }
}
