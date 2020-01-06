package kr.datastation.api.advice;

import kr.datastation.api.security.JwtTokenProvider;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;


@Aspect
public class RequestLoggingAspect {

    private static final Logger userLogger = LoggerFactory.getLogger("UserLogger");

    final JwtTokenProvider jwtTokenProvider;

    @Autowired()
    private HttpServletRequest request;

    @Autowired
    public RequestLoggingAspect(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Pointcut("execution(* kr.datastation.api.controller.*.*(..))")
    public void controller() {
    }

    @Before("controller()")
    public void logBefore(JoinPoint joinPoint) {
        String token = request.getHeader("Authorization");
        if (null != token) {
            String key = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.getUserIdFromJWT(key);
            String methodName = joinPoint.getSignature().toShortString();
            userLogger.info(userId + " " + methodName);
        }
    }
}
