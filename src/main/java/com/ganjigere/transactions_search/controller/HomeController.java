package com.ganjigere.transactions_search.controller;

import com.ganjigere.transactions_search.business.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//@Controller
@RestController
public class HomeController {

    @Autowired
    SearchService searchService;

    @GetMapping(path = "/home")
    public String homeLanding() {
        return "home";
    }

    @GetMapping(path = "/home/orders")
    public Object getOrders() {
        return Map.of("key1", "value1", "key2", "value2");
    }

    @GetMapping(path = "/home/setup")
    public boolean setupData() {
        searchService.addRecords();
        return true;
    }

}
