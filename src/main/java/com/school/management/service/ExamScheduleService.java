package com.school.management.service;

import com.school.management.dto.ExamScheduleDTO;
import com.school.management.entity.Exam;
import com.school.management.entity.ExamSchedule;
import com.school.management.entity.Subject;
import com.school.management.exception.DuplicateResourceException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.repository.ExamRepository;
import com.school.management.repository.ExamScheduleRepository;
import com.school.management.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamScheduleService {
    private final ExamScheduleRepository scheduleRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    public List<ExamSchedule> getSchedulesByExam(Long examId) {
        return scheduleRepository.findByExamId(examId);
    }

    public ExamSchedule getSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
    }

    @Transactional
    public ExamSchedule createSchedule(ExamScheduleDTO dto) {
        validateScheduleDto(dto, null);
        ExamSchedule schedule = new ExamSchedule();
        mapDtoToEntity(dto, schedule);
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public ExamSchedule updateSchedule(Long id, ExamScheduleDTO dto) {
        validateScheduleDto(dto, id);
        ExamSchedule schedule = getSchedule(id);
        mapDtoToEntity(dto, schedule);
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    private void validateScheduleDto(ExamScheduleDTO dto, Long currentId) {
        if (dto.getMaxMarks() <= 0) {
            throw new ValidationException("Maximum marks must be greater than 0");
        }
        if (dto.getPassingMarks() < 0) {
            throw new ValidationException("Passing marks cannot be negative");
        }
        if (dto.getPassingMarks() > dto.getMaxMarks()) {
            throw new ValidationException("Passing marks cannot be greater than maximum marks");
        }
        
        int theory = dto.getTheoryMaxMarks() != null ? dto.getTheoryMaxMarks() : 0;
        int practical = dto.getPracticalMaxMarks() != null ? dto.getPracticalMaxMarks() : 0;
        if (theory < 0 || practical < 0) {
            throw new ValidationException("Theory and practical marks cannot be negative");
        }
        if (theory > 0 || practical > 0) {
            if ((theory + practical) != dto.getMaxMarks()) {
                throw new ValidationException("Theory max marks + Practical max marks must equal Total Max Marks");
            }
        }
        
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            if (!dto.getEndTime().isAfter(dto.getStartTime())) {
                throw new ValidationException("End time must be after start time");
            }
        }
        
        if (dto.getExamId() != null) {
            Exam exam = examRepository.findById(dto.getExamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
            if (dto.getExamDate() != null && exam.getStartDate() != null && exam.getEndDate() != null) {
                if (dto.getExamDate().isBefore(exam.getStartDate()) || dto.getExamDate().isAfter(exam.getEndDate())) {
                    throw new ValidationException("Exam date must be between exam start date (" + exam.getStartDate() + ") and end date (" + exam.getEndDate() + ")");
                }
            }
        }

        scheduleRepository.findByExamIdAndSubjectId(dto.getExamId(), dto.getSubjectId())
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getId().equals(currentId)) {
                        throw new DuplicateResourceException("Schedule for this subject already exists in the exam");
                    }
                });
    }

    private void mapDtoToEntity(ExamScheduleDTO dto, ExamSchedule schedule) {
        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        schedule.setExam(exam);
        schedule.setSubject(subject);
        schedule.setExamDate(dto.getExamDate());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setMaxMarks(dto.getMaxMarks());
        schedule.setPassingMarks(dto.getPassingMarks());
        schedule.setTheoryMaxMarks(dto.getTheoryMaxMarks() != null ? dto.getTheoryMaxMarks() : 0);
        schedule.setPracticalMaxMarks(dto.getPracticalMaxMarks() != null ? dto.getPracticalMaxMarks() : 0);
        schedule.setRoom(dto.getRoom());
        schedule.setInstructions(dto.getInstructions());
    }
}
