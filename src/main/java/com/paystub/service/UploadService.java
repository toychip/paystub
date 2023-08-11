package com.paystub.service;

import com.paystub.config.AESUtilConfig;
import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.ResponseDto;
import com.paystub.dto.UserDto;
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
public class UploadService {

    private final UserMapper userMapper;
    private final EmployeeSalaryMapper employeeSalaryMapper;
    private final AESUtilConfig aesUtilConfig;

    public List<ResponseDto> findResponseByYearAndMonth(Long year, Long month) {
        return userMapper.findJoinedDataByYearAndMonth(year, month);
    }


    public List<ResponseDto> processExcelFile(MultipartFile file, BindingResult bindingResult,
                                              Long year, Long month) {
        List<UserDto> userDtos = new ArrayList<>();
        List<EmployeeSalaryDto> employeeSalaryDtos = new ArrayList<>();

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

                UserDto userDto = createUserDto(row);
                EmployeeSalaryDto employeeSalaryDto = createEmployeeSalaryDto(row, userDto.getEmployeeID());

                userDtos.add(userDto);
                employeeSalaryDtos.add(employeeSalaryDto);
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

        saveUsers(userDtos, bindingResult);
        saveEmployeeSalaries(employeeSalaryDtos, bindingResult);

        // 저장된 데이터를 바로 반환하거나 필요한 경우 데이터베이스에서 다시 조회할 수 있습니다.
        return findResponseByYearAndMonth(year, month);
    }


    @Transactional
    public void saveUsers(List<UserDto> userDtos, BindingResult bindingResult) {
        for (UserDto userDto : userDtos) {
            Optional<UserDto> existingUserWithSameIDAndName = userMapper.findByEmployeeIDAndName(userDto.getEmployeeID(), userDto.getName());
            Optional<UserDto> existingUserWithSameID = userMapper.findByEmployeeID(userDto.getEmployeeID());

            if (existingUserWithSameIDAndName.isPresent()) {
                FieldError error = new FieldError("userDto", "Name", "[" + userDto.getName() + "]님이 이미 존재합니다.");
                bindingResult.addError(error);
            } else if (existingUserWithSameID.isPresent()) {
                FieldError error = new FieldError("userDto", "EmployeeID", "[" + userDto.getName() + "]님이 이미 [" + userDto.getEmployeeID() + "]를 사용중입니다.");
                bindingResult.addError(error);
            } else {
                userMapper.insertUser(userDto);
            }
        }
    }


    @Transactional
    public void saveEmployeeSalaries(List<EmployeeSalaryDto> employeeSalaryDtos, BindingResult bindingResult) {
        for (EmployeeSalaryDto employeeSalaryDto : employeeSalaryDtos) {
            EmployeeSalaryDto existingData = employeeSalaryMapper.findSalaryByYearMonthAndEmployeeID(
                    employeeSalaryDto.getYear(),
                    employeeSalaryDto.getMonth(),
                    employeeSalaryDto.getEmployeeID()
            );
            if (existingData != null) {
                FieldError error = new FieldError("employeeSalaryDto", "EmployeeID", "동일한 EmployeeID, year, month가 이미 존재합니다.");
                bindingResult.addError(error);
            } else {
                employeeSalaryMapper.insertEmployeeSalaryDto(employeeSalaryDto);
            }
        }
    }

    private UserDto createUserDto(Row row) {
        Integer EmployeeID = Integer.valueOf(getStringValueOrNull(row.getCell(0)));
        String name = getStringValueOrNull(row.getCell(1));
        String birthday = getStringValueOrNull(row.getCell(2));
        String emailAddress = getStringValueOrNull(row.getCell(27));
        String socialNumber = aesUtilConfig.encrypt(birthday);

        return UserDto.builder()
                .EmployeeID(EmployeeID)
                .Name(name)
                .State(2) // 활성화 상태
                .Role(1) // 근무자 역할
                .birthday(birthday)
                .SocialNumber(socialNumber)
                .EmailAddress(emailAddress)
                .build();
    }

    private EmployeeSalaryDto createEmployeeSalaryDto(Row row, Integer EmployeeID) {
        // 현재 날짜를 가져옵니다.
        LocalDate now = LocalDate.now();

        // 현재 년도를 가져옵니다.
        int year = now.getYear();

        // 한 달 전의 월을 가져옵니다.
        int month = now.minusMonths(2).getMonthValue();

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

        return EmployeeSalaryDto.builder()
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
}