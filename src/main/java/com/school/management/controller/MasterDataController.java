package com.school.management.controller;

import com.school.management.entity.*;
import com.school.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MasterDataController {
    private final SchoolClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicYearRepository academicYearRepository;
    private final StudentRepository studentRepository;
    private final GradeRuleRepository gradeRuleRepository;

    @GetMapping("/classes")
    public ResponseEntity<List<SchoolClass>> getClasses() {
        return ResponseEntity.ok(classRepository.findAll());
    }

    @GetMapping("/sections")
    public ResponseEntity<List<Section>> getSections(@RequestParam(required = false) Long classId) {
        if (classId != null) {
            return ResponseEntity.ok(sectionRepository.findBySchoolClassId(classId));
        }
        return ResponseEntity.ok(sectionRepository.findAll());
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects(@RequestParam(required = false) Long classId) {
        if (classId != null) {
            List<Subject> subjects = subjectRepository.findBySchoolClassId(classId);
            if (!subjects.isEmpty()) {
                return ResponseEntity.ok(subjects);
            }
        }
        return ResponseEntity.ok(subjectRepository.findAll());
    }

    @GetMapping("/academic-years")
    public ResponseEntity<List<AcademicYear>> getAcademicYears() {
        return ResponseEntity.ok(academicYearRepository.findAll());
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getStudents(@RequestParam(required = false) Long classId, @RequestParam(required = false) Long sectionId) {
        if (classId != null && sectionId != null) {
            return ResponseEntity.ok(studentRepository.findBySchoolClassIdAndSectionId(classId, sectionId));
        } else if (classId != null) {
            return ResponseEntity.ok(studentRepository.findBySchoolClassId(classId));
        }
        return ResponseEntity.ok(studentRepository.findAll());
    }

    @GetMapping("/grade-rules")
    public ResponseEntity<List<GradeRule>> getGradeRules() {
        return ResponseEntity.ok(gradeRuleRepository.findAllByOrderBySortOrderAsc());
    }
}
