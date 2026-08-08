package com.school.management.controller;

import com.school.management.entity.StudentResult;
import com.school.management.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;

    @PostMapping("/calculate")
    public ResponseEntity<List<StudentResult>> calculateResults(@RequestParam Long examId) {
        return ResponseEntity.ok(resultService.calculateResults(examId));
    }

    @GetMapping
    public ResponseEntity<List<StudentResult>> getResults(@RequestParam Long examId) {
        return ResponseEntity.ok(resultService.getResults(examId));
    }

    @GetMapping("/student")
    public ResponseEntity<StudentResult> getStudentResult(@RequestParam Long examId, @RequestParam Long studentId) {
        return ResponseEntity.ok(resultService.getStudentResult(examId, studentId));
    }

    @PostMapping("/publish")
    public ResponseEntity<Map<String, String>> publishResults(@RequestParam Long examId) {
        resultService.publishResults(examId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Results published successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unpublish")
    public ResponseEntity<Map<String, String>> unpublishResults(@RequestParam Long examId) {
        resultService.unpublishResults(examId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Results unpublished successfully");
        return ResponseEntity.ok(response);
    }
}
