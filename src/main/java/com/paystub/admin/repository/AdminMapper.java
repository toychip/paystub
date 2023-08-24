package com.paystub.admin.repository;


import com.paystub.admin.dto.EmployeeSalaryDao;
import com.paystub.admin.dto.response.AdminSalaryResponse;
import com.paystub.user.dto.UserDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AdminMapper {
    void insertEmployeeSalaryDto(EmployeeSalaryDao employeeSalaryDao);
    EmployeeSalaryDao findSalaryByYearMonthAndEmployeeID(
            @Param("year") int year,
            @Param("month") int month,
            @Param("EmployeeID") Integer EmployeeID
    );

    List<AdminSalaryResponse> findJoinedDataByYearAndMonth(
            @Param("year") Long year,
            @Param("month") Long month,
            @Param("name") String name,
            @Param("employeeID") Long employeeId
    );

    void insertUser(UserDao userDao);
    //

    List<UserDao> findByAdminUser();



    void deleteEmployeeSalaryByIds(List<Long> employeeIds);

    void deleteUsersByIds(List<Long> employeeIds);

    void deleteEmployeeSalaryById(@Param("employeeId") Long employeeId,
                                  @Param("year") Long year, @Param("month") Long month);


    Optional<UserDao> findByEmployeeIDAndName(@Param("EmployeeID") Integer EmployeeID, @Param("Name") String Name);

    Optional<UserDao> findByEmployeeID(@Param("EmployeeID") Integer EmployeeID);
}
