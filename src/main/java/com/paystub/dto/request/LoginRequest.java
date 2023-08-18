package com.paystub.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LoginRequest {
    private String username;    // EmployeeID
    private String password;    // birthday
    private Integer role;       // Role (관리자: 2, 사용자: 1)
}
