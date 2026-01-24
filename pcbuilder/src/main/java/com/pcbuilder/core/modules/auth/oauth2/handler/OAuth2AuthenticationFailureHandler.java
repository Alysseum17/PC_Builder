package com.pcbuilder.core.modules.auth.oauth2.handler;

import com.pcbuilder.core.modules.auth.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.pcbuilder.core.modules.auth.utils.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler 
        extends SimpleUrlAuthenticationFailureHandler {
    
    private final HttpCookieOAuth2AuthorizationRequestRepository
        httpCookieOAuth2AuthorizationRequestRepository;
    
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String targetUrl = CookieUtils.getCookie(request,
            HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
            .map(Cookie::getValue)
            .orElse(("/"));

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("error", exception.getLocalizedMessage()) // Додаємо опис помилки
            .build().toUriString();

        httpCookieOAuth2AuthorizationRequestRepository
            .removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}