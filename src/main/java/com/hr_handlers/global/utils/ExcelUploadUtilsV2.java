package com.hr_handlers.global.utils;

import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

@Component
public class ExcelUploadUtilsV2 implements ExcelUtilMethodFactory {
    public <T> List<T> parseExcelToObject(MultipartFile file, Class<T> clazz) throws IOException, InterruptedException  {
        IOUtils.setByteArrayMaxOverride(300_000_000); // 레코드 크기 300MB까지 허용
//        ZipSecureFile.setMinInflateRatio(0);

        System.gc(); // GC 유도
        Thread.sleep(100); // GC 안정 시간

        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();          // 최대 힙 메모리
        long totalMemory = runtime.totalMemory();      // 현재 할당된 힙 메모리
        long freeMemory = runtime.freeMemory();        // 사용 가능한 힙 메모리
        long usedMemory = totalMemory - freeMemory;    // 실제 사용 중인 메모리

        System.out.printf("Max Memory: %d MB%n", maxMemory / (1024 * 1024));
        System.out.printf("Total Memory: %d MB%n", totalMemory / (1024 * 1024));
        System.out.printf("freeMemory: %d MB%n", freeMemory / (1024 * 1024));
        System.out.printf("Used Memory: %d MB%n", usedMemory / (1024 * 1024));

        long beforeUsedMem = getUsedMemory();
        System.out.printf("Before: %d MB%n", beforeUsedMem / (1024 * 1024));

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        System.gc();
        Thread.sleep(100); // GC 안정 시간

        long afterUsedMem = getUsedMemory();
        System.out.printf("After: %d MB%n", afterUsedMem / (1024 * 1024));

        long memoryUsedByApi = afterUsedMem - beforeUsedMem;
        System.out.printf("Memory used by API: %d MB%n", memoryUsedByApi / (1024 * 1024));

        parseHeader(sheet, clazz);
        return parseBody(sheet, clazz);
    }

    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public <T> void parseHeader(Sheet sheet, Class<T> clazz) {
        Set<String> excelHeaders = new HashSet<>();
        Set<String> classHeaders = new HashSet<>();
        int headerStartRowToParse = 0;

        sheet.getRow(headerStartRowToParse).cellIterator()
                .forEachRemaining(e -> excelHeaders.add(e.getStringCellValue()));

        Arrays.stream(clazz.getDeclaredFields())
                .filter(e -> e.isAnnotationPresent(ExcelColumn.class))
                .forEach(e -> {
                    if (e.getAnnotation(ExcelColumn.class).headerName().equals("")) {
                        classHeaders.add(e.getName());
                    }
                    else {
                        classHeaders.add(e.getAnnotation(ExcelColumn.class).headerName());
                    }
                });

        if (!excelHeaders.containsAll(classHeaders)) {
            // 업로드한 엑셀 헤더가 엑셀Dto에 선언한 내용과 불일치 할경우
            //todo exception 따로처리
            throw new IllegalStateException("헤더 불일치.");
        }
    }

