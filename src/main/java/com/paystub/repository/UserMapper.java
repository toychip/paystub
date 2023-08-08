package com.paystub.repository;


import com.paystub.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insertUser(UserDto userDto);
}
