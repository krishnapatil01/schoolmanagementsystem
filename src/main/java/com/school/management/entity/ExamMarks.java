package com.school.management.entity;

import com.school.management.enums.MarksStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exam_marks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_schedule_id", "student_id"})
})
public class ExamMarks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_schedule_id", nullable = false)
    private ExamSchedule examSchedule;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private Double theoryMarks;
    private Double practicalMarks;

    @Column(nullable = false)
    private Double totalMarks = 0.0;

    @Column(nullable = false)
    private boolean absent = false;

    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarksStatus status = MarksStatus.DRAFT;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
