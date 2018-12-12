package com.mini_video;

import com.mini_video.intecepter.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

//	@Bean(initMethod="init")
//	public ZKCuratorClient zkCuratorClient() {
//		return new ZKCuratorClient();
//	}

    @Bean
    public MiniInterceptor miniInterceptor() {
        return new MiniInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

//        registry.addInterceptor(miniInterceptor()).addPathPatterns("/miniVideo/user/**")
//                .addPathPatterns("/miniVideo/video/upload", "/miniVideo/video/uploadCover",
//                        "/miniVideo/video/userLike", "/miniVideo/video/userUnLike",
//                        "/miniVideo/video/saveComment")
//                .addPathPatterns("/miniVideo/bgm/**")
//                .excludePathPatterns("/miniVideo/user/queryPublisher", "/miniVideo/user/regist",
//                        "/miniVideo/user/login", "/miniVideo/user/onLogin",
//                        "/miniVideo/user/mini/userInfoDetail");
//
//        super.addInterceptors(registry);
    }

}
