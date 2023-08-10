package com.paystub.repository;


import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeSalaryMapper {
    void insertEmployeeSalaryDto(EmployeeSalaryDto employeeSalaryDto);
    List<EmployeeSalaryDto> findAllSalaries(); // 모든 급여 정보 반환
}
