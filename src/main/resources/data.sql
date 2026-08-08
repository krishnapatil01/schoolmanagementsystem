-- Admin User (password is 'admin123' BCrypt hash)
MERGE INTO users (id, email, password, full_name, role, active, created_at) KEY(id) VALUES 
(1, 'admin@school.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin User', 'ADMIN', true, CURRENT_TIMESTAMP);

-- Academic Year
MERGE INTO academic_years (id, name, start_date, end_date, active) KEY(id) VALUES 
(1, '2025-2026', '2025-04-01', '2026-03-31', true);

-- Classes
MERGE INTO classes (id, name, description) KEY(id) VALUES (1, 'Class 8', 'Grade 8 Middle School');
MERGE INTO classes (id, name, description) KEY(id) VALUES (2, 'Class 9', 'Grade 9 High School');
MERGE INTO classes (id, name, description) KEY(id) VALUES (3, 'Class 10', 'Grade 10 High School');

-- Sections
MERGE INTO sections (id, class_id, name) KEY(id) VALUES (1, 1, 'A');
MERGE INTO sections (id, class_id, name) KEY(id) VALUES (2, 1, 'B');
MERGE INTO sections (id, class_id, name) KEY(id) VALUES (3, 2, 'A');
MERGE INTO sections (id, class_id, name) KEY(id) VALUES (4, 2, 'B');
MERGE INTO sections (id, class_id, name) KEY(id) VALUES (5, 3, 'A');
MERGE INTO sections (id, class_id, name) KEY(id) VALUES (6, 3, 'B');

-- Subjects for Class 10 (id=3)
MERGE INTO subjects (id, class_id, name, code, assessment_type) KEY(id) VALUES (1, 3, 'Mathematics', 'MATH', 'THEORY_AND_PRACTICAL');
MERGE INTO subjects (id, class_id, name, code, assessment_type) KEY(id) VALUES (2, 3, 'Science', 'SCI', 'THEORY_AND_PRACTICAL');
MERGE INTO subjects (id, class_id, name, code, assessment_type) KEY(id) VALUES (3, 3, 'English', 'ENG', 'THEORY_ONLY');
MERGE INTO subjects (id, class_id, name, code, assessment_type) KEY(id) VALUES (4, 3, 'Hindi', 'HIN', 'THEORY_ONLY');
MERGE INTO subjects (id, class_id, name, code, assessment_type) KEY(id) VALUES (5, 3, 'Social Studies', 'SST', 'THEORY_ONLY');

