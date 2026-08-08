package com.school.management.controller;

import com.school.management.entity.ExamMarks;
import com.school.management.entity.ExamSchedule;
import com.school.management.entity.StudentResult;
import com.school.management.repository.ExamMarksRepository;
import com.school.management.repository.ExamScheduleRepository;
import com.school.management.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/report-card")
@RequiredArgsConstructor
public class ReportCardController {
    private final ResultService resultService;
    private final ExamScheduleRepository scheduleRepository;
    private final ExamMarksRepository marksRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReportCard(@RequestParam Long studentId, @RequestParam Long examId) {
        StudentResult result = resultService.getStudentResult(examId, studentId);
        List<ExamSchedule> schedules = scheduleRepository.findByExamId(examId);

        List<Map<String, Object>> subjectMarks = new ArrayList<>();
        for (ExamSchedule sched : schedules) {
            Map<String, Object> sm = new HashMap<>();
            sm.put("subjectName", sched.getSubject() != null ? sched.getSubject().getName() : "");
            sm.put("maxMarks", sched.getMaxMarks());
            sm.put("passingMarks", sched.getPassingMarks());

            Optional<ExamMarks> markOpt = marksRepository.findByExamScheduleIdAndStudentId(sched.getId(), studentId);
            if (markOpt.isPresent()) {
                ExamMarks mark = markOpt.get();
                sm.put("absent", mark.isAbsent());
                sm.put("theoryMarksObtained", mark.isAbsent() ? null : mark.getTheoryMarks());
                sm.put("practicalMarksObtained", mark.isAbsent() ? null : mark.getPracticalMarks());
                sm.put("totalMarksObtained", mark.isAbsent() ? 0.0 : mark.getTotalMarks());
                sm.put("pass", !mark.isAbsent() && mark.getTotalMarks() >= sched.getPassingMarks());
            } else {
                sm.put("absent", true);
                sm.put("theoryMarksObtained", null);
                sm.put("practicalMarksObtained", null);
                sm.put("totalMarksObtained", 0.0);
                sm.put("pass", false);
            }
            subjectMarks.add(sm);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("examId", result.getExam().getId());
        response.put("examName", result.getExam().getName());
        response.put("academicYear", result.getExam().getAcademicYear() != null ? result.getExam().getAcademicYear().getName() : "");
        response.put("studentId", result.getStudent().getId());
        response.put("studentName", result.getStudent().getFirstName() + " " + result.getStudent().getLastName());
        response.put("studentRollNumber", result.getStudent().getRollNumber());
        response.put("className", result.getExam().getSchoolClass() != null ? result.getExam().getSchoolClass().getName() : "");
        response.put("sectionName", result.getExam().getSection() != null ? result.getExam().getSection().getName() : "All");
        
        response.put("subjectMarks", subjectMarks);
        response.put("totalMarksObtained", result.getGrandTotal());
        response.put("totalMaxMarks", result.getMaxTotal());
        response.put("percentage", result.getPercentage());
        response.put("grade", result.getGrade());
        response.put("resultStatus", result.isPassed() ? "PASS" : "FAIL");
        response.put("rank", result.getRank());
        response.put("remarks", result.getRemarks());

        return ResponseEntity.ok(response);
    }
}
