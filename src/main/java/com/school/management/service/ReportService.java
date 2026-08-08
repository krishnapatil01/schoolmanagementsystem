package com.school.management.service;

import com.school.management.entity.*;
import com.school.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final StudentResultRepository resultRepository;
    private final ExamRepository examRepository;
    private final ExamScheduleRepository scheduleRepository;
    private final ExamMarksRepository marksRepository;

    public List<Map<String, Object>> getClassWiseReport(Long examId, Long classId, Long sectionId) {
        List<StudentResult> results = resultRepository.findByExamIdOrderByPercentageDesc(examId);
        return results.stream()
                .filter(r -> classId == null || (r.getStudent().getSchoolClass() != null && r.getStudent().getSchoolClass().getId().equals(classId)))
                .filter(r -> sectionId == null || (r.getStudent().getSection() != null && r.getStudent().getSection().getId().equals(sectionId)))
                .map(this::mapResultToMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSubjectWiseReport(Long examId, Long subjectId) {
        Optional<ExamSchedule> schedOpt = scheduleRepository.findByExamIdAndSubjectId(examId, subjectId);
        if (schedOpt.isEmpty()) {
            return Collections.emptyList();
        }
        ExamSchedule schedule = schedOpt.get();
        List<ExamMarks> marksList = marksRepository.findByExamScheduleId(schedule.getId());
        
        List<Map<String, Object>> res = new ArrayList<>();
        for (ExamMarks m : marksList) {
            Map<String, Object> map = new HashMap<>();
            map.put("studentRollNumber", m.getStudent().getRollNumber());
            map.put("studentName", m.getStudent().getFirstName() + " " + m.getStudent().getLastName());
            map.put("theoryMarks", m.getTheoryMarks());
            map.put("practicalMarks", m.getPracticalMarks());
            map.put("totalMarks", m.getTotalMarks());
            map.put("absent", m.isAbsent());
            map.put("pass", !m.isAbsent() && m.getTotalMarks() >= schedule.getPassingMarks());
            res.add(map);
        }
        return res;
    }

    public Map<String, Object> getPassFailReport(Long examId, Long classId) {
        List<StudentResult> results = resultRepository.findByExamId(examId).stream()
                .filter(r -> classId == null || (r.getStudent().getSchoolClass() != null && r.getStudent().getSchoolClass().getId().equals(classId)))
                .collect(Collectors.toList());

        long total = results.size();
        long passed = results.stream().filter(StudentResult::isPassed).count();
        long failed = total - passed;
        double percentage = total > 0 ? ((double) passed / total) * 100.0 : 0.0;

        Map<String, Object> res = new HashMap<>();
        res.put("totalStudents", total);
        res.put("passedStudents", passed);
        res.put("failedStudents", failed);
        res.put("passPercentage", percentage);
        return res;
    }

    public List<Map<String, Object>> getRankList(Long examId, Long classId, Long sectionId, Integer limit) {
        List<StudentResult> results = resultRepository.findByExamIdOrderByPercentageDesc(examId).stream()
                .filter(StudentResult::isPassed)
                .filter(r -> classId == null || (r.getStudent().getSchoolClass() != null && r.getStudent().getSchoolClass().getId().equals(classId)))
                .filter(r -> sectionId == null || (r.getStudent().getSection() != null && r.getStudent().getSection().getId().equals(sectionId)))
                .collect(Collectors.toList());

        if (limit != null && limit > 0) {
            results = results.stream().limit(limit).collect(Collectors.toList());
        }

        return results.stream().map(this::mapResultToMap).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopperList(Long examId, Integer limit) {
        int max = (limit != null && limit > 0) ? limit : 5;
        return getRankList(examId, null, null, max);
    }

    public List<Map<String, Object>> getAbsentReport(Long examId) {
        List<ExamSchedule> schedules = scheduleRepository.findByExamId(examId);
        List<Map<String, Object>> absentees = new ArrayList<>();

        for (ExamSchedule sched : schedules) {
            List<ExamMarks> marks = marksRepository.findByExamScheduleId(sched.getId());
            for (ExamMarks m : marks) {
                if (m.isAbsent()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("rollNumber", m.getStudent().getRollNumber());
                    map.put("studentName", m.getStudent().getFirstName() + " " + m.getStudent().getLastName());
                    map.put("subjectName", sched.getSubject() != null ? sched.getSubject().getName() : "");
                    map.put("examDate", sched.getExamDate() != null ? sched.getExamDate().toString() : "");
                    absentees.add(map);
                }
            }
        }
        return absentees;
    }

    public Map<String, Object> getSummaryReport(Long examId) {
        return getPassFailReport(examId, null);
    }

    private Map<String, Object> mapResultToMap(StudentResult r) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", r.getId());
        map.put("examId", r.getExam().getId());
        map.put("studentId", r.getStudent().getId());
        map.put("studentRollNumber", r.getStudent().getRollNumber());
        map.put("studentName", r.getStudent().getFirstName() + " " + r.getStudent().getLastName());
        map.put("totalMarksObtained", r.getGrandTotal());
        map.put("totalMaxMarks", r.getMaxTotal());
        map.put("percentage", r.getPercentage());
        map.put("grade", r.getGrade());
        map.put("rank", r.getRank());
        map.put("passed", r.isPassed());
        map.put("resultStatus", r.isPassed() ? "PASS" : "FAIL");
        map.put("status", r.getPublishStatus() != null ? r.getPublishStatus().name() : "DRAFT");
        return map;
    }
}
