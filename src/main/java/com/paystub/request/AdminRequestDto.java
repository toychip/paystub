package com.paystub.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class AdminRequestDto {

    private Long year;
    private Long month;
    private String name;
    private Long employeeID;
    public AdminRequestDto() {
        this.year = 9999L;
        this.month = 0L;
    }
}
