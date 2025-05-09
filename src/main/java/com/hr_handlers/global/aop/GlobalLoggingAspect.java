package com.hr_handlers.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class GlobalLoggingAspect {

    @Pointcut("execution(* com.hr_handlers..*(..))")
    private void globalPointcut(){}

    @Around("globalPointcut()")
    public Object methodLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().toShortString();
        Object[] args = proceedingJoinPoint.getArgs();
        StopWatch stopWatch = new StopWatch();
        try {
//            log.info("[실행 메서드]: {} [매개변수]: {}", methodName, Arrays.toString(args));
            log.info("[실행 메서드]: {}", methodName);
            stopWatch.start();
            Object result = proceedingJoinPoint.proceed();
//            log.info("[종료 메서드]: {} [반환값]: {}", methodName, result);
            log.info("[종료 메서드]: {}", methodName);
            return result;
        } catch (Throwable ex) {
            log.error("[예외] 메서드: {} | 예외 메시지: {}", methodName, ex.getMessage(), ex);
            throw ex;
        } finally {
            stopWatch.stop();
            log.info("[실행 시간]: {}", stopWatch.getTotalTimeSeconds());
        }
    }
}
