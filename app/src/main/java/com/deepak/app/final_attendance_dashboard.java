package com.deepak.app;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class final_attendance_dashboard extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private String username;
    private String branch;
    private String year;
    private String subjectCode;
    private String subjectName;
    private TextView subjectCodeText;
    private TextView subjectNameText;
    private TextView totalPresent;
    private TextView totalAbsent;
    private TextView attendancePercentage;
    private TextView noAttendance;
    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private final List<AttendanceModel> attendanceList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_attendance_dashboard);
        username = getIntent().getStringExtra("username");
        branch = getIntent().getStringExtra("branch");
        year = getIntent().getStringExtra("year");
        subjectCode = getIntent().getStringExtra("subjectCode");
        subjectName = getIntent().getStringExtra("subjectName");
        if (username == null || branch == null || year == null || subjectCode == null) {
            Toast.makeText(this, "Attendance information not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("RECB_DATABASE");
        initializeViews();
        showSubjectInformation();
        setupRecyclerView();
        loadAttendance();
    }
    private void initializeViews() {
        subjectCodeText = findViewById(R.id.final_subject_code);
        subjectNameText = findViewById(R.id.final_subject_name);
        totalPresent = findViewById(R.id.total_present);
        totalAbsent = findViewById(R.id.total_absent);
        attendancePercentage = findViewById(R.id.attendance_percentage);
        noAttendance = findViewById(R.id.no_attendance);
        recyclerView = findViewById(R.id.attendance_recycler_view);
    }
    private void showSubjectInformation() {
        subjectCodeText.setText(subjectCode);
        if (subjectName != null && !subjectName.isEmpty()) {
            subjectNameText.setText(subjectName);
        } else {
            subjectNameText.setVisibility(View.GONE);
        }
    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter(attendanceList);
        recyclerView.setAdapter(adapter);
    }
private void loadAttendance() {
    DatabaseReference attendanceReference = databaseReference
            .child(branch)
            .child(year)
            .child(username)
            .child("mark_attendance")
            .child(subjectCode);
    attendanceReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    attendanceList.clear();
                    int present = 0;
                    int absent = 0;
                    if (!snapshot.exists()) {
                        noAttendance.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        updateSummary(0, 0);
                        return;
                    }
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        String status = dateSnapshot.getValue(String.class);
                        if (date == null || status == null) {
                            continue;
                        }
                        status = status.trim();
                        attendanceList.add(new AttendanceModel(date, status));
                        if (status.equalsIgnoreCase("P")) {
                            present++;
                        } else if (status.equalsIgnoreCase("A")) {
                            absent++;
                        }
                    }
                    Collections.sort(attendanceList,new Comparator<AttendanceModel>() {
                                @Override
                                public int compare(
                                        AttendanceModel a,
                                        AttendanceModel b) {
                                    return a.getDate().compareTo(b.getDate());
                                }
                            });
                    Toast.makeText(final_attendance_dashboard.this, "Attendance records: " + attendanceList.size(), Toast.LENGTH_SHORT
                    ).show();
                    if (attendanceList.isEmpty()) {
                        noAttendance.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noAttendance.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                    updateSummary(present, absent);
                }
                @Override
                public void onCancelled(
                        com.google.firebase.database.DatabaseError error) {
                    Toast.makeText(final_attendance_dashboard.this, "Firebase Error: " + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
    );
}
    private void updateSummary(int present, int absent) {
        int total = present + absent;
        double percentage = 0;
        if (total > 0) {
            percentage = (present * 100.0) / total;
        }
        totalPresent.setText(String.valueOf(present));
        totalAbsent.setText(String.valueOf(absent));
        attendancePercentage.setText(String.format("%.1f%%", percentage));
    }
}
