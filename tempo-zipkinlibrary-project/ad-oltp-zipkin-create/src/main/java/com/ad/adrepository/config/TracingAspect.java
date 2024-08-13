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
        //Span span = tracer.spanBuilder(methodName).startSpan();
        // 부모 스팬의 컨텍스트를 사용하여 새 스팬 생성
        Span parentSpan = Span.current();

        // 현재 스팬을 컨텍스트로 가져오기
        Context parentContext = Context.current();
        Span span = tracer.spanBuilder(methodName)
                .setParent(parentContext)
                .startSpan();



        try (var scope = span.makeCurrent()) {
            return joinPoint.proceed();
        } catch (Throwable t) {
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}