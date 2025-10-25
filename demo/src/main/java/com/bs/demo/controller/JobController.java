package com.bs.demo.controller;

import com.bs.demo.domain.ApplicationStatus;
import com.bs.demo.domain.Interview;
import com.bs.demo.domain.InterviewStatus;
import com.bs.demo.domain.JobApplication;
import com.bs.demo.domain.JobPosting;
import com.bs.demo.dto.ApplicationStatusUpdateRequest;
import com.bs.demo.dto.InterviewFeedbackRequest;
import com.bs.demo.dto.InterviewScheduleRequest;
import com.bs.demo.dto.JobApplicationRequest;
import com.bs.demo.service.EmploymentManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class JobController {

    private final EmploymentManagementService service;

    public JobController(EmploymentManagementService service) {
        this.service = service;
    }

    @GetMapping("/jobs")
    public List<JobPosting> listJobs(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String location,
                                     @RequestParam(required = false) String workType) {
        return service.listJobs(keyword, location, workType);
    }

    @GetMapping("/jobs/{id}")
    public JobPosting getJob(@PathVariable Long id) {
        return service.getJob(id);
    }

    @GetMapping("/jobs/{id}/applications")
    public List<JobApplication> listApplications(@PathVariable Long id) {
        return service.listApplicationsByJob(id);
    }

    @PostMapping("/jobs/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplication apply(@PathVariable Long id, @RequestBody JobApplicationRequest request) {
        JobApplication application = JobApplication.builder()
                .studentId(request.getStudentId())
                .resumeId(request.getResumeId())
                .coverLetter(request.getCoverLetter())
                .build();
        return service.applyForJob(id, application);
    }

    @PatchMapping("/applications/{id}/status")
    public JobApplication updateApplicationStatus(@PathVariable Long id, @RequestBody ApplicationStatusUpdateRequest request) {
        ApplicationStatus status = ApplicationStatus.valueOf(request.getStatus());
        return service.updateApplicationStatus(id, status);
    }

    @PostMapping("/jobs/{id}/interviews")
    @ResponseStatus(HttpStatus.CREATED)
    public Interview scheduleInterview(@PathVariable Long id, @RequestBody InterviewScheduleRequest request) {
        Interview interview = Interview.builder()
                .applicationId(request.getApplicationId())
                .location(request.getLocation())
                .meetingLink(request.getMeetingLink())
                .feedback(request.getFeedback())
                .build();
        if (request.getScheduledTime() != null) {
            interview.setScheduledTime(LocalDateTime.parse(request.getScheduledTime()));
        }
        if (request.getStatus() != null) {
            interview.setStatus(InterviewStatus.valueOf(request.getStatus()));
        }
        return service.scheduleInterview(id, interview);
    }

    @GetMapping("/jobs/{id}/interviews")
    public List<Interview> listInterviews(@PathVariable Long id) {
        return service.listInterviewsForJob(id);
    }

    @PatchMapping("/interviews/{id}")
    public Interview updateInterview(@PathVariable Long id, @RequestBody InterviewFeedbackRequest request) {
        InterviewStatus status = request.getStatus() != null ? InterviewStatus.valueOf(request.getStatus()) : null;
        return service.updateInterview(id, status, request.getFeedback());
    }
}
