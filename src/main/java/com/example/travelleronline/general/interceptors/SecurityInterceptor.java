package com.example.travelleronline.general.interceptors;

import com.example.travelleronline.general.exceptions.UnauthorizedException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityInterceptor implements HandlerInterceptor {

    public static final String LOGGED = "logged";
    public static final String REMOTE_ADDRESS = "remote_address";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String uri = request.getRequestURI();
        if (uri.contains("registration") ||
        uri.contains("email-verification") ||
        uri.contains("login") ||
        uri.contains("logout")) {
            return true;
        }
        HttpSession session = request.getSession();
        String ip = request.getRemoteAddr();
        if (session.getAttribute(LOGGED) == null ||
                !(boolean) session.getAttribute(LOGGED) ||
                session.getAttribute(REMOTE_ADDRESS) == null ||
                !session.getAttribute(REMOTE_ADDRESS).equals(ip)) {
            throw new UnauthorizedException("You should log in first.");
        }
        return true;
    }

}