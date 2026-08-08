package com.school.management.controller;

import com.school.management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/class-wise")
    public ResponseEntity<Object> getClassWiseReport(@RequestParam Long examId, @RequestParam(required = false) Long classId, @RequestParam(required = false) Long sectionId) {
        return ResponseEntity.ok(reportService.getClassWiseReport(examId, classId, sectionId));
    }

    @GetMapping("/subject-wise")
    public ResponseEntity<Object> getSubjectWiseReport(@RequestParam Long examId, @RequestParam Long subjectId) {
        return ResponseEntity.ok(reportService.getSubjectWiseReport(examId, subjectId));
    }

    @GetMapping("/pass-fail")
    public ResponseEntity<Object> getPassFailReport(@RequestParam Long examId, @RequestParam(required = false) Long classId) {
        return ResponseEntity.ok(reportService.getPassFailReport(examId, classId));
    }

    @GetMapping("/rank-list")
    public ResponseEntity<Object> getRankList(@RequestParam Long examId, @RequestParam(required = false) Long classId, @RequestParam(required = false) Long sectionId, @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(reportService.getRankList(examId, classId, sectionId, limit));
    }

    @GetMapping("/topper-list")
    public ResponseEntity<Object> getTopperList(@RequestParam Long examId, @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(reportService.getTopperList(examId, limit));
    }

    @GetMapping("/absent")
    public ResponseEntity<Object> getAbsentReport(@RequestParam Long examId) {
        return ResponseEntity.ok(reportService.getAbsentReport(examId));
    }

    @GetMapping("/summary")
    public ResponseEntity<Object> getSummaryReport(@RequestParam Long examId) {
        return ResponseEntity.ok(reportService.getSummaryReport(examId));
    }
}
