# 🎓 School Management System - Examination Management Module

An end-to-end web application for school examination management, scheduling, marks entry, automated pass/fail & rank evaluation, report card generation, and real-time performance analytics.

---

## 🛠️ Technology Stack & Programming Languages

### **Backend Framework & Logic**
- **Programming Language**: Java (JDK 17)
- **Framework**: Spring Boot 3.2.5
- **Modules Used**:
  - `Spring Web`: RESTful API endpoints & static resource web hosting
  - `Spring Data JPA`: ORM data access layer with Hibernate 6
  - `Spring Security`: Authentication, HTTP session management, and authorization controls
  - `Spring Validation`: Bean validation annotations (`@NotNull`, `@Min`, `@Max`, `@Size`)
  - `Lombok`: Boilerplate reduction for entity DTOs, getters, setters, and constructors

### **Frontend & User Interface**
- **Core Languages**: HTML5, CSS3, JavaScript (ES6+)
- **Styling**: Modern Vanilla CSS with dark/light themes, glassmorphism, responsive grid layouts, and custom print stylesheets (`@media print`)
- **API Integration**: Native JavaScript Fetch API wrapper (`API.get`, `API.post`, `API.put`, `API.delete`)

### **Database & Storage**
- **Database Engine**: H2 Relational Database (File-based persistent storage at `./data/schooldb`)
- **Data Initializer**: SQL Schema & Initial Seed Script (`data.sql`)

### **Build Tool & Dependency Management**
- **Build System**: Apache Maven 3.x

---

##  Key Features & Functionalities

1. ** Interactive Dashboard**:
   - Live metrics: Total Exams, Upcoming/Ongoing/Completed Exams, Results Pending, Published Results, and Evaluated Students count.
   - Quick-action shortcuts and recent exams table.

2. ** Exam Management**:
   - Create, edit, search (live keyword search), filter (by status, type, class), and soft-delete exams.
   - Validation rules enforcing start date <= end date.

3. ** Exam Schedule**:
   - Subject-wise schedule mapping with room allocation, exam date range constraints, and start/end time validation.
   - Automatic calculation & validation for Theory Max Marks, Practical Max Marks, Total Max Marks, and Passing Marks.

4. ** Marks Entry**:
   - Bulk entry table for theory and practical marks with real-time row totals.
   - Absent student toggles (auto-zeroing marks).
   - Strict boundary validations (rejects marks > Max Marks or < 0).
   - Draft saving & Final Submit & Lock workflow.

5. ** Result Processing & Ranking**:
   - Dynamic evaluation of percentage, grade points, letter grades (`A+`, `A`, `B+`, `B`, `C`, `F`), and Pass/Fail status.
   - **Tie-Aware Ranking**: Ranks assigned exclusively to passed students; ties share the same rank and skip subsequent rank positions (e.g. 1, 1, 3).
   - Bulk Publish & Unpublish result controls.

6. ** Student Report Card**:
   - Search student report cards by Student & Exam selection or direct URL parameters (`?studentId=X&examId=Y`).
   - Detailed breakdown table showing Theory & Practical obtained marks, subject pass status, grand total, percentage, final result, rank, and remarks.
   - Native print layout formatted for physical report cards.

7. **📈Performance Reports**:
   - Tabbed analytics: Summary Stats, Class-wise breakdown, Subject-wise averages, Pass/Fail ratios, Toppers List, and Absentees Report.

---

##  Getting Started

### **Prerequisites**
- **Java Development Kit (JDK)**: Version 17 or higher
- **Maven**: 3.8+ (or Maven Wrapper included)

### **Running the Application**

1. **Clone Repository**:
   ```bash
   git clone <your-repository-url>
   cd QIRO_TECH
