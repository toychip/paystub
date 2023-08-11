package com.paystub.repository;


import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeSalaryMapper {
    void insertEmployeeSalaryDto(EmployeeSalaryDto employeeSalaryDto);
    EmployeeSalaryDto findSalaryByYearMonthAndEmployeeID(
            @Param("year") int year,
            @Param("month") int month,
            @Param("EmployeeID") Integer EmployeeID
    );

    List<EmployeeSalaryDto> findAllSalaries(); // 모든 급여 정보 반환
}
