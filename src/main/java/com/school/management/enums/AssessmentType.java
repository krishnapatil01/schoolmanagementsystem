package com.school.management.enums;

public enum AssessmentType {
    THEORY_ONLY("Theory Only"),
    PRACTICAL_ONLY("Practical Only"),
    THEORY_AND_PRACTICAL("Theory & Practical");

    private final String displayName;

    AssessmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
