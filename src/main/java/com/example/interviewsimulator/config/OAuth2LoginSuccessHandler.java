package com.example.interviewsimulator.config;

import com.example.interviewsimulator.model.LoginInfo;
import com.example.interviewsimulator.repository.LoginInfoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginInfoRepository loginInfoRepository;

    public OAuth2LoginSuccessHandler(LoginInfoRepository loginInfoRepository) {
        this.loginInfoRepository = loginInfoRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String name = user.getAttribute("name");
        String email = user.getAttribute("email");

        LoginInfo loginInfo = new LoginInfo(name, email, LocalDateTime.now());
        loginInfoRepository.save(loginInfo);

        
    // ✅ Store email & name in session
    request.getSession().setAttribute("email", email);
    request.getSession().setAttribute("name", name);

        // Redirect after saving login info
        response.sendRedirect("/api/interview/redirect-after-login");
    }
}
