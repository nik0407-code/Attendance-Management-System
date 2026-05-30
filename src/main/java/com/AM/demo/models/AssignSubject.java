package com.AM.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String teacherName;


    private String teacherEmail;


    private String subjectName;
}
