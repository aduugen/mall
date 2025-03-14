package com.macro.mall.portal.component;

import com.macro.mall.portal.component.VisitorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer { 
       @Autowired
       private VisitorInterceptor visitorInterceptor;
      
       @Override
       public void addInterceptors(InterceptorRegistry registry) {        
            registry.addInterceptor(visitorInterceptor)
                    .addPathPatterns("/home/content");
       }
}
