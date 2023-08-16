package com.paystub.service;

import com.paystub.dto.PageDto;
import com.paystub.dto.UserFormDto;
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

    public String getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
        
    }

    // 메서드 레벨의 트랜잭션 선언이 우선 적용되기 때문
    @Transactional(readOnly = true)
    public List<UserFormDto> totalDataService(Integer year, Integer month) {
        List<UserFormDto> totalData = new ArrayList<>();
        totalData = userMapper.getTotalData(getCurrentMember(), year, month);
        return totalData;
    }

    public List<PageDto> getPage(Integer year, Integer limit,  Integer offset) {

        System.out.println("year = " + year);

        List<PageDto> getPageData = new ArrayList<>();

        if(year == 9999) {
            getPageData = userMapper.findByTotal(getCurrentMember(), limit, offset);
        }
        else {
            getPageData = userMapper.findByYear(getCurrentMember(), year, limit, offset);
        }
        System.out.println("offset = " + offset);

        for (PageDto getPageDatum : getPageData) {
            System.out.println("getPageDatum.getYear() = " + getPageDatum.getYear());
            System.out.println("getPageDatum.getMonth() = " + getPageDatum.getMonth());
            
        }

        return getPageData;
    }

    public int getTotalRecords(Integer year) {
        int count = 0;
        if(year == 9999) {
            count = userMapper.countTotal(getCurrentMember());
        }
        else {
            count = userMapper.countYear(getCurrentMember(), year);
        }
        System.out.println("count = " + count);
        return count;
    }

}
