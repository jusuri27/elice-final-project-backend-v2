package com.hr_handlers.todo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.hr_handlers.todo.dto.HolidayResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

    @Value("${spring.holiday.service.key}")
    private String serviceKey;

    private final String HOLIDAY_API_URL = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SuccessResponse<List<HolidayResponseDto>> getHolidays(int year, int month) {
        try {
            String formattedMonth = String.format("%02d", month);

            String decodedServiceKey = URLDecoder.decode(serviceKey, StandardCharsets.UTF_8);

            String url = UriComponentsBuilder.fromHttpUrl(HOLIDAY_API_URL)
                    .queryParam("serviceKey", decodedServiceKey)
                    .queryParam("solYear", year)
                    .queryParam("solMonth", formattedMonth)
                    .queryParam("_type", "json")
                    .build()
                    .toUriString();

            log.info("Holiday API Request URL: {}", url);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();
            log.info("Holiday API Response: {}", responseBody);

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            List<HolidayResponseDto> holidays = new ArrayList<>();

            JsonNode items = jsonNode.path("response").path("body").path("items").path("item");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    holidays.add(new HolidayResponseDto(
                            item.get("locdate").asText(),
                            item.get("dateName").asText(),
                            "Y".equals(item.get("isHoliday").asText())
                    ));
                }
            } else if (!items.isMissingNode() && !items.isNull()) {
                holidays.add(new HolidayResponseDto(
                        items.get("locdate").asText(),
                        items.get("dateName").asText(),
                        "Y".equals(items.get("isHoliday").asText())
                ));
            }

            return SuccessResponse.of("공휴일 조회 성공", holidays);

        } catch (Exception e) {
            log.error("Failed to get holidays", e);
            throw new GlobalException(ErrorCode.HOLIDAY_API_ERROR);
        }
    }


    @SuppressWarnings("unchecked")
    private List<HolidayResponseDto> parseJsonResponse(Map<String, Object> responseMap) {
        List<HolidayResponseDto> holidays = new ArrayList<>();
        try {
            Map<String, Object> response = (Map<String, Object>) responseMap.get("response");
            Map<String, Object> body = (Map<String, Object>) response.get("body");
            Map<String, Object> items = (Map<String, Object>) body.get("items");

            if (items != null && items.get("item") != null) {
                Object item = items.get("item");

                if (item instanceof List) {
                    List<Map<String, Object>> itemList = (List<Map<String, Object>>) item;
                    for (Map<String, Object> holiday : itemList) {
                        holidays.add(createHolidayDto(holiday));
                    }
                } else if (item instanceof Map) {
                    Map<String, Object> holiday = (Map<String, Object>) item;
                    holidays.add(createHolidayDto(holiday));
                }
            }

            return holidays;
        } catch (Exception e) {
            log.error("Failed to parse JSON response", e);
            throw new GlobalException(ErrorCode.HOLIDAY_PARSE_ERROR);
        }
    }

    private HolidayResponseDto createHolidayDto(Map<String, Object> holiday) {
        return new HolidayResponseDto(
                String.valueOf(holiday.get("locdate")),
                (String) holiday.get("dateName"),
                "Y".equals(holiday.get("isHoliday"))
        );
    }
}