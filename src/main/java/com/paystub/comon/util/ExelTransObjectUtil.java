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
        return new BigDecimal(cell.getNumericCellValue());
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
            }
            // 숫자형 셀일 경우, 그냥 null 반환 (필요하다면 이 부분을 수정할 수 있음)
            return null;
        } else {
            // 그 외의 셀 타입일 경우, null 반환
            return null;
        }
    }
}
