package com.AM.demo.Controller;

import com.AM.demo.Repository.SubjectRepo;
import com.AM.demo.models.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class SubjectController {

    @Autowired
    SubjectRepo subjectRepo;

    @PostMapping("/addSubject")
    public String addSubject(@RequestBody Subject s) {

        if (s.getSubject() == null || s.getSubject().trim().isEmpty()) {
            return "Subject name required";
        }

        Subject existing = subjectRepo.findBySubject(s.getSubject());

        if (existing != null) {
            return "Subject already exists";
        }

        subjectRepo.save(s);

        return "Subject added successfully";
    }

    @GetMapping("/getSubjects")
    public List<Subject> getSubjects() {
        return subjectRepo.findAll();
    }

    @DeleteMapping("/subjects/{id}")
    public String deleteSubject(@PathVariable int id){
        subjectRepo.deleteById(id);
        return "Deleted";
    }


}