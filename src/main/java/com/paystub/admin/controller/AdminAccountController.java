package com.paystub.admin.controller;

import com.paystub.admin.service.ManagementUserService;
import com.paystub.user.dto.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminAccountController {

    private final ManagementUserService managementUserService;

    // 관리자 페이지에서 UserForm을 선택했을 시 페이지 로드하는 메서드
    @GetMapping("/adminUserForm")
    public String getAdminUserForm(Model model) {

        List<UserDao> userList = managementUserService.getAdminUserForm();
        model.addAttribute("adminUserForm", userList);
        return "admin/adminUserForm";
    }

    @PostMapping("/adminUserForm")
    public String deleteUsers(@RequestParam List<Long> employeeIds) {
        managementUserService.deleteUsersByIds(employeeIds);
        return "redirect:adminUserForm"; // 삭제 후 관리자 페이지로 리다이렉트
    }

}
