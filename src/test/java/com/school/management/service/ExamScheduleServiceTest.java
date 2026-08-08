package com.school.management.service;

import com.school.management.dto.ExamScheduleDTO;
import com.school.management.exception.ValidationException;
import com.school.management.repository.ExamRepository;
import com.school.management.repository.ExamScheduleRepository;
import com.school.management.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ExamScheduleServiceTest {

    @Mock
    private ExamScheduleRepository scheduleRepository;
    @Mock
    private ExamRepository examRepository;
    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private ExamScheduleService scheduleService;

    private ExamScheduleDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ExamScheduleDTO();
        dto.setExamId(1L);
        dto.setSubjectId(1L);
        dto.setMaxMarks(100);
        dto.setPassingMarks(35);
        dto.setTheoryMaxMarks(70);
        dto.setPracticalMaxMarks(30);
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(12, 0));
    }

    @Test
    void testPassingMarksGreaterThanMaxMarks() {
        dto.setPassingMarks(105);
        assertThrows(ValidationException.class, () -> scheduleService.createSchedule(dto));
    }

    @Test
    void testTheoryAndPracticalSumMismatch() {
        dto.setTheoryMaxMarks(50);
        dto.setPracticalMaxMarks(40); // Sum 90 != 100 maxMarks
        assertThrows(ValidationException.class, () -> scheduleService.createSchedule(dto));
    }

    @Test
    void testEndTimeBeforeStartTime() {
        dto.setStartTime(LocalTime.of(12, 0));
        dto.setEndTime(LocalTime.of(9, 0));
        assertThrows(ValidationException.class, () -> scheduleService.createSchedule(dto));
    }
}
