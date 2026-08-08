package com.school.management.service;

import com.school.management.dto.MarksEntryDTO;
import com.school.management.dto.MarksEntryResponse;
import com.school.management.dto.StudentMarksDTO;
import com.school.management.entity.ExamMarks;
import com.school.management.entity.ExamSchedule;
import com.school.management.entity.Student;
import com.school.management.enums.MarksStatus;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.repository.ExamMarksRepository;
import com.school.management.repository.ExamScheduleRepository;
import com.school.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarksService {
    private final ExamMarksRepository marksRepository;
    private final ExamScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;

    public MarksEntryResponse getMarksEntry(Long examScheduleId) {
        ExamSchedule schedule = scheduleRepository.findById(examScheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        List<Student> students;
        if (schedule.getExam().getSection() != null) {
            students = studentRepository.findBySchoolClassIdAndSectionId(
                    schedule.getExam().getSchoolClass().getId(), schedule.getExam().getSection().getId());
        } else {
            students = studentRepository.findBySchoolClassId(schedule.getExam().getSchoolClass().getId());
        }
        if (students == null || students.isEmpty()) {
            students = studentRepository.findAll();
        }

        List<ExamMarks> existingMarks = marksRepository.findByExamScheduleId(examScheduleId);
        Map<Long, ExamMarks> marksMap = existingMarks.stream()
                .collect(Collectors.toMap(m -> m.getStudent().getId(), Function.identity()));

        List<MarksEntryResponse.StudentMarkDetail> details = new ArrayList<>();
        for (Student student : students) {
            if (!student.isActive()) continue;
            
            MarksEntryResponse.StudentMarkDetail detail = new MarksEntryResponse.StudentMarkDetail();
            detail.setStudentId(student.getId());
            detail.setRollNumber(student.getRollNumber());
            detail.setName(student.getFirstName() + " " + student.getLastName());
            
            ExamMarks mark = marksMap.get(student.getId());
            if (mark != null) {
                detail.setMarksId(mark.getId());
                detail.setTheoryMarks(mark.getTheoryMarks());
                detail.setPracticalMarks(mark.getPracticalMarks());
                detail.setTotalMarks(mark.getTotalMarks());
                detail.setAbsent(mark.isAbsent());
                detail.setRemarks(mark.getRemarks());
                detail.setStatus(mark.getStatus().name());
            } else {
                detail.setTheoryMarks(null);
                detail.setPracticalMarks(null);
                detail.setTotalMarks(0.0);
                detail.setAbsent(false);
                detail.setStatus(MarksStatus.DRAFT.name());
            }
            details.add(detail);
        }

        MarksEntryResponse response = new MarksEntryResponse();
        response.setExamSchedule(schedule);
        response.setStudents(details);
        return response;
    }

    @Transactional
    public void saveMarks(MarksEntryDTO dto) {
        ExamSchedule schedule = scheduleRepository.findById(dto.getExamScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
                
        for (StudentMarksDTO m : dto.getMarks()) {
            Student student = studentRepository.findById(m.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

            ExamMarks mark = marksRepository.findByExamScheduleIdAndStudentId(schedule.getId(), student.getId())
                    .orElse(new ExamMarks());

            if (mark.getStatus() == MarksStatus.LOCKED) {
                throw new ValidationException("Marks for student " + student.getRollNumber() + " are locked and cannot be modified.");
            }

            mark.setExamSchedule(schedule);
            mark.setStudent(student);
            mark.setAbsent(m.isAbsent());
            mark.setRemarks(m.getRemarks());
            
            if (m.isAbsent()) {
                mark.setTheoryMarks(0.0);
                mark.setPracticalMarks(0.0);
                mark.setTotalMarks(0.0);
            } else {
                double tm = m.getTheoryMarks() != null ? m.getTheoryMarks() : 0.0;
                double pm = m.getPracticalMarks() != null ? m.getPracticalMarks() : 0.0;
                if (tm < 0 || pm < 0) {
                    throw new ValidationException("Marks cannot be negative for student " + student.getRollNumber());
                }
                if (schedule.getTheoryMaxMarks() > 0 && tm > schedule.getTheoryMaxMarks()) {
                    throw new ValidationException("Theory marks (" + tm + ") exceed max limit (" + schedule.getTheoryMaxMarks() + ") for student " + student.getRollNumber());
                }
                if (schedule.getPracticalMaxMarks() > 0 && pm > schedule.getPracticalMaxMarks()) {
                    throw new ValidationException("Practical marks (" + pm + ") exceed max limit (" + schedule.getPracticalMaxMarks() + ") for student " + student.getRollNumber());
                }
                if ((tm + pm) > schedule.getMaxMarks()) {
                    throw new ValidationException("Total marks (" + (tm + pm) + ") exceed max limit (" + schedule.getMaxMarks() + ") for student " + student.getRollNumber());
                }
                mark.setTheoryMarks(tm);
                mark.setPracticalMarks(pm);
                mark.setTotalMarks(tm + pm);
            }
            mark.setStatus(MarksStatus.SUBMITTED);
            marksRepository.save(mark);
        }
    }

    @Transactional
    public void submitMarks(Long examScheduleId) {
        List<ExamMarks> marks = marksRepository.findByExamScheduleId(examScheduleId);
        for (ExamMarks mark : marks) {
            mark.setStatus(MarksStatus.LOCKED);
        }
        marksRepository.saveAll(marks);
    }
}
