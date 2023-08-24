package com.paystub.admin.service;

import com.paystub.comon.util.AESUtilUtil;
import com.paystub.admin.dto.response.AdminSalaryResponse;
import com.paystub.admin.dto.EmployeeSalaryDao;
import com.paystub.user.dto.UserDao;
import com.paystub.admin.repository.AdminMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class ExelService {

//    private final UserMapper userMapper;
    private final AESUtilUtil aesUtilUtil;
    private final SalaryService salaryService;
    private final AdminMapper adminMapper;
    private final ManagementUserService managementUserService;

    // 엑셀 처리를 위한 메서드
    public List<AdminSalaryResponse> findResponseByYearAndMonth(Long year, Long month) {

        List<AdminSalaryResponse> joinedDataByYearAndMonth =
                adminMapper.findJoinedDataByYearAndMonth(year, month, null, null);
        return joinedDataByYearAndMonth;
    }

    // 검색을 위해 오버로딩
    public List<AdminSalaryResponse> findResponseByYearAndMonth(Long year, Long month,
                                                                String name, Long employeeId) {
        return adminMapper.findJoinedDataByYearAndMonth(year, month, name, employeeId);
    }

    public List<AdminSalaryResponse> processExcelFile(MultipartFile file, BindingResult bindingResult,
                                                      Long year, Long month) {

        List<UserDao> userDaos = new ArrayList<>();
        List<EmployeeSalaryDao> employeeSalaryDaos = new ArrayList<>();

        InputStream inputStream = null; // 여기서 변수 선언

        try {
            inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(2); // 세 번째 시트
            // 첫 번째 행은 헤더이므로 두 번째 행부터 시작합니다.
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                UserDao userDao = managementUserService.createUserDto(row);
                EmployeeSalaryDao employeeSalaryDao = salaryService.createEmployeeSalaryDto(row, userDao.getEmployeeID());

                userDaos.add(userDao);
                employeeSalaryDaos.add(employeeSalaryDao);
            }
            workbook.close();
        } catch (Exception e) {
            // 오류 발생 시 BindingResult에 오류 추가
            FieldError error = new FieldError("file", "file", "파일 처리 중 오류가 발생했습니다: " + e.getMessage());
            bindingResult.addError(error);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    FieldError error = new FieldError("file", "file", "파일 스트림 닫기 중 오류가 발생했습니다: " + e.getMessage());
                    bindingResult.addError(error);
                }
            }
        }

        managementUserService.saveUsers(userDaos, bindingResult);
        salaryService.saveEmployeeSalaries(employeeSalaryDaos, bindingResult);

        // 저장된 데이터를 바로 반환하거나 필요한 경우 데이터베이스에서 다시 조회할 수 있습니다.
        return findResponseByYearAndMonth(year, month);
    }
}