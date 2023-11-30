package com.paystub.comon.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ExelTransObjectUtil {
    public BigDecimal getNumericValueOrNull(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        try {
            return new BigDecimal(cell.getNumericCellValue());
        } catch (NumberFormatException e) {
            // 여기에 로그 기록 또는 다른 처리 방법 작성
            System.err.println("NumberFormatException in getNumericValueOrNull: " + e.getMessage());
            return null; // 또는 적절한 기본값 설정
        }
    }

    public String getStringValueOrNull(Cell cell) {
        if (cell == null) {
            return null;
        }

        CellType cellType = cell.getCellType();

        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                // 날짜형식의 셀일 경우, 날짜를 읽어서 문자열로 변환
                Date date = cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  // 날짜 포맷 지정
                return sdf.format(date);
            } else {
                // 숫자형 셀일 경우, 숫자를 문자열로 변환
                return Double.toString(cell.getNumericCellValue());
            }
        } else {
            // 그 외의 셀 타입일 경우, null 반환
            return null;
        }
    }
}
