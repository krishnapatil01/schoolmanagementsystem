package com.school.management.service;

import com.school.management.dto.ExamDTO;
import com.school.management.dto.PageResponse;
import com.school.management.entity.AcademicYear;
import com.school.management.entity.Exam;
import com.school.management.entity.SchoolClass;
import com.school.management.entity.Section;
import com.school.management.enums.ExamStatus;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.repository.AcademicYearRepository;
import com.school.management.repository.ExamRepository;
import com.school.management.repository.SchoolClassRepository;
import com.school.management.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SchoolClassRepository classRepository;
    private final SectionRepository sectionRepository;

    public PageResponse<Exam> getExams(String search, ExamStatus status, String examType, Long classId, Long academicYearId, int page, int size) {
        Specification<Exam> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deleted")));
            if (search != null && !search.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + search.trim().toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (examType != null && !examType.trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("examType").as(String.class)), examType.trim().toUpperCase()));
            }
            if (classId != null) {
                predicates.add(cb.equal(root.get("schoolClass").get("id"), classId));
            }
            if (academicYearId != null) {
                predicates.add(cb.equal(root.get("academicYear").get("id"), academicYearId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Exam> examPage = examRepository.findAll(spec, PageRequest.of(page, size));
        return new PageResponse<>(examPage.getContent(), examPage.getTotalElements(), examPage.getTotalPages(), examPage.getNumber(), examPage.getSize());
    }

    public Exam getExam(Long id) {
        return examRepository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
    }

    public List<Exam> getAllExams() {
        return examRepository.findByDeletedFalse();
    }

    @Transactional
    public Exam createExam(ExamDTO dto) {
        validateExamDto(dto);
        Exam exam = new Exam();
        mapDtoToEntity(dto, exam);
        return examRepository.save(exam);
    }

    @Transactional
    public Exam updateExam(Long id, ExamDTO dto) {
        validateExamDto(dto);
        Exam exam = getExam(id);
        mapDtoToEntity(dto, exam);
        return examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(Long id) {
        Exam exam = getExam(id);
        exam.setDeleted(true);
        examRepository.save(exam);
    }

    @Transactional
    public Exam updateStatus(Long id, ExamStatus status) {
        Exam exam = getExam(id);
        exam.setStatus(status);
        return examRepository.save(exam);
    }

    private void validateExamDto(ExamDTO dto) {
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                throw new ValidationException("End date must be after or equal to start date");
            }
        }
    }

    private void mapDtoToEntity(ExamDTO dto, Exam exam) {
        exam.setName(dto.getName());
        exam.setExamType(dto.getExamType());
        exam.setTerm(dto.getTerm());
        exam.setStartDate(dto.getStartDate());
        exam.setEndDate(dto.getEndDate());
        exam.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            exam.setStatus(dto.getStatus());
        }
        
        AcademicYear ay = academicYearRepository.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic Year not found"));
        exam.setAcademicYear(ay);
        
        SchoolClass sc = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        exam.setSchoolClass(sc);
        
        if (dto.getSectionId() != null) {
            Section section = sectionRepository.findById(dto.getSectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
            exam.setSection(section);
        } else {
            exam.setSection(null);
        }
    }
}
