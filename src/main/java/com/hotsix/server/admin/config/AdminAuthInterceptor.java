package com.hotsix.server.admin.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AdminProperties adminProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 로그인 API는 인증 제외
        if (request.getRequestURI().equals("/api/v1/admin/login")) {
            log.info("관리자 로그인 시도 - IP: {}", request.getRemoteAddr());
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            log.warn("관리자 인증 실패(헤더 없음) - IP: {}", request.getRemoteAddr());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"인증에 실패했습니다.\"}");
            return false;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                throw new IllegalArgumentException("잘못된 인증 헤더 형식입니다.");
            }

            String username = values[0];
            String password = values[1];

            if (!username.equals(adminProperties.getUsername()) || !password.equals(adminProperties.getPassword())) {
                log.warn("관리자 인증 실패 - IP: {}, Username: {}", request.getRemoteAddr(), username);
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\": \"인증에 실패했습니다.\"}");
                return false;
            }

            log.info("관리자 인증 성공 - IP: {}, Username: {}", request.getRemoteAddr(), username);

        } catch (IllegalArgumentException e) {
            log.warn("관리자 인증 예외 - IP: {}, Error: {}", request.getRemoteAddr(), e.getMessage());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"인증에 실패했습니다.\"}");
            return false;
        }

        return true;
    }
}