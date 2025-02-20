package com.ganjigere.transactions_search.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

//@Controller
@RestController
public class HomeController {

    @GetMapping(path = "/home")
    public String homeLanding() {
        return "home";
    }

    @GetMapping(path="/home/orders")
    public Object getOrders() {
        return Map.of("key1", "value1", "key2", "value2");
    }


}
