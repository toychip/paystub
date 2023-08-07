package com.paystub.controller;

import com.paystub.dto.EmployeeSalaryDto;
import com.paystub.dto.ResponseDto;
import com.paystub.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @GetMapping("/upload")
    public String getUploadPage() {

        return "uploadForm";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        // 엑셀 파일 처리 로직 작성
        // JDBC를 사용하여 처리된 데이터를 데이터베이스에 저장

        // 엑셀 파일 처리 로직 작성
        List<ResponseDto> responseDtos = uploadService.excelToDto(file);
        model.addAttribute("employees", responseDtos);
        return "endForm";  // 업로드 상태를 표시하는 페이지로 리디렉션
    }

}
