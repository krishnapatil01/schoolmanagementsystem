package com.school.management.dto;

import com.school.management.enums.ExamStatus;
import com.school.management.enums.ExamType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExamDTO {
    private Long id;

    @NotBlank(message = "Exam name is required")
    private String name;

    @NotNull(message = "Exam type is required")
    private ExamType examType;

    @NotNull(message = "Academic year is required")
    private Long academicYearId;

    private String term;

    @NotNull(message = "Class is required")
    private Long classId;

    private Long sectionId;

    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    
    private ExamStatus status;
}
