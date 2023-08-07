package com.paystub.dto;

import lombok.*;


@Getter
@Setter
@RequiredArgsConstructor
public class ResponseDto{

    private final EmployeeSalaryDto employeeSalaryDto;
    private final UserDto userDto;


}
