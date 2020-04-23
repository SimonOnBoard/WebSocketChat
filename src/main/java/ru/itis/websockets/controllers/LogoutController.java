package ru.itis.websockets.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Controller
public class LogoutController {
    @GetMapping("/logout")
    public ModelAndView confirmLogout(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        Cookie[] cookies = httpServletRequest.getCookies();
        for(Cookie cookie: cookies) {
            cookie.setMaxAge(0);
            cookie.setValue(null);
            httpServletResponse.addCookie(cookie);
        }
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("logout",true);
        return new ModelAndView("redirect:/login");
    }
}
