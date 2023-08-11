package com.paystub.controller;

import com.paystub.dto.ResponseDto;
import com.paystub.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @GetMapping("/adminSelect")
    public String getAdminSelect() {
        return "adminSelect";
    }

    @GetMapping("/admin")
    public String getUploadPage(@RequestParam(required = false) Long year,
                                @RequestParam(required = false) Long month,
                                Model model) {

        List<ResponseDto> responseDtos = uploadService.findAllResponse();
        model.addAttribute("responseDtos", responseDtos);
        return "admin";
    }

    @PostMapping("/admin")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        System.out.println("UploadController.handleFileUpload");

        // 엑셀 파일 처리 로직 작성
        uploadService.processExcelFile(file);

        return "redirect:/admin";  // 업로드 상태를 표시하는 페이지로 리디렉션

    }



}
