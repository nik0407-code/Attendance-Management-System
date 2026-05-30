# Attendance Management System — README

This project is a Spring Boot (Java 17) Attendance Management System. This README explains the Excel Import (upload) and Export (Excel + PDF) features in simple English so a beginner can understand and use them.

---

## 📌 Feature Overview

- Excel Import (Upload)
  - Lets you upload a `.xlsx` file containing many students at once.
  - The backend reads the file and creates student records in the database.
  - Useful when you need to add many students quickly (bulk import).

- Export to Excel / PDF
  - Lets you download attendance records as an Excel file (`.xlsx`) or a PDF.
  - Useful for reports, printing, or sharing with others.

Why useful in real life:
- Bulk import saves time and reduces manual typing errors.
- Exporting to Excel or PDF makes it easy to share, print, or archive attendance data.

---

## 📥 Excel Import (Upload Feature)

Files created or modified for this feature:
- `src/main/resources/static/add-student.html` — added file input and JS to upload an Excel file
- `src/main/java/com/AM/demo/Controller/StudentBulkController.java` — added POST `/api/students/upload`
- `src/main/java/com/AM/demo/Service/StudentImportService.java` — reads Excel and saves `User` entities
- `pom.xml` — added Apache POI dependency

Step-by-step explanation (simple):
1. File selection (frontend)
   - The user clicks the file input on `add-student.html` and chooses an `.xlsx` file.
2. Sending file (frontend)
   - A JavaScript function uses `fetch()` and a `FormData` object to POST the file to the server.
   - Example (frontend JS):
     ```js
     const fd = new FormData();
     fd.append('file', fileInput.files[0]);
     fetch('/api/students/upload', { method: 'POST', body: fd });
     ```
3. Receiving file in Spring Boot
   - Controller method receives `MultipartFile file` with `@RequestParam("file")`.
4. Reading Excel using Apache POI
   - The service opens the workbook with `new XSSFWorkbook(file.getInputStream())`.
   - It reads the first sheet and iterates rows (skips header row at index 0).
5. Looping through rows
   - For each row, it reads cells in the expected order.
6. Mapping to `User` entity
   - Columns map to: `name`, `email`, `password`, `role`, `rollno`, `year`, `contact`.
   - The service validates and normalizes values (trim, lowercase email).
7. Saving to database
   - Valid rows are saved using JPA repository methods (e.g., `userRepo.saveAll()`).
   - Invalid rows are skipped and recorded in an error list.

Easy logic (for beginners):
- Think of each Excel row as a form submission for one student.
- The server reads each row and checks the values. If everything looks good, it saves the student. If something is wrong, it remembers the problem and goes to the next row.

Excel format required:
- File type: `.xlsx` (Excel workbook)
- First row: header (ignored by the importer)
- Columns (A..G, in this order):
  1. name
  2. email
  3. password
  4. role
  5. rollno
  6. year
  7. contact

Output (after upload):
- The service returns a JSON summary, for example:
  ```json
  {
    "insertedCount": 42,
    "failedCount": 2,
    "errors": [ {"row": 5, "message": "Invalid email"} ]
  }
  ```
- The frontend shows a friendly message with counts and row error details.

---

## 📤 Export to Excel

Files created/used:
- `src/main/java/com/AM/demo/Controller/ExportController.java` — endpoint `GET /export/excel`
- `pom.xml` — Apache POI dependency

How it works (simple):
1. Data fetch
   - Controller calls the attendance repository, e.g. `attendanceRepo.findAllByOrderByRollnoAsc()` to get the data.
2. Create Excel (Apache POI)
   - Create an `XSSFWorkbook`, and a sheet: `workbook.createSheet("Attendance")`.
   - Add a header row and then add one row per `Attendance` record.
   - Auto-size columns for readability using `sheet.autoSizeColumn(i)`.
