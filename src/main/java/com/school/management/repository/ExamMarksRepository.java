package com.school.management.repository;

import com.school.management.entity.ExamMarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamMarksRepository extends JpaRepository<ExamMarks, Long> {
    List<ExamMarks> findByExamScheduleId(Long examScheduleId);
    Optional<ExamMarks> findByExamScheduleIdAndStudentId(Long examScheduleId, Long studentId);
}
