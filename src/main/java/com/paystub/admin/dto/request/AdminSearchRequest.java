package com.paystub.admin.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;


@Getter
@Setter
public class AdminSearchRequest {

    private Long year;
    private Long month;
    private String name;
    @Pattern(regexp = "^[0-9]*$", message = "사번은 숫자만 입력 가능합니다!")
    private String employeeID;
    public AdminSearchRequest() {
        this.year = 9999L;
        this.month = 0L;
    }
}
