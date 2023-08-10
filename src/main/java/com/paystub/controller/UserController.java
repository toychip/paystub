package com.paystub.controller;

import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.ResponseDto;
import com.paystub.dto.UserDto;
import com.paystub.dto.UserFormDto;
import com.paystub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {


    private final UserService userService;

    @GetMapping("/user")
    public String getTotalData(Model model) {

        List<UserFormDto> userDtoList = userService.totalDataService();
        model.addAttribute("totalData", userDtoList);

        for (UserFormDto userFormDto : userDtoList) {
            log.info(String.valueOf(userFormDto.getDeductionTotal()));
            log.info(String.valueOf(userFormDto.getElderlyCareInsurance()));
        }
        return "user";
    }
}
