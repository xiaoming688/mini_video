package com.mini_video.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Canyon
 * @date Created in 16:40 2018/1/5
 * 小程序接口服务组件
 */
@Component
public class MpWxMiniProgram {

    private final static Log log = LogFactory.getLog(MpWxMiniProgram.class);

    public JSONObject getLoginInfo(String appid, String secret, String jsCode, String grantType) {
        try {
            Http http = new Http("https://api.weixin.qq.com/sns/jscode2session");
            http.addParam("appid", appid);
            http.addParam("secret", secret);
            http.addParam("js_code", jsCode);
            http.addParam("grant_type", grantType);
            return http.doGet().toJsonObject();
        } catch (Exception e) {
            log.error("获取登录信息失败 :: " + e);
            throw new RuntimeException(e);
        }
    }

}
