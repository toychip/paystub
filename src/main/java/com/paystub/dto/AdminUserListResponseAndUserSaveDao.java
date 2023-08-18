package com.paystub.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserListResponseAndUserSaveDao {

    private Integer EmployeeID; // 일용직근무자사번
    private String Name; //성명
    private Integer State; // 상태, 엑셀에 없는 컬럼, 내가 만들어야함 비활성화:1, 활성화:2
    private Integer Role; // 역할 엑셀에 없는 컬럼, 내가 만들어야함 관리자:1, 근무자:2
    private String birthday; // 생일
    private String SocialNumber; // 주민번호
    private String EmailAddress; //이메일

}