-- Students for Class 10, Section A (class_id=3, section_id=5)
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(1, 'C10A001', 'Aarav', 'Sharma', 'aarav@school.com', '9876543210', 3, 5, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(2, 'C10A002', 'Vivaan', 'Singh', 'vivaan@school.com', '9876543211', 3, 5, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(3, 'C10A003', 'Aditya', 'Verma', 'aditya@school.com', '9876543212', 3, 5, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(4, 'C10A004', 'Diya', 'Patel', 'diya@school.com', '9876543213', 3, 5, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(5, 'C10A005', 'Riya', 'Gupta', 'riya@school.com', '9876543214', 3, 5, true);

-- Students for Class 10, Section B (class_id=3, section_id=6)
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(6, 'C10B001', 'Aryan', 'Kumar', 'aryan@school.com', '9876543215', 3, 6, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(7, 'C10B002', 'Kabir', 'Das', 'kabir@school.com', '9876543216', 3, 6, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(8, 'C10B003', 'Ananya', 'Mishra', 'ananya@school.com', '9876543217', 3, 6, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(9, 'C10B004', 'Ishita', 'Joshi', 'ishita@school.com', '9876543218', 3, 6, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(10, 'C10B005', 'Kavya', 'Nair', 'kavya@school.com', '9876543219', 3, 6, true);

-- Students for Class 8 (class_id=1, section_id=1)
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(11, 'C8A001', 'Rohan', 'Verma', 'rohan@school.com', '9876543220', 1, 1, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(12, 'C8A002', 'Priya', 'Sharma', 'priya@school.com', '9876543221', 1, 1, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(13, 'C8A003', 'Vikram', 'Patel', 'vikram@school.com', '9876543222', 1, 1, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(14, 'C8A004', 'Neha', 'Gupta', 'neha@school.com', '9876543223', 1, 1, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(15, 'C8A005', 'Rahul', 'Singh', 'rahul@school.com', '9876543224', 1, 1, true);

-- Students for Class 9 (class_id=2, section_id=3)
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(16, 'C9A001', 'Amit', 'Shah', 'amit@school.com', '9876543225', 2, 3, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(17, 'C9A002', 'Sneha', 'Reddy', 'sneha@school.com', '9876543226', 2, 3, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(18, 'C9A003', 'Karan', 'Malhotra', 'karan@school.com', '9876543227', 2, 3, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(19, 'C9A004', 'Pooja', 'Joshi', 'pooja@school.com', '9876543228', 2, 3, true);
MERGE INTO students (id, roll_number, first_name, last_name, email, phone, class_id, section_id, active) KEY(id) VALUES 
(20, 'C9A005', 'Dev', 'Kapoor', 'dev@school.com', '9876543229', 2, 3, true);

-- Grade Rules
MERGE INTO grade_rules (id, grade_name, min_percentage, max_percentage, grade_point, description, sort_order) KEY(id) VALUES 
(1, 'A+', 90.0, 100.0, 10.0, 'Outstanding', 1);
MERGE INTO grade_rules (id, grade_name, min_percentage, max_percentage, grade_point, description, sort_order) KEY(id) VALUES 
(2, 'A', 80.0, 89.99, 9.0, 'Excellent', 2);
MERGE INTO grade_rules (id, grade_name, min_percentage, max_percentage, grade_point, description, sort_order) KEY(id) VALUES 
(3, 'B+', 70.0, 79.99, 8.0, 'Very Good', 3);
MERGE INTO grade_rules (id, grade_name, min_percentage, max_percentage, grade_point, description, sort_order) KEY(id) VALUES 
(4, 'B', 60.0, 69.99, 7.0, 'Good', 4);
MERGE INTO grade_rules (id, grade_name, min_percentage, max_percentage, grade_point, description, sort_order) KEY(id) VALUES 
(5, 'C', 50.0, 59.99, 6.0, 'Average', 5);
MERGE INTO grade_rules (id, grade_name, min_percentage, max_percentage, grade_point, description, sort_order) KEY(id) VALUES 
(6, 'D', 35.0, 49.99, 5.0, 'Below Average', 6);
MERGE INTO grade_rules (id, grade_name, min_percentage, max_percentage, grade_point, description, sort_order) KEY(id) VALUES 
(7, 'F', 0.0, 34.99, 0.0, 'Fail', 7);

-- Exam
MERGE INTO exams (id, academic_year_id, class_id, name, exam_type, term, start_date, end_date, status, deleted, created_at) KEY(id) VALUES 
(1, 1, 3, 'Mid-Term Examination 2025', 'MID_TERM', 'Term 1', '2025-09-10', '2025-09-20', 'COMPLETED', false, CURRENT_TIMESTAMP);

-- Exam Schedules
MERGE INTO exam_schedules (id, exam_id, subject_id, exam_date, start_time, end_time, max_marks, passing_marks, theory_max_marks, practical_max_marks, room) KEY(id) VALUES 
(1, 1, 1, '2025-09-10', '09:00:00', '12:00:00', 100, 35, 70, 30, 'Room 101');
MERGE INTO exam_schedules (id, exam_id, subject_id, exam_date, start_time, end_time, max_marks, passing_marks, theory_max_marks, practical_max_marks, room) KEY(id) VALUES 
(2, 1, 2, '2025-09-12', '09:00:00', '12:00:00', 100, 35, 70, 30, 'Room 102');
MERGE INTO exam_schedules (id, exam_id, subject_id, exam_date, start_time, end_time, max_marks, passing_marks, theory_max_marks, practical_max_marks, room) KEY(id) VALUES 
(3, 1, 3, '2025-09-15', '09:00:00', '12:00:00', 100, 35, 100, 0, 'Room 103');
MERGE INTO exam_schedules (id, exam_id, subject_id, exam_date, start_time, end_time, max_marks, passing_marks, theory_max_marks, practical_max_marks, room) KEY(id) VALUES 
(4, 1, 4, '2025-09-17', '09:00:00', '12:00:00', 100, 35, 100, 0, 'Room 104');
MERGE INTO exam_schedules (id, exam_id, subject_id, exam_date, start_time, end_time, max_marks, passing_marks, theory_max_marks, practical_max_marks, room) KEY(id) VALUES 
(5, 1, 5, '2025-09-19', '09:00:00', '12:00:00', 100, 35, 100, 0, 'Room 105');

-- Exam Marks (10 students x 5 subjects)
-- Subject 1 (Math)
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (1, 1, 1, 65.0, 28.0, 93.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (2, 2, 1, 55.0, 25.0, 80.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (3, 3, 1, 45.0, 20.0, 65.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (4, 4, 1, 70.0, 29.0, 99.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (5, 5, 1, 0.0, 0.0, 0.0, true, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (6, 6, 1, 60.0, 26.0, 86.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (7, 7, 1, 50.0, 22.0, 72.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (8, 8, 1, 68.0, 27.0, 95.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (9, 9, 1, 40.0, 18.0, 58.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (10, 10, 1, 35.0, 15.0, 50.0, false, 'LOCKED', CURRENT_TIMESTAMP);

-- Subject 2 (Science)
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (11, 1, 2, 60.0, 25.0, 85.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (12, 2, 2, 58.0, 28.0, 86.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (13, 3, 2, 48.0, 22.0, 70.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (14, 4, 2, 68.0, 28.0, 96.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (15, 5, 2, 42.0, 20.0, 62.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (16, 6, 2, 62.0, 26.0, 88.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (17, 7, 2, 52.0, 24.0, 76.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (18, 8, 2, 65.0, 27.0, 92.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (19, 9, 2, 45.0, 21.0, 66.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (20, 10, 2, 38.0, 18.0, 56.0, false, 'LOCKED', CURRENT_TIMESTAMP);

-- Subject 3 (English)
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (21, 1, 3, 88.0, 0.0, 88.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (22, 2, 3, 78.0, 0.0, 78.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (23, 3, 3, 68.0, 0.0, 68.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (24, 4, 3, 92.0, 0.0, 92.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (25, 5, 3, 60.0, 0.0, 60.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (26, 6, 3, 84.0, 0.0, 84.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (27, 7, 3, 74.0, 0.0, 74.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (28, 8, 3, 90.0, 0.0, 90.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (29, 9, 3, 62.0, 0.0, 62.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (30, 10, 3, 54.0, 0.0, 54.0, false, 'LOCKED', CURRENT_TIMESTAMP);

-- Subject 4 (Hindi)
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (31, 1, 4, 82.0, 0.0, 82.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (32, 2, 4, 76.0, 0.0, 76.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (33, 3, 4, 70.0, 0.0, 70.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (34, 4, 4, 88.0, 0.0, 88.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (35, 5, 4, 65.0, 0.0, 65.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (36, 6, 4, 80.0, 0.0, 80.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (37, 7, 4, 72.0, 0.0, 72.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (38, 8, 4, 86.0, 0.0, 86.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (39, 9, 4, 58.0, 0.0, 58.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (40, 10, 4, 52.0, 0.0, 52.0, false, 'LOCKED', CURRENT_TIMESTAMP);

-- Subject 5 (Social Studies)
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (41, 1, 5, 85.0, 0.0, 85.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (42, 2, 5, 75.0, 0.0, 75.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (43, 3, 5, 65.0, 0.0, 65.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (44, 4, 5, 90.0, 0.0, 90.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (45, 5, 5, 58.0, 0.0, 58.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (46, 6, 5, 82.0, 0.0, 82.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (47, 7, 5, 70.0, 0.0, 70.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (48, 8, 5, 88.0, 0.0, 88.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (49, 9, 5, 60.0, 0.0, 60.0, false, 'LOCKED', CURRENT_TIMESTAMP);
MERGE INTO exam_marks (id, student_id, exam_schedule_id, theory_marks, practical_marks, total_marks, absent, status, created_at) KEY(id) VALUES (50, 10, 5, 50.0, 0.0, 50.0, false, 'LOCKED', CURRENT_TIMESTAMP);

-- Restart sequence counters to avoid primary key collisions with seeded IDs
ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE academic_years ALTER COLUMN id RESTART WITH 100;
ALTER TABLE classes ALTER COLUMN id RESTART WITH 100;
ALTER TABLE sections ALTER COLUMN id RESTART WITH 100;
ALTER TABLE subjects ALTER COLUMN id RESTART WITH 100;
ALTER TABLE students ALTER COLUMN id RESTART WITH 100;
ALTER TABLE grade_rules ALTER COLUMN id RESTART WITH 100;
ALTER TABLE exams ALTER COLUMN id RESTART WITH 100;
ALTER TABLE exam_schedules ALTER COLUMN id RESTART WITH 100;
ALTER TABLE exam_marks ALTER COLUMN id RESTART WITH 100;

