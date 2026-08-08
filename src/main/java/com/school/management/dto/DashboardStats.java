package com.school.management.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardStats {
    private long totalExams;
    private long upcomingExams;
    private long ongoingExams;
    private long completedExams;
    private long resultsPending;
    private long publishedResults;
    private long studentsEvaluated;
    private List<RecentExamDTO> recentExams;

    @Data
    public static class RecentExamDTO {
        private Long id;
        private String name;
        private String examType;
        private String className;
        private String startDate;
        private String endDate;
        private String status;
    }
}
