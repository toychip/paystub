package com.paystub.repository;


import com.paystub.dto.LoginFormDto;
import com.paystub.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;


@Mapper
public interface UserMapper {
    void insertUser(UserDto userDto);


    List<UserDto> findAllUsers();

    Optional<LoginFormDto> findByUsername(String username);

   List<UserFormDto> getTotalData();

}
