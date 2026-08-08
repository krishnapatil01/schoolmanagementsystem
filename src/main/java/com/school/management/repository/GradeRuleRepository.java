package com.school.management.repository;

import com.school.management.entity.GradeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRuleRepository extends JpaRepository<GradeRule, Long> {
    List<GradeRule> findAllByOrderBySortOrderAsc();
}
