package com.paystub.repository;


import com.paystub.dto.LoginFormDto;
import com.paystub.dto.ResponseDto;
import com.paystub.dto.UserDto;
import com.paystub.dto.UserFormDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;


@Mapper
public interface UserMapper {
    void insertUser(UserDto userDto);


    List<ResponseDto> findJoinedDataByYearAndMonth(@Param("year") Long year, @Param("month") Long month);

    Optional<LoginFormDto> findByUsername(String username);

   List<UserFormDto> getTotalData(String employeeID);

   Optional<UserDto> findByEmployeeIDAndName(@Param("EmployeeID") Integer EmployeeID, @Param("Name") String Name);

   Optional<UserDto> findByEmployeeID(@Param("EmployeeID") Integer EmployeeID);


}