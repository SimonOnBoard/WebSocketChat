package ru.itis.websockets.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class IndexController {
    @GetMapping("/")
    public ModelAndView getIndexPage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("start");
        return mv;
    }
}
