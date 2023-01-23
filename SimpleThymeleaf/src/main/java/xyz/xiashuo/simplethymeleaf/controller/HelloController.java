package xyz.xiashuo.simplethymeleaf.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiashuo
 * @date 2023/1/18 10:49
 */
@RestController
@RequestMapping(value = "/Hello")
public class HelloController {

    // http://localhost:8082/SpringBoot-Thymeleaf/Hello/sayHello
    @RequestMapping("/sayHello")
    public String sayHello() {
        return "Hello, Spring Boot 2!";
    }

}
