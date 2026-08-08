package com.school.management.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ReportCardDTO {
    private Map<String, Object> student;
    private Map<String, Object> exam;
    private List<SubjectMark> subjects;
    private double grandTotal;
    private double maxTotal;
    private double percentage;
    private String grade;
    private boolean passed;
    private Integer rank;
    private String remarks;

    @Data
    public static class SubjectMark {
        private String name;
        private int maxMarks;
        private Double theoryMarks;
        private Double practicalMarks;
        private Double totalMarks;
        private String grade;
        private boolean passed;
    }
}
