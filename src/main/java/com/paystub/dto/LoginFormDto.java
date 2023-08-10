package com.paystub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LoginFormDto {
    private String username;    // EmployeeID
    private String password;    // birthday
    private Integer role;       // Role (관리자: 2, 사용자: 1)
}
