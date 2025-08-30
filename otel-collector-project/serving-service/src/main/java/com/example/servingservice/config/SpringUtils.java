package com.example.servingservice.config;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


@Component
public class SpringUtils {

    @Value("${spring.application.name:default-service-name}")
    private String serviceName;

    @Value("${spring.profiles.active:default-profile}")
    private String activeProfile;


    public String getServiceName() {

        String serviceName = System.getProperty("spring.application.name");

        if (serviceName == null || serviceName.isEmpty()) {
            serviceName = this.serviceName;
        }

        return serviceName;

    }

    public String getActiveProfile() {

        String activeProfile = System.getProperty("spring.profiles.active");

        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        }

        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile= this.activeProfile;
        }


        return activeProfile;

    }
}
