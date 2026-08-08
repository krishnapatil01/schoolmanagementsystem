package com.school.management.repository;

import com.school.management.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
    List<ExamSchedule> findByExamId(Long examId);
    Optional<ExamSchedule> findByExamIdAndSubjectId(Long examId, Long subjectId);
}
