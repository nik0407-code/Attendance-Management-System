package com.AM.demo.Repository;

import com.AM.demo.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepo extends JpaRepository<Attendance,Integer> {




    List<Attendance> findByDateAndSubject(String date, String subjectName);

    List<Attendance> findAllByOrderByRollnoAsc();

    List<Attendance> findByDateAndSubjectAndTime(String date, String subject, String time);
}
