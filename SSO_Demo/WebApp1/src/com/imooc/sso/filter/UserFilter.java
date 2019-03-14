package com.imooc.sso.filter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class UserFilter implements Filter {

    private String server;

    private String app;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        server = filterConfig.getInitParameter("Server");
         app = filterConfig.getInitParameter("app");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ticket = null;
        if (null != ((HttpServletRequest)request).getCookies()){
            for (Cookie cookie : ((HttpServletRequest)request).getCookies()){
                if(Objects.equals(cookie.getName(), "Ticket_Granting_Ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (!Objects.equals(null, ticket)){
            // 进行用户校验如果不是用户或者用户非法，需要跳转到登录页面
            chain.doFilter(request, response);
            return;
        }

        ticket = request.getParameter("ticket");
        if(!Objects.equals(null, ticket) && !Objects.equals("", ticket.trim())){
            ((HttpServletResponse)response).addCookie(new Cookie("Tickect_Granting_Ticket", ticket));// key-value
            chain.doFilter(request, response);
        }else{
            ((HttpServletResponse)response).sendRedirect(server + "/ssoLogin?source=" + app);
        }
    }

    @Override
    public void destroy() {

    }
}
