package com.paystub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LoginDto {
    private String username;
    private String password;
}
