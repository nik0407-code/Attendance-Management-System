
# 🎓 Attendance Management System (AMS)

A full-stack **Attendance Management System (AMS)** developed using **Java Spring Boot, MySQL, HTML, CSS, and JavaScript**. This application helps educational institutions efficiently manage student attendance with role-based access for **Admin**, **Teacher**, and **Student**.

---

# 📖 Overview

The Attendance Management System is designed to simplify attendance management by allowing teachers to mark attendance, students to monitor their attendance percentage, and administrators to manage users, subjects, and reports.

The system automatically calculates attendance percentages, identifies defaulters, and provides export functionality for attendance reports.

---

# ✨ Features

## 👨‍💼 Admin

- Secure Login
- Add Students
- Add Teachers
- Add Subjects
- View Attendance
- Delete Attendance Records
- Search Attendance
- View Defaulter List
- Subject-wise Defaulter List
- Combined Attendance Report
- Export Attendance Report (CSV)
- Export Attendance Report (PDF)

---

## 👨‍🏫 Teacher

- Secure Login
- Mark Attendance
- Update Attendance
- View Attendance
- Search Attendance
- View Defaulter List
- Subject-wise Attendance
- Export Attendance Report

---

## 👨‍🎓 Student

- Secure Login
- View Personal Attendance
- View Attendance Percentage
- Check Defaulter Status
- Search Attendance Records

---

# 📊 Attendance Status

| Attendance Percentage | Status |
|----------------------|--------|
| Below 75% | 🚨 Defaulter |
| 75% – 80% | ⚠️ At Risk |
| Above 80% | ✅ Safe |

---

# 📚 Modules

### Authentication Module
- Login
- Role-Based Authentication

### Student Management
- Add Student
- View Student
- Search Student

### Teacher Management
- Add Teacher
- View Teacher

### Subject Management
- Add Subject
- View Subject

### Attendance Module
- Mark Attendance
- Update Attendance
- Delete Attendance
- View Attendance
- Search Attendance

### Reports
- Attendance Report
- Subject-wise Report
- Combined Attendance Report
- Defaulter Report

### Export Module
- CSV Export
- PDF Export

---

# 🚀 Technologies Used

## Backend

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Maven

## Frontend

- HTML5
- CSS3
- JavaScript

## Database

- MySQL

## Libraries

- Apache POI
- OpenPDF
- Lombok

---

# 🗂 Project Structure

```
Attendance Management System
│
├── Controller
│
├── Service
│
├── Repository
│
├── DTO
│
├── Models
│
├── Resources
│
│   └── Static
│        ├── login.html
│        ├── signup.html
│        ├── admin-dashboard.html
│        ├── teacher-dashboard.html
│        ├── student-dashboard.html
│        ├── mark-attendance.html
│        ├── view-attendance.html
│        ├── defaulter-list.html
│
├── application.properties
│
└── pom.xml
```

---

# ⚙️ Installation

## Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/Attendance-Management-System.git
```

---

## Open Project

Open the project using **IntelliJ IDEA**.

---

## Create Database

```sql
CREATE DATABASE attendance_db;
```

---

## Configure Database

Edit `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/attendance_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Run the Project

Using IntelliJ:

Run

```
AttendanceManagementApplication.java
```

or using CMD

```bash
mvn spring-boot:run
```

Application runs at

```
http://localhost:8080
```

---

# 📤 Export Features

The system supports exporting attendance reports.

### Available Export Options

- CSV Export
- PDF Export

---

# 📄 Opening CSV Files

Depending on your Windows regional settings, CSV files may open with all values in a single column.

To display the data correctly:

## Microsoft Excel

1. Open the CSV file.
2. Select **Column A**.
3. Go to **Data → Text to Columns**.
4. Select **Delimited**.
5. Click **Next**.
6. Select **Comma (,)** as the delimiter.
7. Click **Finish**.

---

## WPS Office

1. Open the CSV file.
2. Select **Column A**.
3. Go to **Data → Text to Columns**.
4. Choose **Delimited**.
5. Select **Comma (,)** as the delimiter.
6. Click **Finish**.

---

# 🔍 Attendance Search

Attendance records can be filtered using:

- Student Name
- Roll Number
- Subject
- Date
- Time

---

# 📈 Defaulter List

The system automatically calculates attendance percentage for every student.

### Available Filters

- All Subjects
- Subject-wise Attendance
- Combined Attendance
- Defaulters Only
- At Risk Students
- Safe Students

---

# 🔐 User Roles

| Role | Permissions |
|------|-------------|
| Admin | Full Access |
| Teacher | Attendance Management |
| Student | View Attendance Only |

---

# 📸 Screenshots

Add screenshots of the following pages:

- Login Page
- Admin Dashboard
- Teacher Dashboard
- Student Dashboard
- Mark Attendance
- View Attendance
- Defaulter List
- Attendance Report
- CSV Export
- PDF Export

---

# 🌟 Future Enhancements

- QR Code Attendance
- Face Recognition Attendance
- Biometric Attendance
- Email Notifications
- SMS Notifications
- Mobile Application
- Cloud Deployment
- Dashboard Analytics
- AI-Based Attendance Prediction
- OTP Authentication

---

# 👨‍💻 Author

**Nikhil More**

Computer Engineering Student

GitHub:
https://github.com/YOUR_USERNAME

---

# 📄 License

This project is developed for educational purposes.

Feel free to use, modify, and improve it for learning purposes.

---

# ⭐ Support

If you found this project helpful, please consider giving it a ⭐ on GitHub.

It helps others discover the project and motivates future improvements.
