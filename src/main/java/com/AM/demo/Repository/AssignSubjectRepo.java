package com.AM.demo.Repository;

import com.AM.demo.models.AssignSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignSubjectRepo extends JpaRepository<AssignSubject,Integer> {


    AssignSubject findByTeacherEmailAndSubjectName(String teacherEmail, String subjectName);
}
