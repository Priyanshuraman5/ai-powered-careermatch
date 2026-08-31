package com.careermatch.controller;

import com.careermatch.model.dto.JobDtos.*;
import com.careermatch.exception.ApiException;
import com.careermatch.service.UserService;
import com.careermatch.service.JobService;
import com.careermatch.service.SerpApiJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final SerpApiJobService serpApiJobService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<JobSummary>> listAll() {
        return ResponseEntity.ok(jobService.listAll(CurrentUser.idOrNull()));
    }

    @PostMapping("/search")
    public ResponseEntity<List<JobSummary>> search(@RequestBody JobSearchRequest request) {
        return ResponseEntity.ok(jobService.search(request, CurrentUser.idOrNull()));
    }
    @GetMapping("/external-search")
    public ResponseEntity<List<SerpApiJobService.ExternalJob>> externalSearch(
            @RequestParam String keyword,
            @RequestParam(required = false) String location
    ) {
        return ResponseEntity.ok(
                serpApiJobService.search(keyword, location)
        );
    }

    @GetMapping("/live-recommendations")
    public ResponseEntity<List<SerpApiJobService.ExternalJob>> liveRecommendations() {
        Long userId = CurrentUser.idOrNull();
        if (userId == null) {
            throw ApiException.unauthorized("Log in to get personalized live recommendations");
        }
        return ResponseEntity.ok(serpApiJobService.recommendFor(userService.getById(userId)));
    }
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<JobDetail> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getDetail(id, CurrentUser.idOrNull()));
    }
}


