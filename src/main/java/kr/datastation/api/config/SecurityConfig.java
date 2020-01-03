package kr.datastation.api.config;


import kr.datastation.api.security.CustomUserDetailsService;
import kr.datastation.api.security.JwtAuthenticationEntryPoint;
import kr.datastation.api.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationEntryPoint unauthorizedHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .csrf()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                // specific -> general
                .authorizeRequests()
                    .antMatchers("calendar/calendar/download").authenticated()
                    .antMatchers("/news/entity/download").authenticated()
                    .antMatchers("/news/related/download").authenticated()
                    .antMatchers("/news/timeline/download").authenticated()
                    .antMatchers("/news/topic/download").authenticated()
                    .antMatchers("/news/topic/rank/download").authenticated()
                    .antMatchers("/**").permitAll();
//                    .antMatchers("/weather/*/download").authenticated()
//                    .antMatchers("/weather/download").authenticated()
//                    .antMatchers("/**", "/weather/spss/download").permitAll();
//                    .antMatchers("/v2/api-docs",
//                            "/configuration/ui",
//                            "/swagger-resources/**",
//                            "/configuration/security",
//                            "/swagger-ui.html",
//                            "/webjars/**")
//                        .permitAll();
//                    .antMatchers("/**").authenticated()
//                    .antMatchers("/",
//                        "/favicon.ico",
//                        "/**/*.png",
//                        "/**/*.gif",
//                        "/**/*.svg",
//                        "/**/*.jpg",
//                        "/**/*.html",
//                        "/**/*.css",
//                        "/**/*.js")
//                        .permitAll()
//                    .antMatchers("/api/auth/**")
//                        .permitAll()
//                    .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
//                        .permitAll()
//                    .antMatchers(HttpMethod.GET, "/weather/**", "/api/users/**")
//                        .permitAll()
//                    .anyRequest()
//                        .authenticated();

        // Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}