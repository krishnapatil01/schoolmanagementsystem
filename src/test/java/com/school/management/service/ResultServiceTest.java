package com.school.management.service;

import com.school.management.entity.*;
import com.school.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResultServiceTest {

    @Mock
    private StudentResultRepository resultRepository;
    @Mock
    private ExamRepository examRepository;
    @Mock
    private ExamScheduleRepository scheduleRepository;
    @Mock
    private ExamMarksRepository marksRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private GradeService gradeService;

    @InjectMocks
    private ResultService resultService;

    private Exam exam;
    private SchoolClass schoolClass;
    private Student s1, s2;
    private ExamSchedule sched1;

    @BeforeEach
    void setUp() {
        schoolClass = new SchoolClass();
        schoolClass.setId(1L);

        exam = new Exam();
        exam.setId(1L);
        exam.setSchoolClass(schoolClass);

        s1 = new Student();
        s1.setId(1L);
        s1.setActive(true);

        s2 = new Student();
        s2.setId(2L);
        s2.setActive(true);

        sched1 = new ExamSchedule();
        sched1.setId(1L);
        sched1.setMaxMarks(100);
        sched1.setPassingMarks(35);
    }

    @Test
    void testCalculateResultsPassAndRank() {
        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
        when(scheduleRepository.findByExamId(1L)).thenReturn(Collections.singletonList(sched1));
        when(studentRepository.findBySchoolClassId(1L)).thenReturn(Arrays.asList(s1, s2));

        ExamMarks m1 = new ExamMarks();
        m1.setTotalMarks(85.0);
        m1.setAbsent(false);

        ExamMarks m2 = new ExamMarks();
        m2.setTotalMarks(30.0); // Fail < 35
        m2.setAbsent(false);

        when(marksRepository.findByExamScheduleIdAndStudentId(1L, 1L)).thenReturn(Optional.of(m1));
        when(marksRepository.findByExamScheduleIdAndStudentId(1L, 2L)).thenReturn(Optional.of(m2));
        
        GradeRule gradeA = new GradeRule();
        gradeA.setGradeName("A");
        GradeRule gradeF = new GradeRule();
        gradeF.setGradeName("F");

        when(gradeService.getGradeForPercentage(85.0)).thenReturn(gradeA);
        when(gradeService.getGradeForPercentage(30.0)).thenReturn(gradeF);

        when(resultRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<StudentResult> results = resultService.calculateResults(1L);

        assertEquals(2, results.size());
        
        StudentResult r1 = results.stream().filter(r -> r.getStudent().getId().equals(1L)).findFirst().orElse(null);
        assertNotNull(r1);
        assertTrue(r1.isPassed());
        assertEquals(1, r1.getRank());

        StudentResult r2 = results.stream().filter(r -> r.getStudent().getId().equals(2L)).findFirst().orElse(null);
        assertNotNull(r2);
        assertFalse(r2.isPassed());
        assertNull(r2.getRank()); // Fail gets null rank
    }
}
