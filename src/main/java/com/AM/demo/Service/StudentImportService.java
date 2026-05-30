package com.AM.demo.Service;

import com.AM.demo.Repository.UserRepo;
import com.AM.demo.models.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class StudentImportService {

    private final Logger logger = LoggerFactory.getLogger(StudentImportService.class);

    @Autowired
    private UserRepo userRepo;

    public static class ErrorDetail {
        public int row;
        public String message;
        public ErrorDetail(int row, String message){ this.row=row; this.message=message; }
    }

    public static class UploadResult {
        public int insertedCount;
        public int failedCount;
        public List<ErrorDetail> errors = new ArrayList<>();
    }

    @Transactional
    public UploadResult importFromExcel(MultipartFile file){
        UploadResult result = new UploadResult();

        if(file==null || file.isEmpty()){
            result.errors.add(new ErrorDetail(0, "File is empty"));
            result.failedCount = result.errors.size();
            return result;
        }

        try (InputStream is = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is)){
            XSSFSheet sheet = workbook.getSheetAt(0);
            if(sheet==null){
                result.errors.add(new ErrorDetail(0, "No sheet found in Excel file"));
                result.failedCount = result.errors.size();
                return result;
            }

            // track duplicates within file
            Set<String> emailsInFile = new HashSet<>();
            Set<Integer> rollsInFile = new HashSet<>();

            List<User> toSave = new ArrayList<>();

            int lastRow = sheet.getLastRowNum();
            for(int r = 1; r <= lastRow; r++){ // skip header row (row 0)
                Row row = sheet.getRow(r);
                if(row==null) continue;

                try{
                    String name = getStringCell(row,0);
                    String email = getStringCell(row,1);
                    String password = getStringCell(row,2);
                    String role = getStringCell(row,3);
                    Integer rollno = getIntegerCell(row,4);
                    String year = getStringCell(row,6);
                    String contact = getStringCell(row,5);

                    // basic validation
                    if(name==null || name.isEmpty()){
                        result.errors.add(new ErrorDetail(r+1, "Name required"));
                        continue;
                    }
                    if(email==null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")){
                        result.errors.add(new ErrorDetail(r+1, "Invalid or missing email"));
                        continue;
                    }
                    if(rollno==null){
                        result.errors.add(new ErrorDetail(r+1, "Roll number required"));
                        continue;
                    }

                    // normalize
                    email = email.toLowerCase().trim();
                    if(role==null || role.isEmpty()) role = "Student"; // default

                    // check duplicates in file
                    if(emailsInFile.contains(email)){
                        result.errors.add(new ErrorDetail(r+1, "Duplicate email in file"));
                        continue;
                    }
                    if(rollsInFile.contains(rollno)){
                        result.errors.add(new ErrorDetail(r+1, "Duplicate roll number in file"));
                        continue;
                    }

                    // check DB duplicates
                    if(userRepo.findByEmail(email) != null){
                        result.errors.add(new ErrorDetail(r+1, "Email already exists in DB"));
                        continue;
                    }
                    if(userRepo.findByRollno(rollno) != null){
                        result.errors.add(new ErrorDetail(r+1, "Roll number already exists in DB"));
                        continue;
                    }

                    emailsInFile.add(email);
                    rollsInFile.add(rollno);

                    User u = new User();
                    u.setName(name);
                    u.setEmail(email);
                    u.setPassword(password==null||password.isEmpty()?"changeme":password);
                    u.setRole(role);
                    u.setRollno(rollno);
                    u.setYear(year);
                    u.setContact(contact);

                    toSave.add(u);

                }catch(Exception ex){
                    logger.error("Error parsing row {}", r+1, ex);
                    result.errors.add(new ErrorDetail(r+1, "Error parsing row: " + ex.getMessage()));
                }
            }

            // bulk save
            if(!toSave.isEmpty()){
                try{
                    userRepo.saveAll(toSave);
                    result.insertedCount = toSave.size();
                }catch(Exception ex){
                    // fallback: try saving one-by-one to collect per-row failures
                    logger.error("Bulk save failed, trying per-row save", ex);
                    int success=0;
                    for(User u: toSave){
                        try{
                            userRepo.save(u);
                            success++;
                        }catch(Exception e){
                            logger.error("Failed saving user {}", u.getEmail(), e);
                            result.errors.add(new ErrorDetail(-1, "DB insert failed for email="+u.getEmail()+" : "+e.getMessage()));
                        }
                    }
                    result.insertedCount = success;
                }
            }

            result.failedCount = result.errors.size();
            return result;

        }catch(Exception e){
            logger.error("Failed to process Excel file", e);
            result.errors.add(new ErrorDetail(0, "Failed to read Excel file: "+e.getMessage()));
            result.failedCount = result.errors.size();
            return result;
        }
    }

    private String getStringCell(Row row, int idx){
        Cell c = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c == null) return null;
        CellType type = c.getCellType();
        switch (type) {
            case STRING:
                String s = c.getStringCellValue();
                return s != null ? s.trim() : null;
            case NUMERIC:
                double d = c.getNumericCellValue();
                // avoid scientific notation for integer-like values
                if (d == Math.rint(d)) {
                    return String.valueOf((long) d);
                }
                return String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(c.getBooleanCellValue());
            case FORMULA:
                // handle formula: try cached result
                CellType resType = c.getCachedFormulaResultType();
                if (resType == CellType.STRING) {
                    String sf = c.getStringCellValue();
                    return sf != null ? sf.trim() : null;
                } else if (resType == CellType.NUMERIC) {
                    double dn = c.getNumericCellValue();
                    if (dn == Math.rint(dn)) return String.valueOf((long) dn);
                    return String.valueOf(dn);
                } else if (resType == CellType.BOOLEAN) {
                    return String.valueOf(c.getBooleanCellValue());
                }
                return null;
            case BLANK:
            default:
                return null;
        }
    }

    private Integer getIntegerCell(Row row, int idx){
        Cell c = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c == null) return null;
        try {
            CellType type = c.getCellType();
            if (type == CellType.NUMERIC) {
                double d = c.getNumericCellValue();
                if (Double.isFinite(d) && d == Math.rint(d)) {
                    return (int) d;
                }
                // fractional numeric cannot be safely converted to integer
                return null;
            } else if (type == CellType.STRING) {
                String s = c.getStringCellValue().trim();
                if (s.isEmpty()) return null;
                return Integer.parseInt(s);
            } else if (type == CellType.FORMULA) {
                CellType resType = c.getCachedFormulaResultType();
                if (resType == CellType.NUMERIC) {
                    double d = c.getNumericCellValue();
                    if (Double.isFinite(d) && d == Math.rint(d)) return (int) d;
                    return null;
                } else if (resType == CellType.STRING) {
                    String s = c.getStringCellValue().trim();
                    if (s.isEmpty()) return null;
                    return Integer.parseInt(s);
                }
                return null;
            }
            return null;
        } catch (Exception e) {
            logger.debug("Unable to parse integer cell at index {}: {}", idx, e.getMessage());
            return null;
        }
    }
}
