package com.school.management.service;

import com.school.management.entity.GradeRule;
import com.school.management.repository.GradeRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRuleRepository gradeRuleRepository;
    private List<GradeRule> rules;

    @PostConstruct
    public void init() {
        refreshRules();
    }

    public void refreshRules() {
        this.rules = gradeRuleRepository.findAllByOrderBySortOrderAsc();
    }

    public GradeRule getGradeForPercentage(double percentage) {
        if (rules == null || rules.isEmpty()) {
            refreshRules();
        }
        for (GradeRule rule : rules) {
            if (percentage >= rule.getMinPercentage() && percentage <= rule.getMaxPercentage()) {
                return rule;
            }
        }
        return null;
    }
}
