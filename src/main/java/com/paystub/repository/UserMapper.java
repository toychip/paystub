package com.paystub.repository;


import com.paystub.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sun.jvm.hotspot.debugger.Page;

import java.util.List;
import java.util.Optional;


@Mapper
public interface UserMapper {
    void insertUser(UserDto userDto);


    List<ResponseDto> findJoinedDataByYearAndMonth(@Param("year") Long year, @Param("month") Long month);

    Optional<LoginFormDto> findByUsername(String username);

   List<UserFormDto> getTotalData(String employeeID, Integer year, Integer month);

   Optional<UserDto> findByEmployeeIDAndName(@Param("EmployeeID") Integer EmployeeID, @Param("Name") String Name);

   Optional<UserDto> findByEmployeeID(@Param("EmployeeID") Integer EmployeeID);

   List<PageDto> findByTotal(String employeeID, Integer limit, Integer offset);

   List<PageDto> findByYear(String employeeID, Integer year, Integer limit, Integer offset);


    int countYear(String employeeID, Integer year);

    int countTotal(String employeeID);

}