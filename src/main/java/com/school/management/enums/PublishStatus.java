package com.school.management.enums;

public enum PublishStatus {
    DRAFT("Draft"),
    REVIEW("Under Review"),
    PUBLISHED("Published");

    private final String displayName;

    PublishStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
