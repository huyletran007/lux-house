package com.luxhouse.main.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.luxhouse.main.domain.Users;
import com.luxhouse.main.service.SessionService;
import com.luxhouse.main.service.UserService;

@Component
public class SessionAdminInterceptor implements HandlerInterceptor {
    @Autowired
    SessionService sessionService;
    
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (sessionService.get("loginAdmin", null) != null) {
            Users users = (Users) sessionService.get("loginAdmin");
            Users user = userService.findById(users.getId()).get();
                    
            user.setOrders(null);
            request.setAttribute("AdminDataRoot", user);
        }
    
        return true;
    }

}
