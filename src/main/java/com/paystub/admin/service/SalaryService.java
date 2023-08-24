package com.paystub.admin.service;

import com.paystub.admin.dto.EmployeeSalaryDao;
import com.paystub.admin.dto.request.AdminDeleteSalaryRequest;
import com.paystub.admin.repository.EmployeeSalaryMapper;
import com.paystub.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryService {
    // 급여 명세서 저장 및 삭제 서비스

    private final UserMapper userMapper;
    private final EmployeeSalaryMapper employeeSalaryMapper;

    @Transactional
    public void saveEmployeeSalaries(List<EmployeeSalaryDao> employeeSalaryDaos, BindingResult bindingResult) {
        for (EmployeeSalaryDao employeeSalaryDao : employeeSalaryDaos) {
            EmployeeSalaryDao existingData = employeeSalaryMapper.findSalaryByYearMonthAndEmployeeID(
                    employeeSalaryDao.getYear(),
                    employeeSalaryDao.getMonth(),
                    employeeSalaryDao.getEmployeeID()
            );
            if (existingData != null) {
                FieldError error = new FieldError
                        ("employeeSalaryDto", "EmployeeID",
                                "이미 사번 [" + employeeSalaryDao.getEmployeeID() + "]님의 "
                                        + employeeSalaryDao.getYear() + "년 "
                                        + employeeSalaryDao.getMonth() + "월 데이터가 있습니다. 삭제하고 등록해주세요");
                bindingResult.addError(error);
            } else {
                employeeSalaryMapper.insertEmployeeSalaryDto(employeeSalaryDao);
            }
        }
    }

    // 급여명세서 삭제
    @Transactional
    public void deleteSalariesByIds(List<AdminDeleteSalaryRequest> salaryIds) {
        for (AdminDeleteSalaryRequest key : salaryIds) {
            userMapper.deleteEmployeeSalaryById(key.getEmployeeId(), key.getYear(), key.getMonth());
        }
    }
}
