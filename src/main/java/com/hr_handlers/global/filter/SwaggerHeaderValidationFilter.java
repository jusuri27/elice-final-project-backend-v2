//package com.hr_handlers.global.filter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class SwaggerHeaderValidationFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // Swagger 관련 경로 필터링
//        if (request.getRequestURI().startsWith("/v3/api-docs") ||
//                request.getRequestURI().startsWith("/swagger-ui")) {
//
//            String apiKey = request.getHeader("X-API-KEY");
//
//            if (apiKey == null || apiKey.isEmpty()) {
//                log.error("헤더에 'X-API-KEY'가 누락되었습니다.");
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
//                response.setContentType("application/json");
//                response.getWriter().write("{\"error\": \"X-API-KEY is required in headers.\"}");
//                return; // 필터 체인 종료
//            }
//
//            // 추가 검증 로직 (API 키 값 확인)
//            if (!"valid-api-key".equals(apiKey)) {
//                log.error("잘못된 'X-API-KEY' 값입니다.");
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
//                response.setContentType("application/json");
//                response.getWriter().write("{\"error\": \"Invalid API key.\"}");
//                return; // 필터 체인 종료
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}