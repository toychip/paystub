package com.paystub.user.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
public class UserResponse {

    private Integer year; // 년
    private Integer month; // 월
    private Integer EmployeeID; // 일용직 근무자 사번
    private BigDecimal BasicSalary; // 기본수당
    private BigDecimal HolidayAllowance; // 주휴수당
    private BigDecimal OvertimePay; // 연장 수당
    private BigDecimal OtherPay; // 기타 수당
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

    private String Name; //성명
    private String birthday; // 생일

}
