package com.school.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ExamScheduleDTO {
    private Long id;

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Exam date is required")
    private LocalDate examDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @NotNull(message = "Max marks is required")
    private Integer maxMarks;

    @NotNull(message = "Passing marks is required")
    private Integer passingMarks;

    private Integer theoryMaxMarks;
    private Integer practicalMaxMarks;
    
    private String room;
    private String instructions;
}
