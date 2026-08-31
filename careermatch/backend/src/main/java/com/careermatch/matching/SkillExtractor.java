package com.careermatch.matching;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class SkillExtractor {

    public Set<String> extractSkills(String text) {

        if (text == null || text.isBlank()) {
            return Set.of();
        }

        String normalizedText =
                text.toLowerCase(Locale.ROOT);

        Set<String> foundSkills =
                new LinkedHashSet<>();

        for (SkillDefinition definition : SkillTaxonomy.all()) {

            // -----------------------------------------------------
            // Check canonical skill name
            // -----------------------------------------------------

            if (containsSkill(
                    normalizedText,
                    definition.canonical()
            )) {

                foundSkills.add(
                        definition.canonical()
                );

                continue;
            }

            // -----------------------------------------------------
            // Check aliases
            //
            // Every alias is converted back to the canonical name.
            //
            // Example:
            // "postgres" -> "postgresql"
            // "k8s"      -> "kubernetes"
            // "springboot" -> "spring boot"
            // -----------------------------------------------------

            for (String alias : definition.aliases()) {

                if (containsSkill(
                        normalizedText,
                        alias
                )) {

                    foundSkills.add(
                            definition.canonical()
                    );

                    break;
                }
            }
        }

        return foundSkills;
    }

    /**
     * Checks whether a skill occurs as a meaningful token/phrase
     * instead of as a substring of another word.
     *
     * Examples:
     *
     * "Java developer"       -> true
     * "JavaScript developer"  -> Java is NOT matched
     * "Spring Boot developer" -> true
     * "SpringBoot developer"  -> true
     * "Postgres database"     -> true
     * "K8s deployment"        -> true
     */
    private boolean containsSkill(
            String text,
            String skill
    ) {

        if (text == null
                || text.isBlank()
                || skill == null
                || skill.isBlank()) {

            return false;
        }

        String normalizedSkill =
                skill.toLowerCase(Locale.ROOT)
                        .trim();

        /*
         * Special handling for very short skills.
         *
         * Single-character skills such as "C" and "R"
         * should not be detected inside ordinary text.
         */
        if (normalizedSkill.length() == 1) {

            return containsStandaloneShortSkill(
                    text,
                    normalizedSkill
            );
        }

        /*
         * For normal skills, use non-word boundaries.
         *
         * Java      -> matches
         * JavaScript -> does not match Java
         * PostgreSQL -> matches
         * postgres   -> matches
         */
        String regex =
                "(?<![a-zA-Z0-9])"
                        + Pattern.quote(normalizedSkill)
                        + "(?![a-zA-Z0-9])";

        return Pattern.compile(regex)
                .matcher(text)
                .find();
    }

    /**
     * Prevents false positives for one-character skills.
     *
     * C and R should normally appear as standalone tokens.
     *
     * Examples:
     *
     * "C developer"       -> true
     * "C programming"     -> true
     * "R programming"     -> true
     * "career"            -> false
     * "react"             -> false for C/R
     */
    private boolean containsStandaloneShortSkill(
            String text,
            String skill
    ) {

        String regex =
                "(?<![a-zA-Z0-9])"
                        + Pattern.quote(skill)
                        + "(?![a-zA-Z0-9])";

        return Pattern.compile(regex)
                .matcher(text)
                .find();
    }
}