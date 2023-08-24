package com.paystub.comon.util;

import com.paystub.comon.util.AESUtilUtil;
import com.paystub.user.dto.UserDao;
import com.paystub.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;

//@Component
@RequiredArgsConstructor
public class SuperAdminAccountInit {
    private final UserMapper userMapper;
    private final AESUtilUtil aesUtilUtil;

//    @PostConstruct
    public void init() {

        String birthday = "11111111";
        String socialNumber = aesUtilUtil.encrypt(birthday);

        UserDao admin = UserDao.builder()
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
