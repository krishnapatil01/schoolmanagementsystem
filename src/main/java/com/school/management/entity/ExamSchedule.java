package com.school.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "exam_schedules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_id", "subject_id"})
})
public class ExamSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private LocalDate examDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @Column(nullable = false)
    private int maxMarks;

    @Column(nullable = false)
    private int passingMarks;

    @Column(nullable = false)
    private int theoryMaxMarks = 0;

    @Column(nullable = false)
    private int practicalMaxMarks = 0;

    private String room;
    private String instructions;
}
