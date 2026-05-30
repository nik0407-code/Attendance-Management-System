package com.AM.demo.Controller;

import com.AM.demo.Dto.AssignSubjectDto;
import com.AM.demo.Repository.AssignSubjectRepo;
import com.AM.demo.Repository.UserRepo;
import com.AM.demo.models.AssignSubject;
import com.AM.demo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class AssignSubjectController {

    @Autowired
    AssignSubjectRepo assignSubjectRepo;

    @Autowired
    UserRepo userRepo;

    // ✅ ASSIGN SUBJECT
    @PostMapping("/assignSubject")
    public String assignSubject(@RequestBody AssignSubjectDto u){

        User teacher = userRepo.findByEmail(u.getTeacherEmail());

        if(teacher == null){
            return "Teacher not found";
        }

        if(teacher.getRole() == null || !teacher.getRole().equalsIgnoreCase("Teacher")){
            return "Not a teacher";
        }

        AssignSubject existing = assignSubjectRepo
                .findByTeacherEmailAndSubjectName(u.getTeacherEmail(), u.getSubjectName());

        if(existing != null){
            return "Already assigned";
        }


        AssignSubject a = new AssignSubject();

        a.setTeacherName(teacher.getName());
        a.setTeacherEmail(teacher.getEmail());
        a.setSubjectName(u.getSubjectName());

        assignSubjectRepo.save(a);


        return "Subject assigned successfully";
    }

    // ✅ GET ALL ASSIGNED
    @GetMapping("/getAssignedSubjects")
    public List<AssignSubject> getAll()
    {
        return assignSubjectRepo.findAll();
    }

    // ✅ DELETE
    @DeleteMapping("/deleteAssign/{id}")
    public String delete(@PathVariable int id)
    {
        assignSubjectRepo.deleteById(id);
        return "Deleted successfully";
    }
}