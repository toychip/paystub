package com.paystub.controller;

import com.paystub.dto.LoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public String loginForm(Model model) {

        // 아이디와 비밀번호를 입력할 수 있는 빈껍데기를 담아서 모델에 담음
        model.addAttribute("loginDto", new LoginDto());
        return "loginForm";
    }

}
