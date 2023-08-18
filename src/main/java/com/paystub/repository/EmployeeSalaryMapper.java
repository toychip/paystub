package com.paystub.repository;


import com.paystub.dto.EmployeeSalaryDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeSalaryMapper {
    void insertEmployeeSalaryDto(EmployeeSalaryDao employeeSalaryDao);
    EmployeeSalaryDao findSalaryByYearMonthAndEmployeeID(
            @Param("year") int year,
            @Param("month") int month,
            @Param("EmployeeID") Integer EmployeeID
    );

}
