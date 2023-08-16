package com.paystub.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class PageDto {
    private Integer year; // 년도
    private Integer month; // 월
}
