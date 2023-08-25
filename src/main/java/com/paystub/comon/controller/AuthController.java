package com.paystub.comon.controller;

import com.paystub.comon.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class AuthController {

    // 로그인
    @GetMapping("/login")
    public String loginForm(@Validated Model model, HttpServletRequest request) {

        // 아이디와 비밀번호를 입력할 수 있는 빈껍데기를 담아서 모델에 담음
        model.addAttribute("loginDto", new LoginRequest());

        // 세션에서 errorMessage를 가져와 모델에 추가
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage"); // 메시지를 사용한 후 세션에서 삭제
        }

        return "loginForm";

    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/logout";
    }

}
