package com.school.management.service;

import com.school.management.dto.MarksEntryDTO;
import com.school.management.dto.StudentMarksDTO;
import com.school.management.entity.ExamMarks;
import com.school.management.entity.ExamSchedule;
import com.school.management.entity.Student;
import com.school.management.enums.MarksStatus;
import com.school.management.exception.ValidationException;
import com.school.management.repository.ExamMarksRepository;
import com.school.management.repository.ExamScheduleRepository;
import com.school.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarksServiceTest {

    @Mock
    private ExamMarksRepository marksRepository;
    @Mock
    private ExamScheduleRepository scheduleRepository;
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private MarksService marksService;

    private ExamSchedule schedule;
    private Student student;

    @BeforeEach
    void setUp() {
        schedule = new ExamSchedule();
        schedule.setId(1L);
        schedule.setMaxMarks(100);
        schedule.setTheoryMaxMarks(70);
        schedule.setPracticalMaxMarks(30);

        student = new Student();
        student.setId(1L);
        student.setRollNumber("R001");
    }

    @Test
    void testSaveMarksExceedingTheoryMax() {
        StudentMarksDTO sm = new StudentMarksDTO();
        sm.setStudentId(1L);
        sm.setTheoryMarks(75.0); // 75 > 70 max
        sm.setPracticalMarks(10.0);

        MarksEntryDTO dto = new MarksEntryDTO();
        dto.setExamScheduleId(1L);
        dto.setMarks(Collections.singletonList(sm));

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThrows(ValidationException.class, () -> marksService.saveMarks(dto));
    }

    @Test
    void testSaveMarksLockedStatus() {
        StudentMarksDTO sm = new StudentMarksDTO();
        sm.setStudentId(1L);
        sm.setTheoryMarks(50.0);

        MarksEntryDTO dto = new MarksEntryDTO();
        dto.setExamScheduleId(1L);
        dto.setMarks(Collections.singletonList(sm));

        ExamMarks lockedMark = new ExamMarks();
        lockedMark.setStatus(MarksStatus.LOCKED);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(marksRepository.findByExamScheduleIdAndStudentId(1L, 1L)).thenReturn(Optional.of(lockedMark));

        assertThrows(ValidationException.class, () -> marksService.saveMarks(dto));
    }
}
