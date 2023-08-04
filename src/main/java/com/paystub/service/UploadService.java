package com.paystub.service;

import com.paystub.dto.EmployeeSalaryDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class UploadService {

    public List<EmployeeSalaryDto> exelToObject(MultipartFile inputFile) {

        List<EmployeeSalaryDto> list = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(inputFile.getInputStream());

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트를 가져옵니다.

            for (Row row : sheet) { // 각 행을 반복합니다.
                if (row.getRowNum() == 0) { // 첫 번째 행(헤더)을 건너뜁니다.
                    continue;
                }

                // 각 열의 값을 가져옵니다.
                Integer EmployeeID = (int) getNumericValueOrZero(row.getCell(0));
                BigDecimal BasicSalary = getNumericValueOrNull(row.getCell(1));
                BigDecimal HolidayAllowance = getNumericValueOrNull(row.getCell(2));
                BigDecimal LunchExpenses = getNumericValueOrNull(row.getCell(3));
                BigDecimal FirstWeekHolidayAllowance = getNumericValueOrNull(row.getCell(4));
                BigDecimal RetroactiveHolidayAllowance = getNumericValueOrNull(row.getCell(5));
                BigDecimal TotalPayment = getNumericValueOrNull(row.getCell(6));
                BigDecimal IncomeTax = getNumericValueOrNull(row.getCell(7));
                BigDecimal ResidentTax = getNumericValueOrNull(row.getCell(8));
                BigDecimal EmploymentInsurance = getNumericValueOrNull(row.getCell(9));
                BigDecimal NationalPension = getNumericValueOrNull(row.getCell(10));
                BigDecimal HealthInsurance = getNumericValueOrNull(row.getCell(11));
                BigDecimal ElderlyCareInsurance = getNumericValueOrNull(row.getCell(12));
                BigDecimal EmploymentInsuranceDeduction = getNumericValueOrNull(row.getCell(13));
                BigDecimal NationalPensionDeduction = getNumericValueOrNull(row.getCell(14));
                BigDecimal HealthInsuranceDeduction = getNumericValueOrNull(row.getCell(15));
                BigDecimal ElderlyCareInsuranceDeduction = getNumericValueOrNull(row.getCell(16));
                BigDecimal DeductionTotal = getNumericValueOrNull(row.getCell(17));
                BigDecimal NetPayment = getNumericValueOrNull(row.getCell(18));
                Integer TotalWorkDays = (int) getNumericValueOrZero(row.getCell(19));
                Integer TotalWorkingHours = (int) getNumericValueOrZero(row.getCell(20));
                Integer HolidayCalculationHours = (int) getNumericValueOrZero(row.getCell(21));
                Integer OvertimeCalculationHours = (int) getNumericValueOrZero(row.getCell(22));
                BigDecimal HourlyWage = getNumericValueOrNull(row.getCell(23));
                BigDecimal LunchAllowance = getNumericValueOrNull(row.getCell(24));

//                getNumericValueOrNull((25))

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

                // 리스트에 dto 객체를 추가합니다.
                list.add(dto);
            }

        } catch (IOException e) {
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

}