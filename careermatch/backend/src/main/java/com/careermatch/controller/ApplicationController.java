package com.careermatch.controller;

import com.careermatch.model.dto.ApplicationDtos.*;
import com.careermatch.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(@RequestBody ApplyRequest request) {
        return ResponseEntity.ok(applicationService.apply(CurrentUser.id(), request));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> myApplications() {
        return ResponseEntity.ok(applicationService.getForUser(CurrentUser.id()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id,
                                                              @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateStatus(CurrentUser.id(), id, request));
    }
}
