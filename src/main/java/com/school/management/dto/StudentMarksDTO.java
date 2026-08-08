package com.school.management.dto;

import lombok.Data;

@Data
public class StudentMarksDTO {
    private Long studentId;
    private Double theoryMarks;
    private Double practicalMarks;
    private boolean absent;
    private String remarks;
}
