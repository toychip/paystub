package com.paystub.comon.securityconfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpSession;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return http
                .csrf().disable()       // CSRF 공격 방어 비활성화
                .formLogin()            // 폼 기반 로그인 사용
                .loginPage("/login")    // 로그인 페이지 URL 설정
                .loginProcessingUrl("/login")    // 로그인 처리 URL 설정
                .successHandler(successHandler)  // 로그인 성공시 핸들러 지정
                .failureHandler(failureHandler)  // 로그인 실패시 핸들러 지정
                .and()
                .authorizeHttpRequests()                            // URL별 권한 설정 시작
                .antMatchers("/login").permitAll()                // 로그인 페이지는 누구나 접근 가능
                .antMatchers("/adminSalary").hasRole("ADMIN")     // ADMIN 권한만 접근 가능한 URL 설정
                .antMatchers("/adminDefaultSuccess").hasRole("ADMIN")     // ADMIN 권한만 접근 가능한 URL 설정
                .antMatchers("/adminUserManagement").hasRole("ADMIN")     // ADMIN 권한만 접근 가능한 URL 설정

                .antMatchers("/userPaging").hasRole("USER")  // USER 권한만 접근 가능한 URL 설정
                .antMatchers("/userSalary").hasRole("USER")  // USER 권한만 접근 가능한 URL 설정
                .antMatchers("/afterPaging").hasRole("USER")  // USER 권한만 접근 가능한 URL 설정
                .antMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated()   // 나머지 요청은 인증된 사용자만 접근 가능
                .and()
                .sessionManagement()     // 세션 관리 설정
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)   // 필요시 세션 생성
                .invalidSessionUrl("/login")    // 무효한 세션일 경우 리다이렉트 될 URL
                .maximumSessions(1)             // 최대 세션 수 설정
                .expiredUrl("/login")           // 세션 만료시 리다이렉트 될 URL
                .and()
                .sessionFixation().newSession() // 세션 고정 공격 방지 설정
                .and()
                .authenticationProvider(customAuthenticationProvider)   // 커스텀 인증 프로바이더 사용 설정
                .logout() // Logout 설정
                .logoutUrl("/logout")   // 로그아웃 처리 URL
                .logoutSuccessUrl("/login")  // 로그아웃 성공시 리다이렉트 될 URL
                .addLogoutHandler((request, response, authentication) -> {
                    HttpSession session = request.getSession();
                    if (session != null) {
                        session.invalidate();
                    }
                })
                .deleteCookies("JSESSIONID")    // 로그아웃시 JSESSIONID 쿠키 삭제
                .and()
                .build();
    }
}
