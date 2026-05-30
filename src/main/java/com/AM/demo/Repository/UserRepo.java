package com.AM.demo.Repository;

import com.AM.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface UserRepo extends JpaRepository<User,Integer> {

    User findByEmail(String email);


    User findByRollno(Integer rollno);

    List<User> findByRole(String teacher);
}
