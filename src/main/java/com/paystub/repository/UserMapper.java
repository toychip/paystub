package com.paystub.repository;


import com.paystub.dto.*;
import com.paystub.dto.request.LoginRequest;
import com.paystub.dto.response.AdminSalaryResponse;
import com.paystub.dto.AdminUserListResponseAndUserSaveDao;
import com.paystub.dto.response.UserResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sun.jvm.hotspot.debugger.Page;

import java.util.List;
import java.util.Optional;


@Mapper
public interface UserMapper {
    void insertUser(AdminUserListResponseAndUserSaveDao adminUserListResponseAndUserSaveDao);


    //    List<ResponseDto> findJoinedDataByYearAndMonth(@Param("year") Long year, @Param("month") Long month);
    List<AdminSalaryResponse> findJoinedDataByYearAndMonth(
            @Param("year") Long year,
            @Param("month") Long month,
            @Param("name") String name,
            @Param("employeeID") Long employeeId
    );

    Optional<LoginRequest> findByUsername(String username);

    // EmployeeSalary와 User 테이블을 조인하여 사용자의 해당 년, 월 데이터를 가져온다.
    List<UserResponse> getTotalData(String employeeID, Integer year, Integer month);

    Optional<AdminUserListResponseAndUserSaveDao> findByEmployeeIDAndName(@Param("EmployeeID") Integer EmployeeID, @Param("Name") String Name);

    Optional<AdminUserListResponseAndUserSaveDao> findByEmployeeID(@Param("EmployeeID") Integer EmployeeID);

    // 조건 없이 사용자의 전체 년, 월 데이터를 가져온다.
    List<PageDto> findByTotal(String employeeID, Integer limit, Integer offset);

    // 해당 년에 맞는 사용자의 년, 월 데이터를 가져온다.
    List<PageDto> findByYear(String employeeID, Integer year, Integer limit, Integer offset);

    // 해당 월에 맞는 사용자의 년, 월 데이터를 가져온다.
    List<PageDto> findByMonth(String employeeID, Integer month, Integer limit, Integer offset);

    // 해당 년, 월에 맞는 사용자의 년, 월 데이터를 가져온다.
    List<PageDto> findByYearAndMonth(String employeeID, Integer year, Integer month, Integer limit, Integer offset);

    // 조건 없이 사용자의 전체 데이터 갯수를 가져온다.
    int getCountTotal(String employeeID);

    // 해당 년에 맞는 사용자의 데이터 갯수를 가져온다.
    int getCountYear(String employeeID, Integer year);

    // 해당 월에 맞는 사용자의 데이터 갯수를 가져온다.
    int getCountMonth(String employeeID, Integer month);

    // 해당 년, 월에 맞는 사용자의 데이터 갯수를 가져온다.
    int getCountYearAndMonth(String employeeID, Integer year, Integer month);

    List<AdminUserListResponseAndUserSaveDao> findByAdminUser();

    void deleteEmployeeSalaryByIds(List<Long> employeeIds);

    void deleteUsersByIds(List<Long> employeeIds);

    void deleteEmployeeSalaryById(@Param("employeeId") Long employeeId,
                                  @Param("year") Long year, @Param("month") Long month);
}