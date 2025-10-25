package com.bs.demo.controller;

import com.bs.demo.service.EmploymentManagementService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final EmploymentManagementService service;

    public DashboardController(EmploymentManagementService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return service.getDashboardSummary();
    }
}
