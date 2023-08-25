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

    // 년, 월, 직원 ID로 급여 정보 찾기
    EmployeeSalaryDao findSalaryByYearMonthAndEmployeeID(
            @Param("year") int year,
            @Param("month") int month,
            @Param("EmployeeID") Integer EmployeeID
    );

    // 년, 월, 이름, 직원 ID로 조인된 데이터 찾기
    List<AdminSalaryResponse> findJoinedDataByYearAndMonth(
            @Param("year") Long year,
            @Param("month") Long month,
            @Param("name") String name,
            @Param("employeeID") Long employeeId
    );

    // 사용자 정보 삽입
    void insertUser(UserDao userDao);

    // 관리자 유저 정보 조회
    List<UserDao> findByAdminUser();

    // 직원 급여 정보 삭제 (여러 개)
    void deleteEmployeeSalaryByIds(List<Long> employeeIds);

    // 사용자 정보 삭제 (여러 개)
    void deleteUsersByIds(List<Long> employeeIds);

    // 직원 급여 정보 삭제 (특정 년/월)
    void deleteEmployeeSalaryById(@Param("employeeId") Long employeeId,
                                  @Param("year") Long year, @Param("month") Long month);

    // 직원 ID와 이름으로 사용자 찾기
    Optional<UserDao> findByEmployeeIDAndName(@Param("EmployeeID") Integer EmployeeID, @Param("Name") String Name);

    // 직원 ID로 사용자 찾기
    Optional<UserDao> findByEmployeeID(@Param("EmployeeID") Integer EmployeeID);
}
