package com.paystub.service;

import com.paystub.dto.PageDto;
import com.paystub.dto.response.UserResponse;
import com.paystub.repository.UserMapper;
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
    public List<UserResponse> totalDataService(Integer year, Integer month) {
        return userMapper.getTotalData(getCurrentMember(), year, month);

    }

    public List<PageDto> getPage(Integer year, Integer month, Integer limit,  Integer offset) {
        List<PageDto> getPageData = new ArrayList<>();

        // 만약 사용자가 year "전체", month "전체" 옵션을 선택했을 시
        if(year == 9999 && month == 0) {
            getPageData = userMapper.findByTotal(getCurrentMember(), limit, offset);
        }

        // 만약 사용자가 year "전체", 특정 월 옵션을 선택했을 시
        else if(year == 9999 && month != 0) {
            getPageData = userMapper.findByMonth(getCurrentMember(), month, limit, offset);
        }

        // 만약 사용자가 특정 년, month "전체" 옵션을 선택했을 시
        else if (year != 9999 && month == 0) {
            getPageData = userMapper.findByYear(getCurrentMember(), year, limit, offset);
        }

        return getPageData;
    }

    public int getTotalRecords(Integer year, Integer month) {
        // count 초기화
        int count = 0;

        if(year == 9999 && month == 0) {
            count = userMapper.countTotal(getCurrentMember());
        }

        else if(year == 9999 && month != 0) {
            count = userMapper.countMonth(getCurrentMember(), month);
        }
        else {
            count = userMapper.countYear(getCurrentMember(), year);
        }
        return count;
    }

}
