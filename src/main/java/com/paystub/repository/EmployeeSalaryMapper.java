package com.paystub.repository;


import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeSalaryMapper {
    void insertEmployeeSalaryDto(EmployeeSalaryDto employeeSalaryDto);
}
