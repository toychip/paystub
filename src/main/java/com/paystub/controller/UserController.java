package com.paystub.controller;

import com.paystub.dto.*;
import com.paystub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {


    private final UserService userService;

    @GetMapping("/userSelect")
    public String getUserSelect() {
        return "userSelect";
    }

    @GetMapping("/user")
    public String getTotalData(@RequestParam Integer year, @RequestParam Integer month, Model model) {

        List<UserFormDto> userDtoList = userService.totalDataService(year, month);
        model.addAttribute("totalData", userDtoList);

        return "user";
    }

    @GetMapping("/afterSelect")
    public ResponseEntity<Map<String, Object>> getPage(@RequestParam Integer year, @RequestParam Integer offset) {
        final int limit = 4;
        List<PageDto> pageDtoList = userService.getPage(year, limit, offset * limit);
        int totalRecords = userService.getTotalRecords(year);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);

        Map<String, Object> response = new HashMap<>();
        response.put("yearAndMonth", pageDtoList);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }






}
