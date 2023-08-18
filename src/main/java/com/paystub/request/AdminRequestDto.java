package com.paystub.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Digits;


@Getter
@Setter
public class AdminRequestDto {

    private Long year;
    private Long month;
    private String name;
    @Digits(integer = 10, fraction = 0, message = "사번은 숫자만 입력해 주세요.")
    private Long employeeID;
    public AdminRequestDto() {
        this.year = 9999L;
        this.month = 0L;
    }
}
