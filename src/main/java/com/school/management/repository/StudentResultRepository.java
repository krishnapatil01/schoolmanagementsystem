package com.school.management.repository;

import com.school.management.entity.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentResultRepository extends JpaRepository<StudentResult, Long> {
    List<StudentResult> findByExamId(Long examId);
    List<StudentResult> findByExamIdOrderByPercentageDesc(Long examId);
    Optional<StudentResult> findByExamIdAndStudentId(Long examId, Long studentId);
}
