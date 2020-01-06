// https://stackoverflow.com/questions/36744678/spring-boot-swagger-2-ui-custom-requestmappinghandlermapping-mapping-issue
//package kr.datastation.api.config;
//
//import kr.datastation.api.advice.RequestLoggingAspect;
//import kr.datastation.api.security.JwtTokenProvider;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//@Configuration
//@EnableAspectJAutoProxy(proxyTargetClass=true)
//@ComponentScan(basePackages = {"kr.datastation.api.advice"})
//public class AspectConfig extends WebMvcConfigurationSupport {
//
//    @Bean
//    public JwtTokenProvider jwtTokenProvider() {
//        return new JwtTokenProvider();
//    }
//
//    @Bean
//    public RequestLoggingAspect requestLoggingAspect() {
//        return new RequestLoggingAspect(jwtTokenProvider());
//    }
//}
