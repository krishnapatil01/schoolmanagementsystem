package com.school.management.service;

import com.school.management.dto.ExamDTO;
import com.school.management.entity.AcademicYear;
import com.school.management.entity.Exam;
import com.school.management.entity.SchoolClass;
import com.school.management.enums.ExamType;
import com.school.management.exception.ValidationException;
import com.school.management.repository.AcademicYearRepository;
import com.school.management.repository.ExamRepository;
import com.school.management.repository.SchoolClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;
    @Mock
    private AcademicYearRepository academicYearRepository;
    @Mock
    private SchoolClassRepository classRepository;

    @InjectMocks
    private ExamService examService;

    private ExamDTO validDto;

    @BeforeEach
    void setUp() {
        validDto = new ExamDTO();
        validDto.setName("Mid-Term Test");
        validDto.setExamType(ExamType.MID_TERM);
        validDto.setAcademicYearId(1L);
        validDto.setClassId(1L);
        validDto.setStartDate(LocalDate.of(2025, 9, 10));
        validDto.setEndDate(LocalDate.of(2025, 9, 20));
    }

    @Test
    void testCreateExamSuccess() {
        AcademicYear ay = new AcademicYear();
        ay.setId(1L);
        SchoolClass sc = new SchoolClass();
        sc.setId(1L);

        when(academicYearRepository.findById(1L)).thenReturn(Optional.of(ay));
        when(classRepository.findById(1L)).thenReturn(Optional.of(sc));
        when(examRepository.save(any(Exam.class))).thenAnswer(i -> i.getArgument(0));

        Exam created = examService.createExam(validDto);
        assertNotNull(created);
        assertEquals("Mid-Term Test", created.getName());
    }

    @Test
    void testCreateExamInvalidDateRange() {
        validDto.setStartDate(LocalDate.of(2025, 9, 20));
        validDto.setEndDate(LocalDate.of(2025, 9, 10)); // end before start

        assertThrows(ValidationException.class, () -> examService.createExam(validDto));
    }
}
