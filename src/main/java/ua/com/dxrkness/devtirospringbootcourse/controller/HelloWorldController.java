package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello not quarkus!!";
    }
}
