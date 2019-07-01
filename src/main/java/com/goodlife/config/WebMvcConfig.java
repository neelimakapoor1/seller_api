package com.goodlife.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	@Value("${log.path}")
	private String logPath;

	@Value("${upload.path}")
	private String uploadPath;
	
	@Bean
	public RequestInterceptor requestInterceptor() {
		return new RequestInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestInterceptor());
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/logs/**")
          .addResourceLocations(new File(logPath).toURI().toString()); 
        
        registry
        .addResourceHandler("/uploads/**")
        .addResourceLocations(new File(uploadPath).toURI().toString()); 
    }
}