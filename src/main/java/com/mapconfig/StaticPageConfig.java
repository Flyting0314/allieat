package com.mapconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticPageConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/checkout").setViewName("forward:/checkout.html");
        registry.addViewController("/cart").setViewName("forward:/cart.html");
        registry.addViewController("/orderfood").setViewName("forward:/orderfood.html");
        registry.addViewController("/orderattached").setViewName("forward:/orderattached.html");
    }
}
