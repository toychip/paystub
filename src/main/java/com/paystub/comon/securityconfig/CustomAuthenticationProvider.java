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

    private final AESUtilUtil aesUtilUtil;
    private final UserMapper userMapper;

    // TODO 비밀번호 정책 어떻게할지
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String providedPassword = (String) authentication.getCredentials();

        String aes256Password = aesUtilUtil.encrypt(providedPassword);
        Optional<LoginRequest> loginFormDtoOptional = userMapper.findByUsername(username);
        if (loginFormDtoOptional.isPresent()) {
            LoginRequest loginRequest = loginFormDtoOptional.get();
            if (loginRequest.getPassword().equals(aes256Password)) {
                String role = loginRequest.getRole() == 2 ? "ROLE_ADMIN" : "ROLE_USER";

                return new UsernamePasswordAuthenticationToken(
                        new User(username, providedPassword, Collections.singletonList(new SimpleGrantedAuthority(role))),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
            } else {
                throw new BadCredentialsException("Authentication failed");
            }
        } else {
            throw new BadCredentialsException("Authentication failed");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }


}