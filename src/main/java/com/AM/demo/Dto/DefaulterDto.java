package com.AM.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class DefaulterDto {

    private Integer rollno;
    private String name;
    private String contact;
    private String subject;

    private int present;
    private int total;

    private double percentage;

    private String attendanceStatus;

}
