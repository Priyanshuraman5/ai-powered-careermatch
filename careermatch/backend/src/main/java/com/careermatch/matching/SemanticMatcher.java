package com.careermatch.matching;

import java.util.Set;

public interface SemanticMatcher {

    MatchResult match(
            String candidateText,
            Set<String> candidateSkills,
            String jobText,
            Set<String> requiredSkills
    );
}