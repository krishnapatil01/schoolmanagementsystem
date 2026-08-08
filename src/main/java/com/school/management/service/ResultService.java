package com.school.management.service;

import com.school.management.entity.*;
import com.school.management.enums.PublishStatus;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {
    private final StudentResultRepository resultRepository;
    private final ExamRepository examRepository;
    private final ExamScheduleRepository scheduleRepository;
    private final ExamMarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final GradeService gradeService;

    @Transactional
    public List<StudentResult> calculateResults(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        List<ExamSchedule> schedules = scheduleRepository.findByExamId(examId);
        List<Student> students;
        if (exam.getSection() != null) {
            students = studentRepository.findBySchoolClassIdAndSectionId(exam.getSchoolClass().getId(), exam.getSection().getId());
        } else {
            students = studentRepository.findBySchoolClassId(exam.getSchoolClass().getId());
        }

        List<StudentResult> currentResults = resultRepository.findByExamId(examId);
        Map<Long, StudentResult> resultMap = currentResults.stream().collect(Collectors.toMap(r -> r.getStudent().getId(), r -> r));

        List<StudentResult> processedResults = new ArrayList<>();

        for (Student student : students) {
            if (!student.isActive()) continue;

            double grandTotal = 0;
            double maxTotal = 0;
            boolean passedAll = true;

            for (ExamSchedule schedule : schedules) {
                maxTotal += schedule.getMaxMarks();
                ExamMarks marks = marksRepository.findByExamScheduleIdAndStudentId(schedule.getId(), student.getId()).orElse(null);
                
                if (marks == null || marks.isAbsent()) {
                    passedAll = false;
                } else {
                    grandTotal += marks.getTotalMarks();
                    if (marks.getTotalMarks() < schedule.getPassingMarks()) {
                        passedAll = false;
                    }
                }
            }

            double percentage = maxTotal > 0 ? (grandTotal / maxTotal) * 100 : 0;
            GradeRule gradeRule = gradeService.getGradeForPercentage(percentage);
            String grade = gradeRule != null ? gradeRule.getGradeName() : "";

            StudentResult result = resultMap.getOrDefault(student.getId(), new StudentResult());
            result.setExam(exam);
            result.setStudent(student);
            result.setGrandTotal(grandTotal);
            result.setMaxTotal(maxTotal);
            result.setPercentage(percentage);
            result.setGrade(grade);
            result.setPassed(passedAll);
            
            processedResults.add(result);
        }

        resultRepository.saveAll(processedResults);

        // Ranking Logic
        List<StudentResult> passedStudents = processedResults.stream()
                .filter(StudentResult::isPassed)
                .sorted((r1, r2) -> Double.compare(r2.getPercentage(), r1.getPercentage()))
                .collect(Collectors.toList());

        int rank = 1;
        int count = 0;
        double prevPercentage = -1.0;

        for (StudentResult r : passedStudents) {
            count++;
            if (Math.abs(r.getPercentage() - prevPercentage) > 0.0001) {
                rank = count;
                prevPercentage = r.getPercentage();
            }
            r.setRank(rank);
        }

        for (StudentResult r : processedResults) {
            if (!r.isPassed()) {
                r.setRank(null);
            }
        }
        
        return resultRepository.saveAll(processedResults);
    }

    public List<StudentResult> getResults(Long examId) {
        return resultRepository.findByExamIdOrderByPercentageDesc(examId);
    }

    public StudentResult getStudentResult(Long examId, Long studentId) {
        return resultRepository.findByExamIdAndStudentId(examId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
    }

    @Transactional
    public void publishResults(Long examId) {
        List<StudentResult> results = resultRepository.findByExamId(examId);
        results.forEach(r -> r.setPublishStatus(PublishStatus.PUBLISHED));
        resultRepository.saveAll(results);
    }

    @Transactional
    public void unpublishResults(Long examId) {
        List<StudentResult> results = resultRepository.findByExamId(examId);
        results.forEach(r -> r.setPublishStatus(PublishStatus.DRAFT));
        resultRepository.saveAll(results);
    }
}
