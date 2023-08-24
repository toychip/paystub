package com.paystub.user.service;

import com.paystub.comon.util.PageUtil;
import com.paystub.user.dto.response.UserResponse;
import com.paystub.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;

    // 로그인한 사용자의 객체를 받아오는 메서드
    public String getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();

    }

    // 메서드 레벨의 트랜잭션 선언이 우선 적용되기 때문
    @Transactional(readOnly = true)
    public List<UserResponse> getTotalData(Integer year, Integer month) {
        return userMapper.getTotalData(getCurrentMember(), year, month);

    }

    public List<PageUtil> getPage(Integer year, Integer month, Integer limit, Integer offset) {
        List<PageUtil> PageData = new ArrayList<>();

        // 만약 사용자가 year "전체", month "전체" 옵션을 선택했을 시
        if(year == 9999 && month == 0) {
            PageData = userMapper.findByTotal(getCurrentMember(), limit, offset);
        }

        // 만약 사용자가 year "전체", 특정 월 옵션을 선택했을 시
        else if(year == 9999 && month != 0) {
            PageData = userMapper.findByMonth(getCurrentMember(), month, limit, offset);
        }

        // 만약 사용자가 특정 년, month "전체" 옵션을 선택했을 시
        else if (year != 9999 && month == 0) {
            PageData = userMapper.findByYear(getCurrentMember(), year, limit, offset);
        }

        else if (year != 9999 && month != 0) {
            PageData = userMapper.findByYearAndMonth(getCurrentMember(), year, month, limit, offset);
        }

        return PageData;
    }

    public int getTotalRecords(Integer year, Integer month) {
        // count 초기화
        int count = 0;

        if(year == 9999 && month == 0) {
            count = userMapper.getCountTotal(getCurrentMember());
        }

        else if(year == 9999 && month != 0) {
            count = userMapper.getCountMonth(getCurrentMember(), month);
        }
        else if (year != 9999 && month == 0) {
            count = userMapper.getCountYear(getCurrentMember(), year);
        }

        else {
            count = userMapper.getCountYearAndMonth(getCurrentMember(), year, month);
        }

        return count;
    }

}
