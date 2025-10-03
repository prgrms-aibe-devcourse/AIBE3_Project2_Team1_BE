package com.hotsix.server.admin.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AdminProperties adminProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 로그인 API는 인증 제외
        if (request.getRequestURI().equals("/api/v1/admin/login")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"인증 정보가 없습니다. 관리자 계정으로 로그인해주세요.\"}");
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
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\": \"아이디 또는 비밀번호가 올바르지 않습니다.\"}");
                return false;
            }

        } catch (IllegalArgumentException e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"잘못된 인증 정보입니다. 관리자 계정 확인이 필요합니다.\"}");
            return false;
        }

        return true;
    }
}