    public <T> List<T> parseBody(Sheet sheet, Class<T> clazz) {
        List<T> objects = new ArrayList<>();
        try {
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            var fillUpFromRowMethod = clazz.getMethod("fillUpFromRow", Row.class);

            for(int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getPhysicalNumberOfCells() == 0) continue;

                T object = constructor.newInstance();
                fillUpFromRowMethod.invoke(object, row);
                objects.add(object);
            }
        } catch (NoSuchMethodException e) {
            // todo: exception 따로처리
            throw new IllegalArgumentException("클래스 " + clazz.getName() + "에 fillUpFromRow 메서드가 없습니다.", e);
        } catch (Exception e) {
            // todo: exception 따로처리
            throw new RuntimeException("Excel 데이터 변환 중 오류가 발생했습니다.", e);
        }
        return objects;
    }

    public <T> void renderObjectToExcel(OutputStream stream, List<T> data, Class<T> clazz) throws IOException, IllegalAccessException {
        /* create workbook & sheet */
        Workbook workbook = WorkbookFactory.create(true);
        Sheet sheet = workbook.createSheet();

        /* render header & body */
        renderHeader(sheet, clazz);
        renderBody(sheet, data, clazz);

        /* close stream */
        workbook.write(stream);
        workbook.close();
    }

    public <T> void renderHeader(Sheet sheet, Class<T> clazz) {
        int headerStartRowToRender = 1;
        int startColToRender = 1;

        Row row = sheet.createRow(headerStartRowToRender);
        int colIdx = startColToRender;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                String headerName = field.getAnnotation(ExcelColumn.class).headerName();

                Cell cell = row.createCell(colIdx, CellType.STRING);

                CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
                cellStyle.setBorderTop(BorderStyle.MEDIUM);
                cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                cellStyle.setBorderLeft(BorderStyle.MEDIUM);
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellValue(headerName.equals("") ? field.getName() : headerName);
                cell.setCellStyle(cellStyle); // 색상 적용
                colIdx++;
            }
        }

        // ExcelDownloaddto에 ExcelColumn이 없을때 예외처리
        if (colIdx == startColToRender) {
            throw new GlobalException(ErrorCode.EXCEL_HEADER_NOT_FOUND);
        }
    }

    public <T> void renderBody(Sheet sheet, List<T> data, Class<T> clazz) throws IllegalAccessException {
        int rowIdx = 2;
        int startColToRender = 1;

        // todo : 나중에 리팩토링 ㄱㄱ
        Workbook workbook = sheet.getWorkbook();
        CellStyle highlightStyle = workbook.createCellStyle();
        highlightStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        highlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle midSumStyle = workbook.createCellStyle();
        midSumStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        midSumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle totalSumStyle = workbook.createCellStyle();
        totalSumStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        totalSumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle teamSumStyle = workbook.createCellStyle();
        teamSumStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        teamSumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle grandTotalStyle = workbook.createCellStyle();
        grandTotalStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        grandTotalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        for (T datum : data) {
            Row row = sheet.createRow(rowIdx);
            int colIdx = startColToRender;

            CellStyle currentRowStyle = null;

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // private 필드에 접근하기 위해
                String value = field.get(datum) == null ? "" : String.valueOf(field.get(datum));

                Cell cell = row.createCell(colIdx, CellType.STRING);

                CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);

                cell.setCellValue(Objects.requireNonNullElse(value, ""));
                cell.setCellStyle(cellStyle); // 색상 적용

                colIdx++;


                // 특정 행 타입을 확인하여 스타일 지정
                // todo : 나중에 리팩토링 ㄱㄱ
                if (value.equals("중간 합계")) {
                    currentRowStyle = midSumStyle;
                } else if (value.equals("총 합계")) {
                    currentRowStyle = totalSumStyle;
                } else if (value.equals("팀간 합계")) {
                    currentRowStyle = teamSumStyle;
                } else if (value.equals("합계")) {
                    currentRowStyle = grandTotalStyle;
                }
            }

            if (currentRowStyle != null) {
                for (int i = 1; i <= row.getPhysicalNumberOfCells(); i++) {

                    CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
                    cellStyle.cloneStyleFrom(currentRowStyle); // 기존 스타일 복사

                    if (i == 1) {
                        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
                        cellStyle.setBorderTop(BorderStyle.MEDIUM);
                        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                    } else if (i == row.getPhysicalNumberOfCells()) {
                        cellStyle.setBorderRight(BorderStyle.MEDIUM);
                        cellStyle.setBorderTop(BorderStyle.MEDIUM);
                        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                    } else {
                        cellStyle.setBorderTop(BorderStyle.MEDIUM);
                        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                    }
                    row.getCell(i).setCellStyle(cellStyle); // 색상 적용
                }
            }
            rowIdx++;
        }
    }
}
