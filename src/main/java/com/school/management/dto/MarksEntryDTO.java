package com.school.management.dto;

import lombok.Data;
import java.util.List;

@Data
public class MarksEntryDTO {
    private Long examScheduleId;
    private List<StudentMarksDTO> marks;
}
