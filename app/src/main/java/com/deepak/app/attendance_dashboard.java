package com.deepak.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class attendance_dashboard extends AppCompatActivity {
    DatabaseReference databaseReference;

    String username;
    String branch;
    String year;

    List<String> subjectCodes = new ArrayList<>();
    List<String> subjectNames = new ArrayList<>();

    // Maximum 6 subjects according to your XML
    TextView[] subjectCodeTextViews = new TextView[6];
    TextView[] subjectNameTextViews = new TextView[6];
    Button[] attendanceButtons = new Button[6];
    CardView[] subjectCards = new CardView[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_dashboard);

        username = getIntent().getStringExtra("username");
        branch = getIntent().getStringExtra("branch");
        year = getIntent().getStringExtra("year");


        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Username not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (branch == null || branch.isEmpty()) {Toast.makeText(this, "Branch not found", Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }
        if (year == null || year.isEmpty()) {
            Toast.makeText(this, "Year not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("RECB_DATABASE");
        initializeViews();
        loadSubjects();
    }

    private void initializeViews() {


        subjectCodeTextViews[0] = findViewById(R.id.subject_code_1);
        subjectNameTextViews[0] = findViewById(R.id.subject_name_1);
        attendanceButtons[0] = findViewById(R.id.subject_attendance_1);


        subjectCodeTextViews[1] = findViewById(R.id.subject_code_2);
        subjectNameTextViews[1] = findViewById(R.id.subject_name_2);
        attendanceButtons[1] = findViewById(R.id.subject_attendance_2);


        subjectCodeTextViews[2] = findViewById(R.id.subject_code_3);
        subjectNameTextViews[2] = findViewById(R.id.subject_name_3);
        attendanceButtons[2] = findViewById(R.id.subject_attendance_3);


        subjectCodeTextViews[3] = findViewById(R.id.subject_code_4);
        subjectNameTextViews[3] = findViewById(R.id.subject_name_4);
        attendanceButtons[3] = findViewById(R.id.subject_attendance_4);


        subjectCodeTextViews[4] = findViewById(R.id.subject_code_5);
        subjectNameTextViews[4] = findViewById(R.id.subject_name_5);
        attendanceButtons[4] = findViewById(R.id.subject_attendance_5);


        subjectCodeTextViews[5] = findViewById(R.id.subject_code_6);
        subjectNameTextViews[5] = findViewById(R.id.subject_name_6);
        attendanceButtons[5] = findViewById(R.id.subject_attendance_6);


        for (int i = 0; i < 6; i++) {

            subjectCodeTextViews[i].setVisibility(View.GONE);
            subjectNameTextViews[i].setVisibility(View.GONE);
            attendanceButtons[i].setVisibility(View.GONE);
        }
    }

    private void loadSubjects() {

        DatabaseReference subjectsReference =
                databaseReference.child(branch).child(year).child(username).child("Subjects");

        subjectsReference.get().addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(attendance_dashboard.this, "No subjects found", Toast.LENGTH_LONG).show();
                        return;
                    }

                    subjectCodes.clear();
                    subjectNames.clear();

                    for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                        String subjectCode = subjectSnapshot.child("Subject_Code").getValue(String.class);

                        String subjectName = subjectSnapshot.child("Subject_Name").getValue(String.class);

                        if (subjectCode != null && !subjectCode.isEmpty()) {
                            subjectCodes.add(subjectCode);
                            if (subjectName != null) {subjectNames.add(subjectName);
                            } else {
                                subjectNames.add("");
                            }
                        }
                    }
                    if (subjectCodes.isEmpty()) {
                        Toast.makeText(attendance_dashboard.this, "No subjects found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    displaySubjects();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(attendance_dashboard.this, "Firebase Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private void displaySubjects() {

        int numberOfSubjects =
                Math.min(subjectCodes.size(), 6);

        for (int i = 0; i < numberOfSubjects; i++) {
            final int position = i;
            subjectCodeTextViews[i].setText(subjectCodes.get(i));
            subjectNameTextViews[i].setText(subjectNames.get(i));


            subjectCodeTextViews[i].setVisibility(View.VISIBLE);
            subjectNameTextViews[i].setVisibility(View.VISIBLE);
            attendanceButtons[i].setVisibility(View.VISIBLE);

            attendanceButtons[i].setOnClickListener(v -> {String selectedSubjectCode = subjectCodes.get(position);
                String selectedSubjectName = subjectNames.get(position);
                openAttendanceActivity(selectedSubjectCode, selectedSubjectName
                );
            });
        }
    }

    private void openAttendanceActivity(String subjectCode, String subjectName) {
        Intent intent = new Intent(attendance_dashboard.this, final_attendance_dashboard.class);

        intent.putExtra("username", username);
        intent.putExtra("branch", branch);
        intent.putExtra("year", year);

        intent.putExtra("subjectCode", subjectCode);
        intent.putExtra("subjectName", subjectName);
        startActivity(intent);
    }
}

