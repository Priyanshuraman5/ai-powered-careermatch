package com.careermatch.model.dto;

import java.util.List;

public class UserDtos {

    public record ProfileResponse(
        Long id,
        String email,
        String fullName,
        String headline,
        String location,
        String phone,
        List<String> skills,

        // Coding profiles
        String leetcodeUrl,
        Integer leetcodeProblems,
        Integer leetcodeRating,

        String gfgUrl,
        Integer gfgProblems,

        String codeforcesUrl,
        Integer codeforcesProblems,
        Integer codeforcesRating,

        String codechefUrl,
        Integer codechefProblems,

        String csesUrl,
        Integer csesProblems,

        // Aggregated coding statistics
        Integer questionsSolved,
        Integer activeDays,
        Integer contestRating,
        Integer platforms
    ) {}

    public record ProfileUpdateRequest(
        String fullName,
        String headline,
        String location,
        String phone,
        List<String> skills
    ) {}
}