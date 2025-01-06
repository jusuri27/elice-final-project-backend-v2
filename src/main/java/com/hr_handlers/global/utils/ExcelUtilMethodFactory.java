package com.hr_handlers.global.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ExcelUtilMethodFactory {
    public <T> List<T> parseExcelToObject(MultipartFile file, Class<T> clazz)  throws IOException;
    public <T> void renderObjectToExcel(OutputStream stream, List<T> data, Class<T> clazz) throws IOException, IllegalAccessException;


    public <T> void parseHeader(Sheet sheet, Class<T> clazz);
    public <T> List<T> parseBody(Sheet sheet, Class<T> clazz);

    public <T> void renderHeader(Sheet sheet, Class<T> clazz);
    public <T> void renderBody(Sheet sheet, List<T> data, Class<T> clazz) throws IllegalAccessException;
}