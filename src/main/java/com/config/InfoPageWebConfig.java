package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.frontcontroller.InfoPageInterceptor;
@Configuration
public class InfoPageWebConfig implements WebMvcConfigurer{
	@Autowired
    private InfoPageInterceptor infoPageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	
        
    	registry.addInterceptor(infoPageInterceptor)
                .addPathPatterns("/registerAndLogin/storeInfo","/registerAndLogin/memberInfo","/map2");
    }
}
