package com.backstage.backstageinterceptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    //設定攔截的範圍，目前僅開放/backStage與/backStage/login可以不帶有Token存取。
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/backStage/**")
                .excludePathPatterns("/backStage/login",
                                     "/backStage/**");
        
        
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/member/**")
                .addResourceLocations("file:upload/member/");
    }
}
    
 
