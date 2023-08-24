package com.paystub.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

// 관리자가 사용자 테이블을 저장할때, 관리자 페이지 내에서 회원 관리 시 테이블 불러올때 사용되는 Dao
public class UserDao {

    private Integer EmployeeID; // 일용직근무자사번
    private String Name; //성명
    private Integer State; // 상태, 엑셀에 없는 컬럼, 내가 만들어야함 비활성화:1, 활성화:2
    private Integer Role; // 역할 엑셀에 없는 컬럼, 내가 만들어야함 관리자:1, 근무자:2
    private String birthday; // 생일
    private String SocialNumber; // 주민번호
    private String EmailAddress; //이메일

}
