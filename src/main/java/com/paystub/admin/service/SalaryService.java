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
    // 급여 명세서 저장 및 삭제 서비스

//    private final UserMapper userMapper;
    private final ExelTransObjectUtil exelTransObjectUtil;
    private final AdminMapper adminMapper;

    @Transactional
    public void saveEmployeeSalaries(List<EmployeeSalaryDao> employeeSalaryDaoList, BindingResult bindingResult) {
        for (EmployeeSalaryDao employeeSalaryDao : employeeSalaryDaoList) {
            EmployeeSalaryDao existingData = adminMapper.findSalaryByYearMonthAndEmployeeID(
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
                adminMapper.insertEmployeeSalaryDto(employeeSalaryDao);
            }
        }
    }

    // 급여명세서 삭제
    @Transactional
    public void deleteSalariesByIds(List<AdminDeleteSalaryRequest> salaryIds) {
        for (AdminDeleteSalaryRequest key : salaryIds) {
            adminMapper.deleteEmployeeSalaryById(key.getEmployeeId(), key.getYear(), key.getMonth());
        }
    }

    public EmployeeSalaryDao createEmployeeSalaryDto(Row row, Integer EmployeeID) {
        // 현재 날짜를 가져옵니다.
        LocalDate now = LocalDate.now();

        // 현재 년도를 가져옵니다.
        int year = now.getYear();

        // 한 달 전의 월을 가져옵니다.
        int month = now.minusMonths(1).getMonthValue();

        BigDecimal BasicSalary = exelTransObjectUtil.getNumericValueOrNull(row.getCell(3)); // 기본 수당
        BigDecimal HolidayAllowance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(4)); // 주휴 수당
        BigDecimal LunchExpenses = exelTransObjectUtil.getNumericValueOrNull(row.getCell(5)); // 중식비
        BigDecimal FirstWeekHolidayAllowance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(6)); // 주휴수당 첫째주
        BigDecimal RetroactiveHolidayAllowance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(7)); // 주휴수당 소급분
        BigDecimal TotalPayment = exelTransObjectUtil.getNumericValueOrNull(row.getCell(8)); // 지급합계
        BigDecimal IncomeTax = exelTransObjectUtil.getNumericValueOrNull(row.getCell(9)); // 소득세
        BigDecimal ResidentTax = exelTransObjectUtil.getNumericValueOrNull(row.getCell(10)); // 주민세
        BigDecimal EmploymentInsurance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(11)); // 고용 보험
        BigDecimal NationalPension = exelTransObjectUtil.getNumericValueOrNull(row.getCell(12)); // 국민 연금
        BigDecimal HealthInsurance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(13)); // 건강 보험
        BigDecimal ElderlyCareInsurance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(14)); // 노인 요양
        BigDecimal EmploymentInsuranceDeduction = exelTransObjectUtil.getNumericValueOrNull(row.getCell(15)); // 고용 보험 (소급공제)
        BigDecimal NationalPensionDeduction = exelTransObjectUtil.getNumericValueOrNull(row.getCell(16)); // 국민 연금 (소급공제)
        BigDecimal HealthInsuranceDeduction = exelTransObjectUtil.getNumericValueOrNull(row.getCell(17)); // 건강 보험 (소급공제)
        BigDecimal ElderlyCareInsuranceDeduction = exelTransObjectUtil.getNumericValueOrNull(row.getCell(18)); // 노인 요양 (소급공제)
        BigDecimal DeductionTotal = exelTransObjectUtil.getNumericValueOrNull(row.getCell(19)); // 공제 합계
        BigDecimal NetPayment = exelTransObjectUtil.getNumericValueOrNull(row.getCell(20)); // 실지금액
        BigDecimal TotalWorkDays = exelTransObjectUtil.getNumericValueOrNull(row.getCell(21)); // 총 근로일수
        BigDecimal TotalWorkingHours = exelTransObjectUtil.getNumericValueOrNull(row.getCell(22)); // 총 근무시간

        BigDecimal HolidayCalculationHours = exelTransObjectUtil.getNumericValueOrNull(row.getCell(23)); // 주휴산정시간
        HolidayCalculationHours = HolidayCalculationHours != null ? HolidayCalculationHours.setScale(1, BigDecimal.ROUND_HALF_UP) : null;// 두번째 자리에서 반올림

        BigDecimal OvertimeCalculationHours = exelTransObjectUtil.getNumericValueOrNull(row.getCell(24)); // 주휴산정시간(소급분)
        OvertimeCalculationHours = OvertimeCalculationHours != null ? OvertimeCalculationHours.setScale(1, BigDecimal.ROUND_HALF_UP) : null; // 두번째 자리에서 반올림

        BigDecimal HourlyWage = exelTransObjectUtil.getNumericValueOrNull(row.getCell(25)); // 시급
        BigDecimal LunchAllowance = exelTransObjectUtil.getNumericValueOrNull(row.getCell(26)); // 근태 중식비

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
}
