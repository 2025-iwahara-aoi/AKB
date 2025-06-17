package com.example.Morihara.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.FilterConfig;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        //型変換
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        String path = httpRequest.getServletPath();

        // 除外するパス
        if (path.equals("/") ||
                path.equals("/loading")) {
            chain.doFilter(request, response);
            return;
        }

        // セッション確認
        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            chain.doFilter(request, response);
        } else {
            // 新しいセッション作成してエラーメッセージをセット
            HttpSession newSession = httpRequest.getSession(true);
            newSession.setAttribute("errorMessageForm", "ログインしてください");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
        }
    }

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }
}
