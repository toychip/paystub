package com.paystub.controller;

import com.paystub.dto.ResponseDto;
import com.paystub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @GetMapping("/user")
    public String getTotalData(Model model) {

        List<ResponseDto> responseDtoList = userService.totalDataService();
        System.out.println( responseDtoList);
        model.addAttribute("totalData", responseDtoList);
        return "user";
    }
}
