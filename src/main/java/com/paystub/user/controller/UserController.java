package com.paystub.user.controller;

import com.paystub.comon.util.PageUtil;
import com.paystub.user.dto.response.UserResponse;
import com.paystub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // 쿼리에 사용할 limit를 하드코딩이 아닌 변수로 지정
    private final int limit = 6;

    // 사용자 년,월 조회 페이지 로드하는 메서드
    @GetMapping("/userPaging")
    public String getUserSelect() {
        return "user/userPaging";
    }


    // 급여명세서 팝업창 메서드
    @GetMapping("/userSalary")
    public String getTotalData(@RequestParam Integer year, @RequestParam Integer month, Model model) {

        // EmployeeSalary 와 User 테이블을 조인하여 필요한 데이터만 담아온다.
        List<UserResponse> userDtoList = userService.getTotalData(year, month);

        if(month == 12) {
            year++;
            month = 1;
        }
        else {
            month++;
        }
        String payDay = year + "-" + month + "-14";

        model.addAttribute("payDay", payDay);
        model.addAttribute("totalData", userDtoList);

        return "user/userSalary";
    }

    // 사용자가 년, 월 옵션을 선택했을 시 로드하는 메서드
    @GetMapping("/afterPaging")
    public ResponseEntity<Map<String, Object>> getPage(@RequestParam(defaultValue = "9999") Integer year,
                                                       @RequestParam(defaultValue = "0") Integer month,
                                                       @RequestParam Integer offset) {

        // 조건에 맞는 데이터만 담아온다.
        List<PageUtil> pageUtilList = userService.getPage(year, month, limit, offset * limit);

        // 조건에 맞는 데이터들의 갯수를 센다.
        int totalRecords = userService.getTotalRecords(year, month);

        // 데이터들의 갯수를 limit로 나누어 한 페이지에 보일 리스트 갯수를 지정
        int totalPages = (int) Math.ceil((double) totalRecords / limit);

        Map<String, Object> response = new HashMap<>();
        response.put("yearAndMonth", pageUtilList);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }

}