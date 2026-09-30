
package com.deepak.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class student_dashboard extends AppCompatActivity {

    TextView studentName;
    TextView studentRoll;
    TextView studentBranch;
    TextView studentYear;
    TextView studentSemester;
    String username;

    ImageView studentPhoto;
  Button attendance_check_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);


        studentName = findViewById(R.id.student_name);
        studentRoll = findViewById(R.id.student_roll);
        studentBranch = findViewById(R.id.student_branch);
        studentYear = findViewById(R.id.student_year);
        studentSemester = findViewById(R.id.student_semester);
        attendance_check_button=findViewById(R.id.attendance_btn);
//      studentPhoto = findViewById(R.id.student_photo);


        String name = getIntent().getStringExtra("name");
        String rollNo = getIntent().getStringExtra("roll_no");
        String branch = getIntent().getStringExtra("branch");
        String year = getIntent().getStringExtra("year");
        String semester = getIntent().getStringExtra("semester");

        username = getIntent().getStringExtra("username");

        studentName.setText("Name - " + name);
        studentRoll.setText("Roll No. - " + rollNo);
        studentBranch.setText("Branch - " + branch);
        studentYear.setText("Year - " + year);
        studentSemester.setText("Semester - " + semester);

        attendance_check_button.setOnClickListener(v -> {
            Intent intent = new Intent(student_dashboard.this, attendance_dashboard.class);
            intent.putExtra("username", rollNo);
            intent.putExtra("year",year);
            intent.putExtra("branch",branch);
            startActivity(intent);
        });

//        // Student photo
//        studentPhoto.setImageResource(R.drawable.student_photo);
    }
}