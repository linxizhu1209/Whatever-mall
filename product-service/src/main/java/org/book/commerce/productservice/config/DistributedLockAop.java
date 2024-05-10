package org.book.commerce.productservice.config;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.book.commerce.common.exception.ConflictException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;

@Aspect
@Slf4j
@Component
@Order(value = 1)
@RequiredArgsConstructor
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";
    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(org.book.commerce.productservice.config.DistributedLock)")
    public Object
    lock(final ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(),joinPoint.getArgs(),distributedLock.key());
        RLock rLock = redissonClient.getLock(key);

        log.info("{} - 락 획득 시도",key);
        try{
            boolean available = rLock.tryLock(distributedLock.waitTime(),distributedLock.leaseTime(),distributedLock.timeUnit());
            if(!available){
                log.info("{} - 락 획득 실패",key);
                throw new ConflictException("락 획득이 실패했습니다!");
            }
            log.info("{} - 락 획득 성공",key);
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try{
                log.info("{} - 락 반납",key);
                rLock.unlock();
            } catch (IllegalMonitorStateException e){
                log.info("이미 반납된 키 입니다.");
            }
        }
    }
}
