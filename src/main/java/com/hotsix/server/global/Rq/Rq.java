package com.hotsix.server.global.Rq;

import com.hotsix.server.global.config.security.jwt.JwtAuthentication;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final UserRepository userRepository;

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication instanceof JwtAuthentication)) {
            throw new ApplicationException(UserErrorCase.UNAUTHORIZED);
        }

        Long userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.USER_NOT_FOUND));
    }

    public void setCookie(String name, String value) {
        if (value == null) value = "";
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        String serverName = req.getServerName();

        if (serverName != null && serverName.contains(".")) {
            cookie.setDomain(serverName);
        }

        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");

        if (value.isBlank()) {
            cookie.setMaxAge(0);
        } else {
            cookie.setMaxAge(60 * 60 * 24 * 365); // 1년
        }

        resp.addCookie(cookie);
    }

    public void deleteCookie(String name) {
        setCookie(name, null);
    }

    @SneakyThrows
    public void sendRedirect(String url) {
        resp.sendRedirect(url);
    }
}
