package com.macro.mall.portal.config;

import com.macro.mall.portal.component.VisitorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
       @Autowired
       private VisitorInterceptor visitorInterceptor;

       @Value("${upload.path}")
       private String uploadPath;

       @Override
       public void addInterceptors(InterceptorRegistry registry) {
              registry.addInterceptor(visitorInterceptor)
                            .addPathPatterns("/home/content");
       }

       @Override
       public void addResourceHandlers(ResourceHandlerRegistry registry) {
              // 添加外部文件夹映射，将/pics/**映射到物理路径
              registry.addResourceHandler("/pics/**")
                            .addResourceLocations("file:" + uploadPath + "/");

              // 打印日志，便于调试
              System.out.println("添加静态资源映射: /pics/** -> " + "file:" + uploadPath + "/");
       }
}
