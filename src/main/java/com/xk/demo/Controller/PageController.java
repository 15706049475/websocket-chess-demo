package com.xk.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping({"/","chess"})
    public String toChess(){
        return "chess";
    }

    @RequestMapping("/index")
    public String toIndex(){
        return "index";
    }
}
