package com.paystub.config;

import com.paystub.dto.UserDto;
import com.paystub.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//@Component
@RequiredArgsConstructor
public class SuperAdminInit {
    private final UserMapper userMapper;
    private final AESUtilConfig aesUtilConfig;

//    @PostConstruct
    public void init() {

        String birthday = "11111111";
        String socialNumber = aesUtilConfig.encrypt(birthday);

        UserDto admin = UserDto.builder()
                .EmployeeID(998844)
                .Name("관리자")
                .State(1)
                .Role(2)
                .birthday(birthday)
                .SocialNumber(socialNumber)
                .EmailAddress("admin@nicednr.co.kr")
                .build();

        userMapper.insertUser(admin);
    }
}