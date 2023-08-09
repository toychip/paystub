package com.paystub.repository;


import com.paystub.dto.ResponseDto;
import com.paystub.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void insertUser(UserDto userDto);

    List<ResponseDto> getTotalData();
}
