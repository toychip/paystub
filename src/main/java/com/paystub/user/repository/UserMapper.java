package com.paystub.user.repository;


import com.paystub.comon.dto.request.LoginRequest;
import com.paystub.comon.util.PageUtil;
import com.paystub.user.dto.response.UserResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;


@Mapper
public interface UserMapper {

    Optional<LoginRequest> findByUsername(String username);

    // EmployeeSalary와 User 테이블을 조인하여 사용자의 해당 년, 월 데이터를 가져온다.
    List<UserResponse> getTotalData(String employeeID, Integer year, Integer month);



    // 조건 없이 사용자의 전체 년, 월 데이터를 가져온다.
    List<PageUtil> findByTotal(String employeeID, Integer limit, Integer offset);

    // 해당 년에 맞는 사용자의 년, 월 데이터를 가져온다.
    List<PageUtil> findByYear(String employeeID, Integer year, Integer limit, Integer offset);

    // 해당 월에 맞는 사용자의 년, 월 데이터를 가져온다.
    List<PageUtil> findByMonth(String employeeID, Integer month, Integer limit, Integer offset);

    // 해당 년, 월에 맞는 사용자의 년, 월 데이터를 가져온다.
    List<PageUtil> findByYearAndMonth(String employeeID, Integer year, Integer month, Integer limit, Integer offset);

    // 조건 없이 사용자의 전체 데이터 갯수를 가져온다.
    int getCountTotal(String employeeID);

    // 해당 년에 맞는 사용자의 데이터 갯수를 가져온다.
    int getCountYear(String employeeID, Integer year);

    // 해당 월에 맞는 사용자의 데이터 갯수를 가져온다.
    int getCountMonth(String employeeID, Integer month);

    // 해당 년, 월에 맞는 사용자의 데이터 갯수를 가져온다.
    int getCountYearAndMonth(String employeeID, Integer year, Integer month);

}