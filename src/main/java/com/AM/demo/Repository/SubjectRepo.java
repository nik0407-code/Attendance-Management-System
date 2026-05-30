package com.AM.demo.Repository;

import com.AM.demo.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepo extends JpaRepository<Subject,Integer> {
    Subject findBySubject(String subject);
}
