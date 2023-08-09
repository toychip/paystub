package com.paystub.service;

import com.paystub.dto.ResponseDto;
import com.paystub.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    // 메서드 레벨의 트랜잭션 선언이 우선 적용되기 때문
    @Transactional(readOnly = true)
    public List<ResponseDto> totalDataService() {
        return userMapper.getTotalData();
    }
}
