package com.AM.demo.Controller;

import com.AM.demo.Service.StudentImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/students")
public class StudentBulkController {

    @Autowired
    private StudentImportService importService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file){
        StudentImportService.UploadResult result = importService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }
}
