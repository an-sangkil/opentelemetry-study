package com.ad.adrepository.config;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 6. 28.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Add CORS configuration if needed
        registry.addMapping("/**");
    }


    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        // Add HTTP message codec configuration if needed
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Add formatters if needed
    }

    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        // Configure path matching options if needed
    }


}