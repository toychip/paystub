package com.paystub.repository;


import com.paystub.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;


@Mapper
public interface UserMapper {
    void insertUser(UserDto userDto);


    List<ResponseDto> findJoinedDataByYearAndMonth(@Param("year") Long year, @Param("month") Long month);

    Optional<LoginFormDto> findByUsername(String username);

    // EmployeeSalary와 User 테이블을 조인하여 사용자의 해당 년, 월 데이터를 가져온다.
   List<UserFormDto> getTotalData(String employeeID, Integer year, Integer month);

   Optional<UserDto> findByEmployeeIDAndName(@Param("EmployeeID") Integer EmployeeID, @Param("Name") String Name);

   Optional<UserDto> findByEmployeeID(@Param("EmployeeID") Integer EmployeeID);

   // 조건 없이 사용자의 전체 년, 월 데이터를 가져온다.
   List<PageDto> findByTotal(String employeeID, Integer limit, Integer offset);

   // 해당 년에 맞는 사용자의 년, 월 데이터를 가져온다.
   List<PageDto> findByYear(String employeeID, Integer year, Integer limit, Integer offset);

   // 조건 없이 사용자의 전체 데이터 갯수를 가져온다.
   int countTotal(String employeeID);

   // 해당 년에 맞는 사용자의 데이터 갯수를 가져온다.
   int countYear(String employeeID, Integer year);


}