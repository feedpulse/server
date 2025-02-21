package de.feedpulse.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @RequestMapping(produces = "application/json")
    public String home() {
        return "{\"message\": \"Hello World!\"}";
    }

    @RequestMapping(produces = "text/html")
    public String homeHtml() {
        return "<h1>Hello World!</h1>";
    }

}
