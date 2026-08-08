package com.school.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "classes")
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "schoolClass")
    private List<Section> sections;

    @JsonIgnore
    @OneToMany(mappedBy = "schoolClass")
    private List<Subject> subjects;
}
