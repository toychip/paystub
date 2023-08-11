package com.paystub.controller;

import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.FileUploadForm;
import com.paystub.dto.ResponseDto;
import com.paystub.dto.UserDto;
import com.paystub.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @GetMapping("/admin")
    public String getUploadPage(@RequestParam(required = false) Long year,
                                @RequestParam(required = false) Long month,
                                Model model) {
// --
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);

        // 매개변수가 제공되지 않은 경우 기본 값을 사용
        if (year == null) {
            year = (long) oneMonthAgo.getYear();
        }
        if (month == null) {
            month = (long) oneMonthAgo.getMonthValue();
        }
// --

        List<ResponseDto> responseDtos = uploadService.findResponseByYearAndMonth(year, month);
        model.addAttribute("responseDtos", responseDtos);
        return "admin";
    }

    @PostMapping("/admin")
    public String handleFileUpload(@ModelAttribute @Valid FileUploadForm form, BindingResult bindingResult,
                                   @RequestParam(required = false) Long year,
                                   @RequestParam(required = false) Long month, Model model) {

        MultipartFile file = form.getFile();

        if (year == null) {
            year = (long) LocalDate.now().minusMonths(1).getYear();
        }
        if (month == null) {
            month = (long) LocalDate.now().minusMonths(1).getMonthValue();
        }

        if (file.isEmpty()) {
            // 파일이 비어 있을 경우 오류 메시지 설정
            bindingResult.rejectValue("file", "error.file", "파일을 선택해주세요.");
        }

        List<ResponseDto> responseDtos = null;
        if (!bindingResult.hasErrors()) {
            // 엑셀 파일 처리 로직 작성
            responseDtos = uploadService.processExcelFile(file, bindingResult, year, month); // 결과를 받아옴
        }

        if (bindingResult.hasErrors()) {
            // 오류 메시지를 모델에 추가
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors", errors);

            // 현재 페이지 데이터를 다시 로드할 필요가 없음
            if (responseDtos == null) {
                responseDtos = uploadService.processExcelFile(file, bindingResult, year, month);
            }
            model.addAttribute("responseDtos", responseDtos);

            // 같은 뷰를 반환하여 현재 페이지에 오류를 표시
            return "admin";
        }

        return "redirect:/admin"; // 성공적인 업로드 후 리다이렉션
    }


}
