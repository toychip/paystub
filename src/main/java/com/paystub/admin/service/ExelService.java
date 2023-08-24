package com.paystub.admin.service;

import com.paystub.comon.util.AESUtilUtil;
import com.paystub.admin.dto.response.AdminSalaryResponse;
import com.paystub.admin.dto.EmployeeSalaryDao;
import com.paystub.user.dto.UserDao;
import com.paystub.admin.repository.EmployeeSalaryMapper;
import com.paystub.user.repository.UserMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class ExelService {

    private final UserMapper userMapper;
    private final EmployeeSalaryMapper employeeSalaryMapper;
    private final SalaryService salaryService;
    private final AESUtilUtil aesUtilUtil;

    // 엑셀 처리를 위한 메서드
    public List<AdminSalaryResponse> findResponseByYearAndMonth(
            Long year, Long month) {


        List<AdminSalaryResponse> joinedDataByYearAndMonth = userMapper.findJoinedDataByYearAndMonth(year, month, null, null);

        return joinedDataByYearAndMonth;
    }

    // 검색을 위해 오버로딩
    public List<AdminSalaryResponse> findResponseByYearAndMonth(
            Long year, Long month, String name, Long employeeId
    ) {
        return userMapper.findJoinedDataByYearAndMonth(year, month, name, employeeId);
    }

    public List<AdminSalaryResponse> processExcelFile(MultipartFile file, BindingResult bindingResult,
                                                      Long year, Long month) {
        List<UserDao> adminUserListResponsAndUserSaveDaos = new ArrayList<>();
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

                UserDao userDao = createUserDto(row);
                EmployeeSalaryDao employeeSalaryDao = salaryService.createEmployeeSalaryDto(row, userDao.getEmployeeID());

                adminUserListResponsAndUserSaveDaos.add(userDao);
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

        saveUsers(adminUserListResponsAndUserSaveDaos, bindingResult);
        salaryService.saveEmployeeSalaries(employeeSalaryDaos, bindingResult);

        // 저장된 데이터를 바로 반환하거나 필요한 경우 데이터베이스에서 다시 조회할 수 있습니다.
        return findResponseByYearAndMonth(year, month);
    }


    @Transactional
    public void saveUsers(List<UserDao> adminUserListResponsAndUserSaveDaos, BindingResult bindingResult) {
        for (UserDao userDao : adminUserListResponsAndUserSaveDaos) {
            Optional<UserDao> existingUserWithSameIDAndName =
                    userMapper.findByEmployeeIDAndName(userDao.getEmployeeID(), userDao.getName());
            Optional<UserDao> existingUserWithSameID =
                    userMapper.findByEmployeeID(userDao.getEmployeeID());

            if (existingUserWithSameIDAndName.isPresent()) {
//                FieldError error = new FieldError("userDto", "Name",
//                        "[" + userDto.getName() + "]님은 이미 존재해서 회원이 추가되지 않았습니다");
//                bindingResult.addError(error);
            } else if (existingUserWithSameID.isPresent()) {
                FieldError error = new FieldError("userDto", "EmployeeID",
                        " 이미 [" + userDao.getEmployeeID() + "] 사번을 가진 직원이 존재합니다..");
                bindingResult.addError(error);
            } else {
                userMapper.insertUser(userDao);
            }
        }
    }




    public UserDao createUserDto(Row row) {
        Integer EmployeeID = Integer.valueOf(getStringValueOrNull(row.getCell(0)));
        String name = getStringValueOrNull(row.getCell(1));
        String birthday = getStringValueOrNull(row.getCell(2));
        String emailAddress = getStringValueOrNull(row.getCell(27));
        String socialNumber = aesUtilUtil.encrypt(birthday);

        return UserDao.builder()
                .EmployeeID(EmployeeID)
                .Name(name)
                .State(2) // 활성화 상태
                .Role(1) // 근무자 역할
                .birthday(birthday)
                .SocialNumber(socialNumber)
                .EmailAddress(emailAddress)
                .build();
    }



    public BigDecimal getNumericValueOrNull(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return new BigDecimal(cell.getNumericCellValue());
    }


    public String getStringValueOrNull(Cell cell) {
        if (cell == null) {
            return null;
        }

        CellType cellType = cell.getCellType();

        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                // 날짜형식의 셀일 경우, 날짜를 읽어서 문자열로 변환
                Date date = cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  // 날짜 포맷 지정
                return sdf.format(date);
            }
            // 숫자형 셀일 경우, 그냥 null 반환 (필요하다면 이 부분을 수정할 수 있음)
            return null;
        } else {
            // 그 외의 셀 타입일 경우, null 반환
            return null;
        }
    }

    public List<UserDao> getAdminUserForm() {
        return userMapper.findByAdminUser();
    }

    @Transactional
    public void deleteUsersByIds(List<Long> employeeIds) {
        userMapper.deleteEmployeeSalaryByIds(employeeIds);
        userMapper.deleteUsersByIds(employeeIds);
    }



}