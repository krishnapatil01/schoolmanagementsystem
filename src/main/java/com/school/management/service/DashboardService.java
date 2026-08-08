package com.school.management.service;

import com.school.management.dto.DashboardStats;
import com.school.management.entity.Exam;
import com.school.management.enums.ExamStatus;
import com.school.management.enums.PublishStatus;
import com.school.management.repository.ExamRepository;
import com.school.management.repository.StudentResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ExamRepository examRepository;
    private final StudentResultRepository resultRepository;

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();
        List<Exam> activeExams = examRepository.findByDeletedFalse();

        stats.setTotalExams((long) activeExams.size());
        stats.setUpcomingExams(activeExams.stream().filter(e -> e.getStatus() == ExamStatus.SCHEDULED || e.getStatus() == ExamStatus.DRAFT).count());
        stats.setOngoingExams(activeExams.stream().filter(e -> e.getStatus() == ExamStatus.ONGOING).count());
        stats.setCompletedExams(activeExams.stream().filter(e -> e.getStatus() == ExamStatus.COMPLETED || e.getStatus() == ExamStatus.PUBLISHED).count());
        
        long publishedResultExams = resultRepository.findAll().stream()
                .filter(r -> r.getPublishStatus() == PublishStatus.PUBLISHED)
                .map(r -> r.getExam().getId())
                .distinct()
                .count();

        long evaluatedStudentsCount = resultRepository.count();

        stats.setPublishedResults(publishedResultExams);
        stats.setResultsPending((int) Math.max(0, stats.getCompletedExams() - publishedResultExams));
        stats.setStudentsEvaluated((int) evaluatedStudentsCount);
        
        stats.setRecentExams(activeExams.stream()
                .sorted((e1, e2) -> e2.getId().compareTo(e1.getId()))
                .limit(5)
                .map(e -> {
                    DashboardStats.RecentExamDTO dto = new DashboardStats.RecentExamDTO();
                    dto.setId(e.getId());
                    dto.setName(e.getName());
                    dto.setExamType(e.getExamType() != null ? e.getExamType().name() : "");
                    dto.setClassName(e.getSchoolClass() != null ? e.getSchoolClass().getName() : "");
                    dto.setStartDate(e.getStartDate() != null ? e.getStartDate().toString() : "");
                    dto.setEndDate(e.getEndDate() != null ? e.getEndDate().toString() : "");
                    dto.setStatus(e.getStatus() != null ? e.getStatus().name() : "DRAFT");
                    return dto;
                }).collect(Collectors.toList()));
        
        return stats;
    }
}
