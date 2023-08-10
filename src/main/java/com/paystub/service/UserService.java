package com.paystub.service;

import com.paystub.dto.ResponseDto;
import com.paystub.dto.UserFormDto;
import com.paystub.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.mysql.cj.conf.PropertyKey.logger;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;


    // 메서드 레벨의 트랜잭션 선언이 우선 적용되기 때문
    @Transactional(readOnly = true)
    public List<UserFormDto> totalDataService() {
        List<UserFormDto> totalData = new ArrayList<>();
        totalData = userMapper.getTotalData();
//        System.out.println(totalData);
//        for(UserFormDto userFormDto : totalData) {
//            log.info("Total data: {}", responseDto.toString());
//        }
        return totalData;
    }
}
