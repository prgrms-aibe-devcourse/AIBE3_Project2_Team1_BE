package com.hotsix.server.global.config;

import com.hotsix.server.user.entity.Provider;
import com.hotsix.server.user.repository.UserRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import jakarta.servlet.http.HttpServletRequest;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;
    private final UserRepository userRepository;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo,
                                              String baseUri,
                                              UserRepository userRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, baseUri);
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        return customize(req, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
        return customize(req, request);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest req, HttpServletRequest request) {
        if (req == null) return null;

        // 강제로 동의 페이지 띄우기
        return OAuth2AuthorizationRequest.from(req)
                .additionalParameters(params -> params.put("prompt", "login"))
                .build();
    }


    private boolean checkFirstLogin(HttpServletRequest request) {
        String registrationId = extractRegistrationId(request);
        if (registrationId == null) return false;

        Provider provider;
        try {
            provider = Provider.valueOf(registrationId.toUpperCase());
        } catch (Exception e) {
            return false;
        }

        // 이미 DB에 사용자 있는지 확인
        return userRepository.findByProvider(provider).isEmpty();
    }

    private String extractRegistrationId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String[] parts = uri.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : null;
    }
}
