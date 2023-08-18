package com.paystub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDeleteSalaryRequest {
    private Long employeeId;
    private Long year;
    private Long month;
}
