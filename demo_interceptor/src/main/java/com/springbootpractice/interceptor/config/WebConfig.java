package com.springbootpractice.interceptor.config;

import com.springbootpractice.interceptor.config.interceptor.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 说明：TODO
 * @author carter
 * 创建时间： 2020年02月19日 11:03 下午
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor myIntercepter = new MyInterceptor() ;
        registry.addInterceptor(myIntercepter).addPathPatterns("/**");
    }
}
