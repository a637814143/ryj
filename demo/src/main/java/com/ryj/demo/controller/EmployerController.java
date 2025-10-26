package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.entity.Employer;
import com.ryj.demo.service.EmployerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping
    public ApiResponse<Employer> create(@Valid @RequestBody Employer employer) {
        employer.setId(null);
        employerService.save(employer);
        return ApiResponse.success(employer);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody Employer employer) {
        employer.setId(id);
        return ApiResponse.success(employerService.updateById(employer));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(employerService.removeById(id));
    }

    @GetMapping("/{id}")
    public ApiResponse<Employer> detail(@PathVariable Long id) {
        return ApiResponse.success(employerService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<Employer>> list(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Employer> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Employer::getCompanyName, keyword);
        }
        Page<Employer> result = employerService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }
}
