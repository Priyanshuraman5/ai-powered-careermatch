package com.careermatch.controller;

import com.careermatch.model.entity.User;
import com.careermatch.service.SkillGapService;
import com.careermatch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skill-gap")
@RequiredArgsConstructor
public class SkillGapController {

    private final SkillGapService skillGapService;
    private final UserService userService;

    @PostMapping("/analyze")
    public ResponseEntity<SkillGapService.AnalysisResult> analyze(
            @RequestBody AnalyzeRequest request
    ) {

        Long userId = CurrentUser.idOrNull();

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.getById(userId);

        return ResponseEntity.ok(
                skillGapService.analyze(
                        user,
                        request.jobDescription()
                )
        );
    }

    public record AnalyzeRequest(
            String jobDescription
    ) {
    }
}