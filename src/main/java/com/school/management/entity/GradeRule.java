package com.school.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "grade_rules")
public class GradeRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String gradeName;

    private double minPercentage;
    private double maxPercentage;
    private double gradePoint;

    private String description;
    private int sortOrder;
}
