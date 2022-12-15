package com.luxhouse.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.luxhouse.main.interceptor.SessionInterceptor;

@Configuration
public class SessionInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    SessionInterceptor sessionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**");
    }
}