3. Send as download
   - Set response headers:
     - `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
     - `Content-Disposition: attachment; filename=attendance.xlsx`
   - Write workbook bytes to `response.getOutputStream()`.

Frontend (button logic):
- A button calls a JS function that does `fetch('/export/excel')`, converts response to a `Blob`, and triggers a download with a temporary anchor element.

Output file:
- `attendance.xlsx` containing columns: `Roll No, Name, Contact, Subject, Date, Time, Status`.

---

## 📄 Export to PDF

How PDF is generated:
- The project uses OpenPDF (a maintained alternative to older iText APIs).
- The controller creates a `Document`, a `PdfPTable` with 7 columns, adds headers and rows, then writes bytes to the response output.

Table logic (simple):
- Create `PdfPTable(7)`, add header cells using `table.addCell("Header")`, then add each attendance row with `table.addCell(String.valueOf(value))`.

Download response:
- Controller sets `Content-Type: application/pdf` and `Content-Disposition: attachment; filename=attendance.pdf` and writes the generated PDF bytes.
- Frontend fetches `/export/pdf`, turns the response into a blob, and triggers a download.

---

## 🔧 Files Modified / Created (summary)

- `src/main/resources/static/add-student.html` — added file input and `uploadExcel()` JS function to post Excel files
- `src/main/resources/static/view-attendanceAdmin.html` — added Export to Excel and Download PDF buttons and JS (`downloadExcelFromServer()` and `downloadPdfFromServer()`)
- `src/main/java/com/AM/demo/Controller/StudentBulkController.java` — POST `/api/students/upload` endpoint
- `src/main/java/com/AM/demo/Service/StudentImportService.java` — reads `.xlsx`, validates rows, saves users and returns results
- `src/main/java/com/AM/demo/Controller/ExportController.java` — GET `/export/excel` and GET `/export/pdf`
- `pom.xml` — added `poi-ooxml` and `openpdf` dependencies + compiler plugin for Java 17

Why each changed:
- HTML: to provide UI controls and JS to call the backend upload/download endpoints.
- Controllers: expose REST endpoints for upload and file downloads.
- Service: centralize Excel parsing logic and DB operations.
- `pom.xml`: to include the libraries needed for Excel and PDF processing.

---

## ⚙️ Dependencies Added (short)

- Apache POI (`org.apache.poi:poi-ooxml`) — needed to read and write `.xlsx` Excel files.
- OpenPDF (`com.github.librepdf:openpdf`) — used to create PDF files on the server.
- Maven compiler plugin configured to use Java 17.

---

## 🔄 Complete Flow (visual, simple)

- Excel Import:
  `User -> (select file in browser) -> POST /api/students/upload -> StudentImportService -> DB (User table) -> JSON result -> UI shows summary`

- Export Excel/PDF:
  `User -> click Export -> GET /export/excel or /export/pdf -> ExportController -> Build file (POI/OpenPDF) -> response -> browser downloads file`

---

## 🎯 Output (what the user sees)

- After Upload: summary message with counts and errors (if any).
- After Export Excel: `attendance.xlsx` downloaded.
- After Export PDF: `attendance.pdf` downloaded.

---

## 💡 Notes / Learnings (common issues)

- `CELL_TYPE_STRING` / `CELL_TYPE_NUMERIC` errors: these were removed in POI 5.x. Use `cell.getCellType()` and the `CellType` enum instead.
- `Cannot resolve symbol 'poi'` or `'lowagie'`: means Maven dependencies are not downloaded or the IDE needs a Maven reimport. Run:

```bash
cd demo
mvn -U -DskipTests=true clean compile
```

- If Maven fails to download, check proxy or `~/.m2/settings.xml`.
- For large Excel exports consider `SXSSFWorkbook` (streaming) to reduce memory.

---

## Next steps (suggested)

- Add header name based mapping for Excel so columns can be in any order.
- Add authentication for export/upload endpoints so only admins can use them.
- Provide a CSV of failed rows for easier corrections and re-upload.

---

If you want, I can also add this README to a different path, or create a short `docs/IMPORT_EXPORT.md` file with more examples and sample Excel templates. Tell me where you want the file or any edits to its content.
