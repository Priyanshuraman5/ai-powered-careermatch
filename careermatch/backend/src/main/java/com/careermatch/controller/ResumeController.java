package com.careermatch.controller;

import com.careermatch.model.dto.ResumeDtos.ResumeResponse;
import com.careermatch.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ResumeResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(resumeService.uploadAndProcess(CurrentUser.id(), file));
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> history() {
        return ResponseEntity.ok(resumeService.getHistory(CurrentUser.id()));
    }
}
