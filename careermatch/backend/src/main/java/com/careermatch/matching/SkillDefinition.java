package com.careermatch.matching;

import java.util.List;

public record SkillDefinition(
        String canonical,
        String category,
        List<String> aliases
) {
}