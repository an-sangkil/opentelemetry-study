package com.ad.adrepository.config;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 8. 2.
 */
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TracingAspect {

    private final Tracer tracer;

    public TracingAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around("@annotation(Traced)")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        // 현재 활성화된 부모 스팬을 가져옵니다.
        Span parentSpan = Span.current();
        if (parentSpan.getSpanContext().isValid()) {
            // 이미 유효한 스팬이 존재하면, 해당 스팬을 사용하여 새로운 스팬을 생성
            Span span = tracer.spanBuilder(methodName)
                    .setParent(Context.current().with(parentSpan))
                    .startSpan();

            try (Scope scope = span.makeCurrent()) {
                return joinPoint.proceed();
            } catch (Throwable t) {
                span.recordException(t);
                throw t;
            } finally {
                span.end();
            }
        } else {
            // 유효한 스팬이 없으면 기존 로직대로 새로운 스팬을 생성
            return joinPoint.proceed();
        }
    }
}