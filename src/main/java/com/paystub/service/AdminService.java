package com.paystub.service;

import com.paystub.config.AESUtilConfig;
import com.paystub.dto.response.AdminSalaryResponse;
import com.paystub.dto.EmployeeSalaryDao;
import com.paystub.dto.request.AdminDeleteSalaryRequest;
import com.paystub.dto.AdminUserListResponseAndUserSaveDao;
import com.paystub.repository.EmployeeSalaryMapper;
import com.paystub.repository.UserMapper;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class AdminService {

    private final UserMapper userMapper;
    private final EmployeeSalaryMapper employeeSalaryMapper;
    private final AESUtilConfig aesUtilConfig;

    // 엑셀 처리를 위한 메서드
    public List<AdminSalaryResponse> findResponseByYearAndMonth(
            Long year, Long month) {
        return userMapper.findJoinedDataByYearAndMonth(year, month, null, null);
    }

    // 검색을 위해 오버로딩
    public List<AdminSalaryResponse> findResponseByYearAndMonth(
            Long year, Long month, String name, Long employeeId
    ) {
        System.out.println("service.year = " + year);
        System.out.println("service.getMonth() = " + month);
        return userMapper.findJoinedDataByYearAndMonth(year, month, name, employeeId);
    }

    public List<AdminSalaryResponse> processExcelFile(MultipartFile file, BindingResult bindingResult,
                                                      Long year, Long month) {
        List<AdminUserListResponseAndUserSaveDao> adminUserListResponsAndUserSaveDaos = new ArrayList<>();
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

                AdminUserListResponseAndUserSaveDao adminUserListResponseAndUserSaveDao = createUserDto(row);
                EmployeeSalaryDao employeeSalaryDao = createEmployeeSalaryDto(row, adminUserListResponseAndUserSaveDao.getEmployeeID());

                adminUserListResponsAndUserSaveDaos.add(adminUserListResponseAndUserSaveDao);
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
        saveEmployeeSalaries(employeeSalaryDaos, bindingResult);

        // 저장된 데이터를 바로 반환하거나 필요한 경우 데이터베이스에서 다시 조회할 수 있습니다.
        return findResponseByYearAndMonth(year, month);
    }


    @Transactional
    public void saveUsers(List<AdminUserListResponseAndUserSaveDao> adminUserListResponsAndUserSaveDaos, BindingResult bindingResult) {
        for (AdminUserListResponseAndUserSaveDao adminUserListResponseAndUserSaveDao : adminUserListResponsAndUserSaveDaos) {
            Optional<AdminUserListResponseAndUserSaveDao> existingUserWithSameIDAndName =
                    userMapper.findByEmployeeIDAndName(adminUserListResponseAndUserSaveDao.getEmployeeID(), adminUserListResponseAndUserSaveDao.getName());
            Optional<AdminUserListResponseAndUserSaveDao> existingUserWithSameID =
                    userMapper.findByEmployeeID(adminUserListResponseAndUserSaveDao.getEmployeeID());

            if (existingUserWithSameIDAndName.isPresent()) {
//                FieldError error = new FieldError("userDto", "Name",
//                        "[" + userDto.getName() + "]님은 이미 존재해서 회원이 추가되지 않았습니다");
//                bindingResult.addError(error);
            } else if (existingUserWithSameID.isPresent()) {
                FieldError error = new FieldError("userDto", "EmployeeID",
                        " 이미 [" + adminUserListResponseAndUserSaveDao.getEmployeeID() + "] 사번을 가진 직원이 존재합니다..");
                bindingResult.addError(error);
            } else {
                userMapper.insertUser(adminUserListResponseAndUserSaveDao);
            }
        }
    }


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

    private AdminUserListResponseAndUserSaveDao createUserDto(Row row) {
        Integer EmployeeID = Integer.valueOf(getStringValueOrNull(row.getCell(0)));
        String name = getStringValueOrNull(row.getCell(1));
        String birthday = getStringValueOrNull(row.getCell(2));
        String emailAddress = getStringValueOrNull(row.getCell(27));
        String socialNumber = aesUtilConfig.encrypt(birthday);

        return AdminUserListResponseAndUserSaveDao.builder()
                .EmployeeID(EmployeeID)
                .Name(name)
                .State(2) // 활성화 상태
                .Role(1) // 근무자 역할
                .birthday(birthday)
                .SocialNumber(socialNumber)
                .EmailAddress(emailAddress)
                .build();
    }

    private EmployeeSalaryDao createEmployeeSalaryDto(Row row, Integer EmployeeID) {
        // 현재 날짜를 가져옵니다.
        LocalDate now = LocalDate.now();

        // 현재 년도를 가져옵니다.
        int year = now.getYear();

        // 한 달 전의 월을 가져옵니다.
        int month = now.minusMonths(1).getMonthValue();

        BigDecimal BasicSalary = getNumericValueOrNull(row.getCell(3)); // 기본 수당
        BigDecimal HolidayAllowance = getNumericValueOrNull(row.getCell(4)); // 주휴 수당
        BigDecimal LunchExpenses = getNumericValueOrNull(row.getCell(5)); // 중식비
        BigDecimal FirstWeekHolidayAllowance = getNumericValueOrNull(row.getCell(6)); // 주휴수당 첫째주
        BigDecimal RetroactiveHolidayAllowance = getNumericValueOrNull(row.getCell(7)); // 주휴수당 소급분
        BigDecimal TotalPayment = getNumericValueOrNull(row.getCell(8)); // 지급합계
        BigDecimal IncomeTax = getNumericValueOrNull(row.getCell(9)); // 소득세
        BigDecimal ResidentTax = getNumericValueOrNull(row.getCell(10)); // 주민세
        BigDecimal EmploymentInsurance = getNumericValueOrNull(row.getCell(11)); // 고용 보험
        BigDecimal NationalPension = getNumericValueOrNull(row.getCell(12)); // 국민 연금
        BigDecimal HealthInsurance = getNumericValueOrNull(row.getCell(13)); // 건강 보험
        BigDecimal ElderlyCareInsurance = getNumericValueOrNull(row.getCell(14)); // 노인 요양
        BigDecimal EmploymentInsuranceDeduction = getNumericValueOrNull(row.getCell(15)); // 고용 보험 (소급공제)
        BigDecimal NationalPensionDeduction = getNumericValueOrNull(row.getCell(16)); // 국민 연금 (소급공제)
        BigDecimal HealthInsuranceDeduction = getNumericValueOrNull(row.getCell(17)); // 건강 보험 (소급공제)
        BigDecimal ElderlyCareInsuranceDeduction = getNumericValueOrNull(row.getCell(18)); // 노인 요양 (소급공제)
        BigDecimal DeductionTotal = getNumericValueOrNull(row.getCell(19)); // 공제 합계
        BigDecimal NetPayment = getNumericValueOrNull(row.getCell(20)); // 실지금액
        BigDecimal TotalWorkDays = getNumericValueOrNull(row.getCell(21)); // 총 근로일수
        BigDecimal TotalWorkingHours = getNumericValueOrNull(row.getCell(22)); // 총 근무시간

        BigDecimal HolidayCalculationHours = getNumericValueOrNull(row.getCell(23)); // 주휴산정시간
        HolidayCalculationHours = HolidayCalculationHours != null ? HolidayCalculationHours.setScale(1, BigDecimal.ROUND_HALF_UP) : null;// 두번째 자리에서 반올림

        BigDecimal OvertimeCalculationHours = getNumericValueOrNull(row.getCell(24)); // 주휴산정시간(소급분)
        OvertimeCalculationHours = OvertimeCalculationHours != null ? OvertimeCalculationHours.setScale(1, BigDecimal.ROUND_HALF_UP) : null; // 두번째 자리에서 반올림

        BigDecimal HourlyWage = getNumericValueOrNull(row.getCell(25)); // 시급
        BigDecimal LunchAllowance = getNumericValueOrNull(row.getCell(26)); // 근태 중식비

        return EmployeeSalaryDao.builder()
                .year(year)
                .month(month)
                .EmployeeID(EmployeeID)
                .BasicSalary(BasicSalary)
                .HolidayAllowance(HolidayAllowance)
                .LunchExpenses(LunchExpenses)
                .FirstWeekHolidayAllowance(FirstWeekHolidayAllowance)
                .RetroactiveHolidayAllowance(RetroactiveHolidayAllowance)
                .TotalPayment(TotalPayment)
                .IncomeTax(IncomeTax)
                .ResidentTax(ResidentTax)
                .EmploymentInsurance(EmploymentInsurance)
                .NationalPension(NationalPension)
                .HealthInsurance(HealthInsurance)
                .ElderlyCareInsurance(ElderlyCareInsurance)
                .EmploymentInsuranceDeduction(EmploymentInsuranceDeduction)
                .NationalPensionDeduction(NationalPensionDeduction)
                .HealthInsuranceDeduction(HealthInsuranceDeduction)
                .ElderlyCareInsuranceDeduction(ElderlyCareInsuranceDeduction)
                .DeductionTotal(DeductionTotal)
                .NetPayment(NetPayment)
                .TotalWorkDays(TotalWorkDays)
                .TotalWorkingHours(TotalWorkingHours)
                .HolidayCalculationHours(HolidayCalculationHours)
                .OvertimeCalculationHours(OvertimeCalculationHours)
                .HourlyWage(HourlyWage)
                .LunchAllowance(LunchAllowance)
                .build();
    }

    private BigDecimal getNumericValueOrNull(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return new BigDecimal(cell.getNumericCellValue());
    }


    private String getStringValueOrNull(Cell cell) {
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

    public List<AdminUserListResponseAndUserSaveDao> getAdminUserForm() {
        return userMapper.findByAdminUser();
    }

    @Transactional
    public void deleteUsersByIds(List<Long> employeeIds) {
        userMapper.deleteEmployeeSalaryByIds(employeeIds);
        userMapper.deleteUsersByIds(employeeIds);
    }

    // 급여명세서 삭제
    @Transactional
    public void deleteSalariesByIds(List<AdminDeleteSalaryRequest> salaryIds) {
        for (AdminDeleteSalaryRequest key : salaryIds) {
            userMapper.deleteEmployeeSalaryById(key.getEmployeeId(), key.getYear(), key.getMonth());
        }
    }

}