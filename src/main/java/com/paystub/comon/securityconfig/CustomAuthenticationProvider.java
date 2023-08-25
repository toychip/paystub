package com.paystub.comon.securityconfig;

import com.paystub.comon.dto.request.LoginRequest;
import com.paystub.comon.util.AESUtilUtil;
import com.paystub.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AESUtilUtil aesUtilUtil; // 암호화 유틸리티
    private final UserMapper userMapper; // 사용자 정보를 조회하기 위한 매퍼

    // 사용자 인증을 처리하는 메서드
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();  // 사용자로부터 입력받은 아이디
        String providedPassword = (String) authentication.getCredentials(); // 사용자로부터 입력받은 비밀번호

        String aes256Password = aesUtilUtil.encrypt(providedPassword);
        Optional<LoginRequest> loginFormDtoOptional = userMapper.findByUsername(username);

        if (loginFormDtoOptional.isPresent()) {  // 사용자 정보가 존재하는 경우
            LoginRequest loginRequest = loginFormDtoOptional.get();

            if (loginRequest.getPassword().equals(aes256Password)) {    // 비밀번호가 일치하는 경우
                String role = loginRequest.getRole() == 2 ? "ROLE_ADMIN" : "ROLE_USER"; // 역할에 따른 권한 부여

                // 인증 토큰 생성 및 반환
                return new UsernamePasswordAuthenticationToken(
                        new User(username, providedPassword, Collections.singletonList(new SimpleGrantedAuthority(role))),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
            } else {
                // 비밀번호가 일치하지 않는 경우 예외 발생
                throw new BadCredentialsException("Authentication failed");
            }
        } else {
            // 사용자 정보가 존재하지 않는 경우 예외 발생
            throw new BadCredentialsException("Authentication failed");
        }

    }

    // UsernamePasswordAuthenticationToken 클래스를 지원하는지 여부를 반환
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }


}
