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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // 사용자 년,월 조회 페이지 로드하는 메서드
    @GetMapping("/userSelect")
    public String getUserSelect() {
        return "userSelect";
    }

    // 사용자가 년, 월을 모두 선택 후 조회했을 시 로드하는 메서드
    @GetMapping("/user")
    public String getTotalData(@RequestParam Integer year, @RequestParam Integer month, Model model) {

        // EmployeeSalary 와 User 테이블을 조인하여 필요한 데이터만 담아온다.
        List<UserFormDto> userDtoList = userService.totalDataService(year, month);
        model.addAttribute("totalData", userDtoList);

        return "user";
    }

    // 사용자가 년, 월 중 하나라도 "전체" 옵션을 선택했을 시 로드하는 메서드
    @GetMapping("/afterSelect")
    public ResponseEntity<Map<String, Object>> getPage(@RequestParam Integer year, @RequestParam Integer offset) {

        // 쿼리에 사용할 limit를 하드코딩이 아닌 변수로 지정
        final int limit = 4;

        // 조건에 맞는 데이터만 담아온다.
        List<PageDto> pageDtoList = userService.getPage(year, limit, offset * limit);

        // 조건에 맞는 데이터들의 갯수를 센다.
        int totalRecords = userService.getTotalRecords(year);

        // 데이터들의 갯수를 limit로 나누어 한 페이지에 보일 리스트 갯수를 지정
        int totalPages = (int) Math.ceil((double) totalRecords / limit);

        Map<String, Object> response = new HashMap<>();
        response.put("yearAndMonth", pageDtoList);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }






}
