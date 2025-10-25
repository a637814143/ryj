package com.bs.demo.controller;

import com.bs.demo.domain.Employer;
import com.bs.demo.domain.JobPosting;
import com.bs.demo.domain.JobStatus;
import com.bs.demo.dto.EmployerCreateRequest;
import com.bs.demo.dto.JobCreateRequest;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employers")
@CrossOrigin
public class EmployerController {

    private final EmploymentManagementService service;

    public EmployerController(EmploymentManagementService service) {
        this.service = service;
    }

    @GetMapping
    public List<Employer> listEmployers() {
        return service.listEmployers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employer createEmployer(@RequestBody EmployerCreateRequest request) {
        Employer employer = Employer.builder()
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .description(request.getDescription())
                .build();
        return service.createEmployer(employer);
    }

    @GetMapping("/{id}/jobs")
    public List<JobPosting> listJobs(@PathVariable Long id) {
        return service.listJobsByEmployer(id);
    }

    @PostMapping("/{id}/jobs")
    @ResponseStatus(HttpStatus.CREATED)
    public JobPosting createJob(@PathVariable Long id, @RequestBody JobCreateRequest request) {
        JobPosting job = JobPosting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .salaryRange(request.getSalaryRange())
                .location(request.getLocation())
                .workType(request.getWorkType())
                .requirements(request.getRequirements())
                .status(request.getStatus() != null ? JobStatus.valueOf(request.getStatus()) : null)
                .build();
        if (request.getPublishedDate() != null) {
            job.setPublishedDate(LocalDate.parse(request.getPublishedDate()));
        }
        return service.createJob(id, job);
    }
}
