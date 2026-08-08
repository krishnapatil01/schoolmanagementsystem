package com.school.management.dto;

import lombok.Data;
import java.util.List;

@Data
public class MarksEntryResponse {
    private Object examSchedule;
    private List<StudentMarkDetail> students;

    @Data
    public static class StudentMarkDetail {
        private Long studentId;
        private String rollNumber;
        private String name;
        private Double theoryMarks;
        private Double practicalMarks;
        private Double totalMarks;
        private boolean absent;
        private String remarks;
        private Long marksId;
        private String status;
    }
}
