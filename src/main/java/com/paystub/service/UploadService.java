package com.paystub.service;

import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.ResponseDto;
import com.paystub.dto.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class UploadService {

    public List<ResponseDto> excelToDto(MultipartFile file) {

        List<ResponseDto> list = new ArrayList<>();

        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(2); // 세 번째 시트
            // 첫 번째 행은 헤더이므로 두 번째 행부터 시작합니다.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // 각 열의 값을 가져옵니다.

                // 사번
                Integer EmployeeID = getNumericValueOrZero(row.getCell(0));
                String name = getStringValueOrNull(row.getCell(1));
                String birthday = getStringValueOrNull(row.getCell(3));
                String emailAddress = getStringValueOrNull(row.getCell(27));


                BigDecimal BasicSalary = getNumericValueOrNull(row.getCell(2));
                BigDecimal HolidayAllowance = getNumericValueOrNull(row.getCell(4));
                BigDecimal LunchExpenses = getNumericValueOrNull(row.getCell(5));
                BigDecimal FirstWeekHolidayAllowance = getNumericValueOrNull(row.getCell(6));
                BigDecimal RetroactiveHolidayAllowance = getNumericValueOrNull(row.getCell(7));
                BigDecimal TotalPayment = getNumericValueOrNull(row.getCell(8));
                BigDecimal IncomeTax = getNumericValueOrNull(row.getCell(9));
                BigDecimal ResidentTax = getNumericValueOrNull(row.getCell(10));
                BigDecimal EmploymentInsurance = getNumericValueOrNull(row.getCell(11));
                BigDecimal NationalPension = getNumericValueOrNull(row.getCell(12));
                BigDecimal HealthInsurance = getNumericValueOrNull(row.getCell(13));
                BigDecimal ElderlyCareInsurance = getNumericValueOrNull(row.getCell(14));
                BigDecimal EmploymentInsuranceDeduction = getNumericValueOrNull(row.getCell(15));
                BigDecimal NationalPensionDeduction = getNumericValueOrNull(row.getCell(16));
                BigDecimal HealthInsuranceDeduction = getNumericValueOrNull(row.getCell(17));
                BigDecimal ElderlyCareInsuranceDeduction = getNumericValueOrNull(row.getCell(18));
                BigDecimal DeductionTotal = getNumericValueOrNull(row.getCell(19));
                BigDecimal NetPayment = getNumericValueOrNull(row.getCell(20));
                Integer TotalWorkDays = getNumericValueOrZero(row.getCell(21));
                Integer TotalWorkingHours = getNumericValueOrZero(row.getCell(22));
                Integer HolidayCalculationHours = getNumericValueOrZero(row.getCell(23));
                Integer OvertimeCalculationHours = getNumericValueOrZero(row.getCell(24));
                BigDecimal HourlyWage = getNumericValueOrNull(row.getCell(25));
                BigDecimal LunchAllowance = getNumericValueOrNull(row.getCell(26));

                // UserDto 객체를 생성하고 값을 설정합니다.

                UserDto userDto = UserDto.builder()
                        .EmployeeID(EmployeeID)
                        .Name(name)
                        .prState(2) // 활성화 상태
                        .Role(2) // 근무자 역할
                        .birthday(birthday)
                        .SocialNumber(null) // 주민번호는 Excel 데이터에 없습니다.
                        .EmailAddress(emailAddress)
                        .build();

                // EmployeeSalaryDto 객체를 생성하고 값을 설정합니다.

                EmployeeSalaryDto dto = EmployeeSalaryDto.builder()
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
                ResponseDto responseDto = new ResponseDto(dto, userDto);
                // 리스트에 ResponseDto 객체를 추가합니다.
                list.add(responseDto);
            }
            workbook.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    private BigDecimal getNumericValueOrNull(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return new BigDecimal(cell.getNumericCellValue());
    }

    private int getNumericValueOrZero(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return 0;
        }
        return (int) cell.getNumericCellValue();
    }

    private String getStringValueOrNull(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) {
            return null;
        }
        return cell.getStringCellValue();
    }
}