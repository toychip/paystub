package com.paystub.admin.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;


@Getter
@Setter
public class AdminSearchRequest {

    private Long year;
    private Long month;
    private String name;
    @Digits(integer = 10, fraction = 0, message = "사번은 숫자만 입력가능합니다.")
    private Long employeeID;
    public AdminSearchRequest() {
        this.year = 9999L;
        this.month = 0L;
    }
}
