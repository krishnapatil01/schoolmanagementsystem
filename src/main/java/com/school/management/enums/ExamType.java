package com.school.management.enums;

public enum ExamType {
    UNIT_TEST("Unit Test"),
    MID_TERM("Mid-Term Exam"),
    SEMESTER("Semester/Term Exam"),
    FINAL("Final Exam"),
    PRACTICAL("Practical Exam"),
    CUSTOM("Custom Exam");

    private final String displayName;

    ExamType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
