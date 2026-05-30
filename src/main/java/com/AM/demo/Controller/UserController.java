package com.AM.demo.Controller;

import com.AM.demo.Dto.EditProfileDto;
import com.AM.demo.Dto.LoginDto;
import com.AM.demo.Repository.UserRepo;
import com.AM.demo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*"     )

public class UserController {

    @Autowired
    UserRepo userRepo;

    @PostMapping("/signup")
    public String signup(@RequestBody User user){

        if(userRepo.findByEmail(user.getEmail()) != null){
            return "Email already exists";
        }
        if(user.getEmail() == null ||
                !user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return "Invalid Email Format";
        }

        if(user.getRole().equalsIgnoreCase("student") && user.getRollno() == null){
            return "Roll number required for student";
        }

        if(user.getRollno() != null && userRepo.findByRollno(user.getRollno()) != null){
            return "Roll number already exists";
        }

        userRepo.save(user);

        return "Signup Successful";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto u){

        User user = userRepo.findByEmail(u.getEmail());

        if(user == null)
        {
            return "email not found";
        }

        if(!user.getName().equals(u.getName()))
        {
            return "incorrect Name";
        }

        if(!user.getPassword().equals(u.getPassword()))
        {
            return "Incorrect password    ";
        }

        if(!user.getRole().equalsIgnoreCase(u.getRole()))
        {
            return "Incorrect Role";
        }

        return user.getRole();
    }
    @PostMapping("/addStudent")
    public String addStudent(@RequestBody User u)
    {
        User user = userRepo.findByEmail(u.getEmail());
        if(user != null)
        {
            return "Email already exists";
        }

        User user2 = userRepo.findByRollno(u.getRollno());
        if(user2 != null)
        {
            return "Roll number already exists";
        }

        u.setRole("Student");
        userRepo.save(u);

        return "Student added successfully";
    }

    @PostMapping("/addTeacher")
    public String addTeacher(@RequestBody User u)
    {
        if(u.getName()==null || u.getEmail()==null || u.getPassword()==null){
            return "All fields required";
        }

        User existing = userRepo.findByEmail(u.getEmail());

        if(existing != null){
            return "Email already exists";
        }

        u.setRole("Teacher");

        userRepo.save(u);

        return "Teacher added successfully";
    }

    @GetMapping("/getTeachers")
    public List<User> getTeachers() {
        return userRepo.findByRole("Teacher");
    }

    @GetMapping("/getUsers")
    public List<User> getUsers(){
        return userRepo.findAll();
    }

    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id){
        userRepo.deleteById(id);
        return "User deleted successfully";
    }

    @GetMapping("/profile")
    public User getStudent(@RequestParam String email)
    {
        System.out.println("EMAIL RECEIVED: " + email);
        return userRepo.findByEmail(email);
    }

    @PutMapping("/updateProfile")
    public String updateProfile(@RequestBody EditProfileDto dto) {

        User user = userRepo.findByEmail(dto.getCurrentEmail());

        if(user == null){
            return "User not found";
        }

        // ✅ EMAIL UPDATE
        if(dto.getEmail() != null && !dto.getEmail().isEmpty()){

            if(user.getEmail().equals(dto.getEmail())){
                return "Same email not allowed";
            }

            if(userRepo.findByEmail(dto.getEmail()) != null){
                return "Email already exists";
            }

            user.setEmail(dto.getEmail());
        }

        // ✅ PASSWORD UPDATE
        if(dto.getPassword() != null && !dto.getPassword().isEmpty()){

            if(user.getPassword().equalsIgnoreCase(dto.getPassword()))
            {
                return "Same password not allowed";
            }

            user.setPassword(dto.getPassword());
        }

        userRepo.save(user);

        return "Profile updated successfully";
    }
}
