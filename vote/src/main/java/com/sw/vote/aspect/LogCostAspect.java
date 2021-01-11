package com.sw.vote.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


@Aspect
@Component
public class LogCostAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@annotation(logCost)")
    public void logExecutionTime(LogCost logCost) {
    }


    @Around(value = "logExecutionTime(logCost)", argNames = "proceedingJoinPoint,logCost")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, LogCost logCost) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object proceed = proceedingJoinPoint.proceed();
        stopWatch.stop();
        logger.info("[{}]:耗时{}ms", logCost.name(),
                stopWatch.getTotalTimeMillis());
        return proceed;
    }
}