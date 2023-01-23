package xyz.xiashuo.simplethymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author xiashuo
 * @date 2023/1/18 10:59
 */
@Controller
@RequestMapping(value = "/")
public class IndexController {

    @RequestMapping("/")
    public String sayHello() {
        return "index";
    }

    @RequestMapping("/test0")
    public String test0(Model model) {
        model.addAttribute("name", "xiashuo");
        return "test0";
    }

}
