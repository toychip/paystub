package com.paystub.admin.service;

import com.paystub.admin.dto.EmployeeSalaryDao;
import com.paystub.admin.dto.request.AdminDeleteSalaryRequest;
import com.paystub.admin.repository.AdminMapper;
import com.paystub.comon.util.ExelTransObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryService {
//     급여 명세서 저장 및 삭제 서비스

    private final ExelTransObjectUtil exelTransObjectUtil;
    private final AdminMapper adminMapper;

    // 급여 명세서를 저장하는 메서드 (트랜잭션 처리)
    @Transactional
    public void saveEmployeeSalaries(List<EmployeeSalaryDao> employeeSalaryDaoList, BindingResult bindingResult) {
        for (EmployeeSalaryDao employeeSalaryDao : employeeSalaryDaoList) {
            // 해당 년, 월, 직원 ID에 대한 급여 데이터 조회
            EmployeeSalaryDao existingData = adminMapper.findSalaryByYearMonthAndEmployeeID(
                    employeeSalaryDao.getYear(),
                    employeeSalaryDao.getMonth(),
                    employeeSalaryDao.getEmployeeID()
            );
            if (existingData != null) {
                // 동일한 년, 월, 직원 ID의 데이터가 이미 있으면 오류 추가
                FieldError error = new FieldError
                        ("employeeSalaryDto", "EmployeeID",
                                "이미 사번 [" + employeeSalaryDao.getEmployeeID() + "]님의 "
                                        + employeeSalaryDao.getYear() + "년 "
                                        + employeeSalaryDao.getMonth() + "월 데이터가 있습니다. 삭제하고 등록해주세요");
                bindingResult.addError(error);
            } else {
                // 급여 데이터 삽입
                adminMapper.insertEmployeeSalaryDto(employeeSalaryDao);
            }
        }
    }

    // 급여명세서 삭제 메서드 (트랜잭션 처리)
    @Transactional
    public void deleteSalariesByIds(List<AdminDeleteSalaryRequest> salaryIds) {
        for (AdminDeleteSalaryRequest key : salaryIds) {
            // 주어진 직원 ID, 년, 월에 대한 급여 데이터 삭제
            adminMapper.deleteEmployeeSalaryById(key.getEmployeeId(), key.getYear(), key.getMonth());
        }
    }

    // 엑셀의 행(Row)을 EmployeeSalaryDao 객체로 변환
    public EmployeeSalaryDao createEmployeeSalaryDto(Row row, String date, Integer employeeID) {

        // "년"을 기준으로 분할

        int year = getYear(date);
        int month = getMonth(date);

        BigDecimal basicSalary = exelTransObjectUtil.getNumericValueOrNull(row.getCell(3)); // 기본 수당
        BigDecimal holidayAllowance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(4)); // 주휴 수당
        BigDecimal lunchExpenses = exelTransObjectUtil.getNumericValueOrNull(row.getCell(5)); // 중식비
        BigDecimal firstWeekHolidayAllowance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(6)); // 주휴수당 첫째주
        BigDecimal retroactiveHolidayAllowance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(7)); // 주휴수당 소급분
        BigDecimal totalPayment = exelTransObjectUtil.getNumericValueOrNull(row.getCell(8)); // 지급합계
        BigDecimal overtimePay = exelTransObjectUtil.getNumericValueOrNull(row.getCell(9)); // 연장 수당
        BigDecimal otherPay = exelTransObjectUtil.getNumericValueOrNull(row.getCell(10)); // 기타 수당
        BigDecimal incomeTax = exelTransObjectUtil.getNumericValueOrNull(row.getCell(11)); // 소득세
        BigDecimal residentTax = exelTransObjectUtil.getNumericValueOrNull(row.getCell(12)); // 주민세
        BigDecimal employmentInsurance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(13)); // 고용 보험
        BigDecimal nationalPension = exelTransObjectUtil.getNumericValueOrNull(row.getCell(14)); // 국민 연금
        BigDecimal healthInsurance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(15)); // 건강 보험
        BigDecimal elderlyCareInsurance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(16)); // 노인 요양
        BigDecimal employmentInsuranceDeduction = exelTransObjectUtil.getNumericValueOrNull(row.getCell(17)); // 고용 보험 (소급공제)
        BigDecimal nationalPensionDeduction = exelTransObjectUtil.getNumericValueOrNull(row.getCell(18)); // 국민 연금 (소급공제)
        BigDecimal healthInsuranceDeduction = exelTransObjectUtil.getNumericValueOrNull(row.getCell(19)); // 건강 보험 (소급공제)
        BigDecimal elderlyCareInsuranceDeduction = exelTransObjectUtil.getNumericValueOrNull(row.getCell(20)); // 노인 요양 (소급공제)
        BigDecimal deductionTotal = exelTransObjectUtil.getNumericValueOrNull(row.getCell(21)); // 공제 합계
        BigDecimal netPayment = exelTransObjectUtil.getNumericValueOrNull(row.getCell(22)); // 실지금액
        BigDecimal totalWorkDays = exelTransObjectUtil.getNumericValueOrNull(row.getCell(23)); // 총 근로일수
        BigDecimal totalWorkingHours = exelTransObjectUtil.getNumericValueOrNull(row.getCell(24)); // 총 근무시간

        BigDecimal holidayCalculationHours = exelTransObjectUtil.getNumericValueOrNull(row.getCell(25)); // 주휴산정시간
        holidayCalculationHours = holidayCalculationHours != null ? holidayCalculationHours.setScale(1, BigDecimal.ROUND_HALF_UP) : null;// 두번째 자리에서 반올림

        BigDecimal overtimeCalculationHours = exelTransObjectUtil.getNumericValueOrNull(row.getCell(26)); // 주휴산정시간(소급분)
        overtimeCalculationHours = overtimeCalculationHours != null ? overtimeCalculationHours.setScale(1, BigDecimal.ROUND_HALF_UP) : null; // 두번째 자리에서 반올림

        BigDecimal hourlyWage = exelTransObjectUtil.getNumericValueOrNull(row.getCell(27)); // 시급
        BigDecimal lunchAllowance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(28)); // 근태 중식비

        // EmployeeSalaryDao 객체 생성 및 반환
        return EmployeeSalaryDao.builder()
                .year(year)
                .month(month)
                .EmployeeID(employeeID)
                .BasicSalary(basicSalary)
                .HolidayAllowance(holidayAllowance)
                .OvertimePay(overtimePay)
                .OtherPay(otherPay)
                .LunchExpenses(lunchExpenses)
                .FirstWeekHolidayAllowance(firstWeekHolidayAllowance)
                .RetroactiveHolidayAllowance(retroactiveHolidayAllowance)
                .TotalPayment(totalPayment)
                .IncomeTax(incomeTax)
                .ResidentTax(residentTax)
                .EmploymentInsurance(employmentInsurance)
                .NationalPension(nationalPension)
                .HealthInsurance(healthInsurance)
                .ElderlyCareInsurance(elderlyCareInsurance)
                .EmploymentInsuranceDeduction(employmentInsuranceDeduction)
                .NationalPensionDeduction(nationalPensionDeduction)
                .HealthInsuranceDeduction(healthInsuranceDeduction)
                .ElderlyCareInsuranceDeduction(elderlyCareInsuranceDeduction)
                .DeductionTotal(deductionTotal)
                .NetPayment(netPayment)
                .TotalWorkDays(totalWorkDays)
                .TotalWorkingHours(totalWorkingHours)
                .HolidayCalculationHours(holidayCalculationHours)
                .OvertimeCalculationHours(overtimeCalculationHours)
                .HourlyWage(hourlyWage)
                .LunchAllowance(lunchAllowance)
                .build();
    }

    public int getYear(String date) {
        String[] yearSplit = date.split("년", 2);
        String yearPart = yearSplit[0].trim(); // 연도 부분
        return Integer.parseInt(yearPart);
    }

    public int getMonth(String date) {
        String[] monthSplit = date.split("월", 2);
        String monthPart = monthSplit[0].trim(); // 월 부분

        // "월" 앞의 부분에서 숫자만 추출
        String[] splitForMonth = monthPart.split(" ");
        String month = splitForMonth[splitForMonth.length - 1];
        return Integer.parseInt(month);
    }
}
