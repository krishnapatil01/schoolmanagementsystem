package com.school.management.enums;

public enum MarksStatus {
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    LOCKED("Locked");

    private final String displayName;

    MarksStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
