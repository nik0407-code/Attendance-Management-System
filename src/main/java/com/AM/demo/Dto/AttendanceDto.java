package com.AM.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {

    private String teacherEmail;
    private String subjectName;
    private String date;
    private String time;

    private List<Integer> students; // present students IDs
}