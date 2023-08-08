package com.paystub.service;

import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.ResponseDto;
import com.paystub.dto.UserDto;
import com.paystub.repository.UserMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class UploadService {

    private final UserMapper userMapper;

    public List<ResponseDto> excelToDto(MultipartFile file) {

        List<ResponseDto> list = new ArrayList<>();

        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(2); // 세 번째 시트
            // 첫 번째 행은 헤더이므로 두 번째 행부터 시작합니다.
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // 각 열의 값을 가져옵니다.

                // 사번
                Integer EmployeeID = Integer.valueOf(getStringValueOrNull(row.getCell(0)));
                String name = getStringValueOrNull(row.getCell(1));
                String birthday = getStringValueOrNull(row.getCell(2));
                String emailAddress = getStringValueOrNull(row.getCell(27));

                // 기본 수당
                BigDecimal BasicSalary = getNumericValueOrNull(row.getCell(3));
                // 주휴 수당
                BigDecimal HolidayAllowance = getNumericValueOrNull(row.getCell(4));
                // 중식비
                BigDecimal LunchExpenses = getNumericValueOrNull(row.getCell(5));
                // 주휴수당 첫째주
                BigDecimal FirstWeekHolidayAllowance = getNumericValueOrNull(row.getCell(6));
                // 주휴수당 소급분
                BigDecimal RetroactiveHolidayAllowance = getNumericValueOrNull(row.getCell(7));
                // 지급합계
                BigDecimal TotalPayment = getNumericValueOrNull(row.getCell(8));
                // 소득세
                BigDecimal IncomeTax = getNumericValueOrNull(row.getCell(9));
                // 주민세
                BigDecimal ResidentTax = getNumericValueOrNull(row.getCell(10));
                // 고용 보험
                BigDecimal EmploymentInsurance = getNumericValueOrNull(row.getCell(11));
                // 국민 연금
                BigDecimal NationalPension = getNumericValueOrNull(row.getCell(12));
                // 건강 보험
                BigDecimal HealthInsurance = getNumericValueOrNull(row.getCell(13));
                // 노인 요양
                BigDecimal ElderlyCareInsurance = getNumericValueOrNull(row.getCell(14));
                // 고용 보험 (소급공제)
                BigDecimal EmploymentInsuranceDeduction = getNumericValueOrNull(row.getCell(15));
                // 국민 연금 (소급공제)
                BigDecimal NationalPensionDeduction = getNumericValueOrNull(row.getCell(16));
                // 건강 보험 (소급공제)
                BigDecimal HealthInsuranceDeduction = getNumericValueOrNull(row.getCell(17));
                // 노인 요양 (소급공제)
                BigDecimal ElderlyCareInsuranceDeduction = getNumericValueOrNull(row.getCell(18));
                // 공제 합계
                BigDecimal DeductionTotal = getNumericValueOrNull(row.getCell(19));
                // 실지금액
                BigDecimal NetPayment = getNumericValueOrNull(row.getCell(20));
                // 총 근로일수
                BigDecimal TotalWorkDays = getNumericValueOrNull(row.getCell(21));
                // 총 근무시간
                BigDecimal TotalWorkingHours = getNumericValueOrNull(row.getCell(22));

                // 주휴산정시간
                BigDecimal HolidayCalculationHours = getNumericValueOrNull(row.getCell(23));
                // 두번째 자리에서 반올림
                HolidayCalculationHours = HolidayCalculationHours != null ? HolidayCalculationHours.setScale(1, BigDecimal.ROUND_HALF_UP) : null;

                // 주휴산정시간(소급분)
                BigDecimal OvertimeCalculationHours = getNumericValueOrNull(row.getCell(24));
                // 두번째 자리에서 반올림
                OvertimeCalculationHours = OvertimeCalculationHours != null ? OvertimeCalculationHours.setScale(1, BigDecimal.ROUND_HALF_UP) : null;

                // 시급
                BigDecimal HourlyWage = getNumericValueOrNull(row.getCell(25));
                // 근태 중식비
                BigDecimal LunchAllowance = getNumericValueOrNull(row.getCell(26));

                // UserDto 객체를 생성하고 값을 설정합니다.

                UserDto userDto = UserDto.builder()
                        .EmployeeID(EmployeeID)
                        .Name(name)
                        .State(2) // 활성화 상태
                        .Role(2) // 근무자 역할
                        .birthday(birthday)
                        .SocialNumber(null) // 주민번호는 Excel 데이터에 없습니다.
                        .EmailAddress(emailAddress)
                        .build();

                // EmployeeSalaryDto 객체를 생성하고 값을 설정합니다.

                EmployeeSalaryDto employeeSalaryDto = EmployeeSalaryDto.builder()
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
                // ResponseDto 객체를 생성하고 값을 설정합니다.
                ResponseDto responseDto = new ResponseDto(employeeSalaryDto, userDto);
                // 리스트에 ResponseDto 객체를 추가합니다.
                list.add(responseDto);
            }
            workbook.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    @Transactional
    public void saveData(List<UserDto> data) {
        for (UserDto userDto : data) {
            userMapper.insertUser(userDto); // EmployeeSalaryDto에 대해서도 동일하게 처리
            }
    }

    private BigDecimal getNumericValueOrNull(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return new BigDecimal(cell.getNumericCellValue());
    }

    private String getStringValueOrNull(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) {
            return null;
        }
        return cell.getStringCellValue();
    }
}