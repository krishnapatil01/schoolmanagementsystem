package com.school.management.controller;

import com.school.management.dto.MarksEntryDTO;
import com.school.management.dto.MarksEntryResponse;
import com.school.management.service.MarksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/marks")
@RequiredArgsConstructor
public class MarksController {
    private final MarksService marksService;

    @GetMapping("/entry")
    public ResponseEntity<MarksEntryResponse> getMarksEntry(@RequestParam Long examScheduleId) {
        return ResponseEntity.ok(marksService.getMarksEntry(examScheduleId));
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveMarks(@RequestBody MarksEntryDTO dto) {
        marksService.saveMarks(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Marks saved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitMarks(@RequestParam Long examScheduleId) {
        marksService.submitMarks(examScheduleId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Marks submitted successfully");
        return ResponseEntity.ok(response);
    }
}
