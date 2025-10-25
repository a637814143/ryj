package com.bs.demo.controller;

import com.bs.demo.domain.EmploymentIntention;
import com.bs.demo.domain.JobApplication;
import com.bs.demo.domain.Resume;
import com.bs.demo.domain.Student;
import com.bs.demo.dto.EmploymentIntentionRequest;
import com.bs.demo.dto.ResumeRequest;
import com.bs.demo.dto.StudentCreateRequest;
import com.bs.demo.dto.StudentUpdateRequest;
import com.bs.demo.service.EmploymentManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin
public class StudentController {

    private final EmploymentManagementService service;

    public StudentController(EmploymentManagementService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> listStudents() {
        return service.listStudents();
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return service.getStudent(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@RequestBody StudentCreateRequest request) {
        Student student = Student.builder()
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .major(request.getMajor())
                .email(request.getEmail())
                .phone(request.getPhone())
                .biography(request.getBiography())
                .educationHistory(request.getEducationHistory())
                .practiceExperience(request.getPracticeExperience())
                .awards(request.getAwards())
                .build();
        return service.createStudent(student);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody StudentUpdateRequest request) {
        Student update = Student.builder()
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .major(request.getMajor())
                .email(request.getEmail())
                .phone(request.getPhone())
                .biography(request.getBiography())
                .educationHistory(request.getEducationHistory())
                .practiceExperience(request.getPracticeExperience())
                .awards(request.getAwards())
                .build();
        return service.updateStudent(id, update);
    }

    @PutMapping("/{id}/intention")
    public EmploymentIntention updateIntention(@PathVariable Long id, @RequestBody EmploymentIntentionRequest request) {
        EmploymentIntention intention = EmploymentIntention.builder()
                .expectedPosition(request.getExpectedPosition())
                .salaryRange(request.getSalaryRange())
                .preferredCities(request.getPreferredCities())
                .workType(request.getWorkType())
                .notes(request.getNotes())
                .build();
        return service.updateIntention(id, intention);
    }

    @PostMapping("/{id}/resumes")
    @ResponseStatus(HttpStatus.CREATED)
    public Resume createResume(@PathVariable Long id, @RequestBody ResumeRequest request) {
        Resume resume = Resume.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .skills(request.getSkills())
                .experiences(request.getExperiences())
                .attachments(request.getAttachments())
                .portfolioUrl(request.getPortfolioUrl())
                .build();
        return service.createResume(id, resume);
    }

    @GetMapping("/{id}/resumes")
    public List<Resume> listResumes(@PathVariable Long id) {
        return service.listResumes(id);
    }

    @GetMapping("/{id}/applications")
    public List<JobApplication> listApplications(@PathVariable Long id) {
        return service.listApplicationsByStudent(id);
    }
}
