package com.AM.demo.Service;

import com.AM.demo.Dto.DefaulterDto;
import com.AM.demo.Repository.AttendanceRepo;
import com.AM.demo.models.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaulterService {

    @Autowired
    private AttendanceRepo attendanceRepository;

    public List<DefaulterDto> getDefaulterList() {

        List<Attendance> attendanceList = attendanceRepository.findAll();

        Map<String, DefaulterDto> map = new HashMap<>();

        for (Attendance attendance : attendanceList) {

            // Subject-wise key
            String subjectKey = attendance.getRollno() + "_" + attendance.getSubject();

            // Combined attendance key
            String allSubjectKey = attendance.getRollno() + "_ALL";

            // ==========================
            // SUBJECT-WISE
            // ==========================

            if (!map.containsKey(subjectKey)) {

                DefaulterDto dto = new DefaulterDto();

                dto.setRollno(attendance.getRollno());
                dto.setName(attendance.getName());
                dto.setContact(attendance.getContact());
                dto.setSubject(attendance.getSubject());

                dto.setPresent(0);
                dto.setTotal(0);

                map.put(subjectKey, dto);
            }

            DefaulterDto subjectDto = map.get(subjectKey);

            subjectDto.setTotal(subjectDto.getTotal() + 1);

            if ("Present".equalsIgnoreCase(attendance.getStatus())) {
                subjectDto.setPresent(subjectDto.getPresent() + 1);
            }

            // ==========================
            // ALL SUBJECTS
            // ==========================

            if (!map.containsKey(allSubjectKey)) {

                DefaulterDto dto = new DefaulterDto();

                dto.setRollno(attendance.getRollno());
                dto.setName(attendance.getName());
                dto.setContact(attendance.getContact());
                dto.setSubject("All Subjects");

                dto.setPresent(0);
                dto.setTotal(0);

                map.put(allSubjectKey, dto);
            }

            DefaulterDto allDto = map.get(allSubjectKey);

            allDto.setTotal(allDto.getTotal() + 1);

            if ("Present".equalsIgnoreCase(attendance.getStatus())) {
                allDto.setPresent(allDto.getPresent() + 1);
            }
        }

        // ==========================
        // CALCULATE PERCENTAGE
        // ==========================

        List<DefaulterDto> result = new ArrayList<>();

        for (DefaulterDto dto : map.values()) {

            double percentage = 0;

            if (dto.getTotal() > 0) {
                percentage = ((double) dto.getPresent() / dto.getTotal()) * 100;
            }

            dto.setPercentage(Math.round(percentage));

            if (percentage < 75) {
                dto.setAttendanceStatus("Defaulter");
            } else if (percentage < 80) {
                dto.setAttendanceStatus("At Risk");
            } else {
                dto.setAttendanceStatus("Safe");
            }

            result.add(dto);
        }

        result.sort(Comparator.comparing(DefaulterDto::getName));

        return result;
    }

    public List<DefaulterDto> getExcelData() {
        return getDefaulterList();
    }
}