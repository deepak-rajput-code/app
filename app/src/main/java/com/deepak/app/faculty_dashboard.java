
package com.deepak.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class faculty_dashboard extends AppCompatActivity {


    ImageView imgFaculty;
    TextView tvFacultyName;
    TextView textView2;
    RadioGroup radioGroupBranch;
    RadioGroup radioGroupYear;
    RadioButton radioIT;
    RadioButton radioEE;
    RadioButton radioCE;

    RadioButton radioYear1;
    RadioButton radioYear2;
    RadioButton radioYear3;
    RadioButton radioYear4;

    EditText etSubjectCode;

    Button btnLoadStudents;
    Button btnDownloadAttendance;


    LinearLayout studentSection;

    TextView tvStudentNumber;
    TextView tvRollNumber;
    TextView tvStudentName;
    TextView tvDate;

    Button btnPresent;
    Button btnAbsent;
    Button btnNext;


    DatabaseReference databaseReference;

    List<Student> studentList = new ArrayList<>();

    int currentStudentIndex = 0;

    String selectedBranch = "";
    String selectedYear = "";
    String subjectCode = "";

    String todayDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);
        imgFaculty = findViewById(R.id.imgFaculty);
        tvFacultyName = findViewById(R.id.tvFacultyName);
        textView2 = findViewById(R.id.textView2);
        radioGroupBranch = findViewById(R.id.radioGroupBranch);
        radioGroupYear = findViewById(R.id.radioGroupYear);
        radioIT = findViewById(R.id.radioIT);
        radioEE = findViewById(R.id.radioEE);
        radioCE = findViewById(R.id.radioCE);
        radioYear1 = findViewById(R.id.radioYear1);
        radioYear2 = findViewById(R.id.radioYear2);
        radioYear3 = findViewById(R.id.radioYear3);
        radioYear4 = findViewById(R.id.radioYear4);
        etSubjectCode = findViewById(R.id.etSubjectCode);
        btnLoadStudents = findViewById(R.id.btnLoadStudents);
        btnDownloadAttendance = findViewById(R.id.btnDownloadAttendance);
        studentSection = findViewById(R.id.studentSection);
        tvStudentNumber = findViewById(R.id.tvStudentNumber);
        tvRollNumber = findViewById(R.id.tvRollNumber);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvDate = findViewById(R.id.tvDate);
        btnPresent = findViewById(R.id.btnPresent);
        btnAbsent = findViewById(R.id.btnAbsent);
        btnNext = findViewById(R.id.btnNext);
        databaseReference = FirebaseDatabase.getInstance().getReference("RECB_DATABASE");
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText("Date: " + todayDate);
        String facultyName = getIntent().getStringExtra("facultyName");
        String facultyUsername = getIntent().getStringExtra("username");
        if (facultyName != null && !facultyName.isEmpty()) {
            tvFacultyName.setText(facultyName);
        } else {
            tvFacultyName.setText("Faculty");
        }
        if (facultyUsername != null) {
            textView2.setText("Username: " + facultyUsername);
        }
        radioGroupBranch.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.radioIT) {
                        selectedBranch = "IT";
                    } else if (checkedId == R.id.radioEE) {
                        selectedBranch = "EE";
                    } else if (checkedId == R.id.radioCE) {
                        selectedBranch = "CE";
                    }
                }
        );

        radioGroupYear.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.radioYear1) {
                        selectedYear = "1";
                    } else if (checkedId == R.id.radioYear2) {
                        selectedYear = "2";
                    } else if (checkedId == R.id.radioYear3) {
                        selectedYear = "3";
                    } else if (checkedId == R.id.radioYear4) {
                        selectedYear = "4";
                    }
                }
        );
        btnLoadStudents.setOnClickListener(v -> loadStudents());
        btnPresent.setOnClickListener(v -> markAttendance("P"));
        btnAbsent.setOnClickListener(v -> markAttendance("A"));
        btnNext.setOnClickListener(v -> nextStudent());
        btnDownloadAttendance.setOnClickListener(v -> downloadAttendance());
    }
    private void loadStudents() {
        if (selectedBranch.isEmpty()) {
            Toast.makeText(this, "Please select branch", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedYear.isEmpty()) {
            Toast.makeText(this, "Please select year", Toast.LENGTH_SHORT).show();
            return;
        }
        subjectCode = etSubjectCode.getText().toString().trim().toUpperCase();
        if (subjectCode.isEmpty()) {etSubjectCode.setError("Enter subject code");
            etSubjectCode.requestFocus();
            return;
        }
        textView2.setText("Subject Code: " + subjectCode);
        studentList.clear();
        currentStudentIndex = 0;
        DatabaseReference studentsReference = databaseReference.child(selectedBranch).child(selectedYear);
        studentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(faculty_dashboard.this, "No students found", Toast.LENGTH_SHORT).show();
                            studentSection.setVisibility(LinearLayout.GONE);
                            return;
                        }

                        for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                            String name = studentSnapshot.child("Name").getValue(String.class);
                            String rollNo = studentSnapshot.child("Roll_No").getValue(String.class);
                            String username = studentSnapshot.child("Username").getValue(String.class);

                            if (name == null) {
                                name = "Unknown";
                            }
                            if (rollNo == null) {
                                rollNo = studentSnapshot.getKey();
                            }
                            if (username == null) {
                                username = studentSnapshot.getKey();
                            }
                            Student student = new Student(name,rollNo,username);
                            studentList.add(student);
                        }

                        studentList.sort((s1, s2) -> s1.rollNo.compareTo(s2.rollNo));
                        if (!studentList.isEmpty()) {
                            studentSection.setVisibility(LinearLayout.VISIBLE);
                            currentStudentIndex = 0;
                            showCurrentStudent();
                            Toast.makeText(faculty_dashboard.this, studentList.size() + " students loaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(faculty_dashboard.this, "No students found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(faculty_dashboard.this, "Firebase Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void showCurrentStudent() {
        if (studentList.isEmpty()) {
            return;
        }
        Student student = studentList.get(currentStudentIndex);
        tvStudentNumber.setText("Student " + (currentStudentIndex + 1) + " of " + studentList.size());
        tvRollNumber.setText("Roll Number: " + student.rollNo);
        tvStudentName.setText(student.name);
        tvDate.setText("Date: " + todayDate);
        if (currentStudentIndex == studentList.size() - 1) {
            btnNext.setText("FINISH ATTENDANCE");
        } else {
            btnNext.setText("NEXT STUDENT →");
        }
    }

    private void markAttendance(String status) {
        if (studentList.isEmpty()) {
            Toast.makeText(this, "Load students first", Toast.LENGTH_SHORT).show();
            return;
        }
        Student student = studentList.get(currentStudentIndex);
        DatabaseReference attendanceReference = databaseReference
                        .child(selectedBranch)
                        .child(selectedYear)
                        .child(student.username)
                        .child("mark_attendance")
                        .child(subjectCode)
                        .child(todayDate);
        attendanceReference.setValue(status).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (status.equals("P")) {
                            Toast.makeText(faculty_dashboard.this, student.name + " marked PRESENT", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(faculty_dashboard.this, student.name + " marked ABSENT", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(faculty_dashboard.this, "Failed to save attendance", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void nextStudent() {
        if (studentList.isEmpty()) {
            return;
        }
        if (currentStudentIndex >= studentList.size() - 1) {
            Toast.makeText(this, "Attendance completed for all students", Toast.LENGTH_LONG).show();
            return;
        }
        currentStudentIndex++;
        showCurrentStudent();
    }
    private void downloadAttendance() {
        if (selectedBranch.isEmpty() || selectedYear.isEmpty()) {
            Toast.makeText(this, "Select branch and year first", Toast.LENGTH_SHORT).show();
            return;
        }
        subjectCode = etSubjectCode.getText().toString().trim().toUpperCase();
        if (subjectCode.isEmpty()) {
            Toast.makeText(this, "Enter subject code first", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference studentsReference = databaseReference.child(selectedBranch).child(selectedYear);
        studentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StringBuilder csv = new StringBuilder();
                        csv.append("Roll Number,Student Name");
                        List<String> allDates = new ArrayList<>();
                        for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                            DataSnapshot attendanceSnapshot = studentSnapshot.child("mark_attendance").child(subjectCode);
                            for (DataSnapshot dateSnapshot : attendanceSnapshot.getChildren()) {
                                String date = dateSnapshot.getKey();
                                if (date != null && !allDates.contains(date)) {
                                    allDates.add(date);
                                }
                            }
                        }
                        allDates.sort(String::compareTo);
                        for (String date : allDates) {
                            csv.append(",").append(date);
                        }
                        csv.append("\n");
                        for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                            String name = studentSnapshot.child("Name").getValue(String.class);
                            String rollNo = studentSnapshot.child("Roll_No").getValue(String.class);
                            if (name == null) {
                                name = "Unknown";
                            }
                            if (rollNo == null) {rollNo = studentSnapshot.getKey();
                            }
                            csv.append(escapeCsv(rollNo)).append(",").append(escapeCsv(name));
                            for (String date : allDates) {
                                String status = "";
                                DataSnapshot statusSnapshot = studentSnapshot.child("mark_attendance").child(subjectCode).child(date);
                                if (statusSnapshot.exists()) {
                                    String value = statusSnapshot.getValue(String.class);
                                    if (value != null) {
                                        status = value;
                                    }
                                }
                                csv.append(",").append(status);
                            }
                            csv.append("\n");
                        }

                        saveCsvFile(csv.toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(faculty_dashboard.this, "Firebase Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
    private void saveCsvFile(String csvData) {
        String fileName = selectedBranch + "_Year" + selectedYear + "_" + subjectCode + "_Attendance.csv";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        getPreferences(MODE_PRIVATE).edit().putString("csv_data", csvData).apply();
        startActivityForResult(intent, 1001);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            String csvData = getPreferences(MODE_PRIVATE).getString("csv_data", "");
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    outputStream.write(csvData.getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                    Toast.makeText(this, "Attendance downloaded successfully", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "File Error: " + e.getMessage(), Toast.LENGTH_LONG
                ).show();
            }
        }
    }
    private static class Student {
        String name;
        String rollNo;
        String username;
        Student(String name, String rollNo, String username) {
            this.name = name;
            this.rollNo = rollNo;
            this.username = username;
        }
    }
}