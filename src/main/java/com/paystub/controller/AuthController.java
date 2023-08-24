package com.paystub.controller;

import com.paystub.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AuthController {

    // 로그인
    @GetMapping("/login")
    public String loginForm(@Validated Model model) {

        // 아이디와 비밀번호를 입력할 수 있는 빈껍데기를 담아서 모델에 담음
        model.addAttribute("loginDto", new LoginRequest());

        return "loginForm";

    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/logout";
    }

}
