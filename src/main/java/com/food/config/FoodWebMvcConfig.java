package com.food.config;

import com.food.interceptor.StorePermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FoodWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new StorePermissionInterceptor())
                .addPathPatterns("/registerAndLogin/storeInfo/*/food/**"); // 要攔的路徑
    }
}
