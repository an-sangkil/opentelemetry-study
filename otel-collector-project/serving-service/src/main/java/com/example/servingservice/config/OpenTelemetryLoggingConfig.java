package com.example.servingservice.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class OpenTelemetryLoggingConfig implements InitializingBean {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenTelemetryLoggingConfig.class);
    
    private final OpenTelemetry openTelemetry;
    
    public OpenTelemetryLoggingConfig(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
        logger.info("✅ OpenTelemetryLoggingConfig 생성됨, OpenTelemetry instance: {}", openTelemetry);
    }
    
    @Override
    public void afterPropertiesSet() {
        try {
            logger.info("🔥 OpenTelemetryAppender.install() 호출 시작");
            OpenTelemetryAppender.install(this.openTelemetry);
            logger.info("✅ OpenTelemetryAppender.install() 성공!");
            
            // 테스트 로그 전송
            logger.info("📤 이것은 OpenTelemetry로 전송될 테스트 로그입니다!");
            logger.warn("⚠️ 이것은 WARNING 레벨 테스트 로그입니다!");
            logger.error("❌ 이것은 ERROR 레벨 테스트 로그입니다!");
            
        } catch (Exception e) {
            logger.error("💥 OpenTelemetryAppender.install() 실패: {}", e.getMessage(), e);
        }
    }
}