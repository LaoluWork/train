package com.laolu.train.business.config;

import com.laolu.train.common.interceptor.LogInterceptor;
import com.laolu.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LogInterceptor logInterceptor;

    @Resource
    MemberInterceptor memberInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**");

        // 排除的路径，不需要将context-path加上
        registry.addInterceptor(memberInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/hello"
                );

    }
}
