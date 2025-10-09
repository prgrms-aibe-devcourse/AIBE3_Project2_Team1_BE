package com.hotsix.server.auth.resolver;

import com.hotsix.server.auth.exception.AuthErrorCase;
import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.global.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && (Long.class.equals(parameter.getParameterType())
                || long.class.equals(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mav,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Long l) { return l; }
            if (principal instanceof Integer i) { return i.longValue(); }
            if (principal instanceof String s && s.chars().allMatch(Character::isDigit)) {
                return Long.parseLong(s);
            }
            String name = auth.getName();
            if (name != null && name.chars().allMatch(Character::isDigit)) {
                return Long.parseLong(name);
            }
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String authz = request != null ? request.getHeader("Authorization") : null;
        if (StringUtils.hasText(authz) && authz.startsWith("Bearer ")) {
            String token = authz.substring(7);
            try {
                Long userId = jwtTokenProvider.getUserId(token);
                if (userId != null) { return userId; }
            } catch (IllegalArgumentException e) {
                throw new ApplicationException(AuthErrorCase.UNAUTHORIZED);
            }
        }

        throw new ApplicationException(AuthErrorCase.UNAUTHORIZED);
    }
}
