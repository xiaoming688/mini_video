package com.mini_video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
//@ComponentScan(basePackages = {"com.inabook"})
public class VideoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoApiApplication.class, args);
	}
}
