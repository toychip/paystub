package com.paystub.dto;

import lombok.*;


@Getter
@Setter

@NoArgsConstructor
public class ResponseDto extends EmployeeSalaryDto {
    private Integer EmployeeID; // 일용직근무자사번
    private String Name; //성명
    private Integer prState; // 상태
    private Integer Role; // 역할
    private String birthday; // 생일
    private String SocialNumber; // 주민번호
    private String EmailAddress; //이메일
}
