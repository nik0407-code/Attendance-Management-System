package com.AM.demo.Controller;

import com.AM.demo.Dto.AttendanceDto;
import com.AM.demo.Repository.AttendanceRepo;
import com.AM.demo.Repository.SubjectRepo;
import com.AM.demo.Repository.UserRepo;
import com.AM.demo.models.Attendance;
import com.AM.demo.models.Subject;
import com.AM.demo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin( origins = "*")

public class AttendanceController {

    @Autowired
    AttendanceRepo attendanceRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    SubjectRepo subjectRepo;


    @PostMapping("/markAttendance")
    public String markAttendance(@RequestBody AttendanceDto dto){

        // Prevent duplicate attendance
        List<Attendance> existing = attendanceRepo
                .findByDateAndSubject(dto.getDate(), dto.getSubjectName());



        // Get all students
        List<User> students = userRepo.findAll()
                .stream()
                .filter(u -> u.getRole().equalsIgnoreCase("Student"))
                .toList();

        for(User u : students){

            Attendance a = new Attendance();

            a.setName(u.getName());
            a.setContact(u.getContact());
            a.setRollno(u.getRollno());

            a.setSubject(dto.getSubjectName());
            a.setDate(dto.getDate());
            a.setTime(dto.getTime());

            // 🔥 FIXED LINE
            if(dto.getStudents().contains(u.getRollno())){
                a.setStatus("Present");
            } else {
                a.setStatus("Absent");
            }

            attendanceRepo.save(a);
        }

        return "Attendance saved successfully";
    }

    // ✅ GET ALL ATTENDANCE (sorted by rollno)
    @GetMapping("/attendance")
    public List<Attendance> getAllAttendance() {

        return attendanceRepo.findAllByOrderByRollnoAsc();
    }

    @GetMapping("/subjects")
    public List<Subject> getAllSubjects() {
        return subjectRepo.findAll();
    }

    @GetMapping("/getAttendance")
    public List<Attendance> getAttendance(
            @RequestParam String date,
            @RequestParam String subject,
            @RequestParam String time){

        return attendanceRepo.findByDateAndSubjectAndTime(date, subject, time);
    }

    @PutMapping("/updateAttendance")
    public String updateAttendance(@RequestBody List<Attendance> list){

        for(Attendance a : list){

            Attendance existing = attendanceRepo.findById(a.getId()).orElse(null);

            if(existing != null){
                existing.setStatus(a.getStatus());
                attendanceRepo.save(existing);
            }
        }

        return "Attendance updated successfully";
    }

    @DeleteMapping("/attendance/delete")
    public ResponseEntity<?> deleteAttendance(@RequestBody Map<String, List<Integer>> body) {
        List<Integer> ids = body.get("ids");
        attendanceRepo.deleteAllById(ids);
        return ResponseEntity.ok(Map.of("success", true));
    }



    }



