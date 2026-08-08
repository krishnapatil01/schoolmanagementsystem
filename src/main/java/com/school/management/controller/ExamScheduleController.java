package com.school.management.controller;

import com.school.management.dto.ExamScheduleDTO;
import com.school.management.entity.ExamSchedule;
import com.school.management.service.ExamScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-schedules")
@RequiredArgsConstructor
public class ExamScheduleController {
    private final ExamScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ExamSchedule>> getSchedules(@RequestParam Long examId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByExam(examId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamSchedule> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }

    @PostMapping
    public ResponseEntity<ExamSchedule> createSchedule(@Valid @RequestBody ExamScheduleDTO dto) {
        return ResponseEntity.ok(scheduleService.createSchedule(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamSchedule> updateSchedule(@PathVariable Long id, @Valid @RequestBody ExamScheduleDTO dto) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
