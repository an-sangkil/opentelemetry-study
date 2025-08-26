package com.example;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 순수 OpenTelemetry를 사용한 Campaign Controller
 * - 수동 트레이스 계측
 * - 커스텀 메트릭 생성
 * - 구조화된 로깅
 *
 * @author yohan.an
 * @version Copyright (C) 2025 by KakaoHealthcare. All right reserved.
 * @since 2025. 8. 6.
 */
@Slf4j
@RestController
public class CampaignController {

    private final Tracer tracer;
    private final LongCounter campaignCreateCounter;

    public CampaignController(Tracer tracer, Meter meter) {

        this.tracer = tracer;
        this.campaignCreateCounter = meter
            .counterBuilder("campaign_create_requests_total")
            .setDescription("Total number of campaign create requests")
            .build();
    }

    /**
     * 수동 트레이싱 예제 - 직접 스팬 생성/관리
     */
    @GetMapping("/api/campaign/create")
    public String campaignCreate() {
        // 수동 스팬 생성
        Span span = tracer.spanBuilder("campaign_create")
            .setSpanKind(io.opentelemetry.api.trace.SpanKind.SERVER)
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // 스팬 속성 추가
            span.setAttribute("campaign.operation", "create");
            span.setAttribute("http.method", "GET");

            // 메트릭 카운터 증가
            campaignCreateCounter.add(1);

            // 구조화된 로깅 (traceId, spanId 자동 포함)
            log.info("Processing campaign creation request");

            // 비즈니스 로직 시뮬레이션
            Thread.sleep(50); // 50ms 지연

            log.info("Campaign creation completed successfully");

            return "success";
        } catch (Exception e) {
            // 에러 처리
            span.recordException(e);
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
            log.error("Campaign creation failed", e);
            throw new RuntimeException(e);
        } finally {
            span.end();
        }
    }

    /**
     * Spring Boot 자동 트레이싱 예제 - 별도 어노테이션 없이 자동 계측
     */
    @GetMapping("/api/campaign/list")
    public String campaignList() throws InterruptedException {
        log.info("Fetching campaign list");
        
        // 비즈니스 로직 시뮬레이션
        Thread.sleep(30);
        
        log.info("Campaign list retrieved successfully");
        return "campaign-list-data";
    }
}
