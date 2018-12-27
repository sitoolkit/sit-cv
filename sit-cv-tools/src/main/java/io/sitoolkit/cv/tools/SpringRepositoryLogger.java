package io.sitoolkit.cv.tools;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SpringRepositoryLogger {

    @PostConstruct
    public void init() {
        System.out.println("Initialized " + this.getClass());
    }

    @Around("@within(org.springframework.stereotype.Repository)")
    public Object proceed(ProceedingJoinPoint pjp) throws Throwable {

        System.out.println("[RepositoryMethod]" + pjp.getSignature());

        return pjp.proceed();
    }
}
