package com.paystub.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSalaryDao {
    private Integer EmployeeID; // 일용직 근무자 사번

    private Integer year;
    private Integer month;
    private BigDecimal BasicSalary; // 기본수당
    private BigDecimal HolidayAllowance; // 주휴수당
    private BigDecimal LunchExpenses; // 지금 중식비
    private BigDecimal FirstWeekHolidayAllowance; // 주휴수당(첫째주)
    private BigDecimal RetroactiveHolidayAllowance; // 주휴수당(소급분)
    private BigDecimal TotalPayment; // 지급합계
    private BigDecimal IncomeTax; // 소득세
    private BigDecimal ResidentTax; // 주민세
    private BigDecimal EmploymentInsurance; // 고용보험
    private BigDecimal NationalPension; // 국민연금
    private BigDecimal HealthInsurance; // 건강보험
    private BigDecimal ElderlyCareInsurance; // 노인요양
    private BigDecimal EmploymentInsuranceDeduction; // 고용보험 (소급공제)
    private BigDecimal NationalPensionDeduction; // 국민연금 (소급공제)
    private BigDecimal HealthInsuranceDeduction; // 건강보험 (소급공제)
    private BigDecimal ElderlyCareInsuranceDeduction; // 노인요양 (소급공제)
    private BigDecimal DeductionTotal; // 공제합계
    private BigDecimal NetPayment; // 실지급액
    private BigDecimal TotalWorkDays; // 총 근로일수
    private BigDecimal TotalWorkingHours; // 총근무시간
    private BigDecimal HolidayCalculationHours; // 주휴산정시간
    private BigDecimal OvertimeCalculationHours; // 주휴산정시간(소급분)
    private BigDecimal HourlyWage; // 시급
    private BigDecimal LunchAllowance; // 근태 중식비

}
