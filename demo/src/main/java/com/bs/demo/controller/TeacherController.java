package com.bs.demo.controller;

import com.bs.demo.domain.Teacher;
import com.bs.demo.dto.GuidanceNoteRequest;
import com.bs.demo.dto.TeacherCreateRequest;
import com.bs.demo.service.EmploymentManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin
public class TeacherController {

    private final EmploymentManagementService service;

    public TeacherController(EmploymentManagementService service) {
        this.service = service;
    }

    @GetMapping
    public List<Teacher> listTeachers() {
        return service.listTeachers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Teacher createTeacher(@RequestBody TeacherCreateRequest request) {
        Teacher teacher = Teacher.builder()
                .name(request.getName())
                .department(request.getDepartment())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
        return service.createTeacher(teacher);
    }

    @PostMapping("/{id}/guidance")
    public Teacher addGuidance(@PathVariable Long id, @RequestBody GuidanceNoteRequest request) {
        return service.addGuidance(id, request.getStudentId(), request.getNote());
    }
}
