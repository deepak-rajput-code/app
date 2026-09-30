
package com.deepak.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
public class main_control extends AppCompatActivity {
    Button addDataButton;
    Button addFacultyButton;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);
        addDataButton = findViewById(R.id.button);
        addFacultyButton=findViewById(R.id.button2);
        databaseReference = FirebaseDatabase.getInstance().getReference("RECB_DATABASE");
                addFacultyButton.setOnClickListener(v->{
                Intent intent = new Intent( main_control.this, add_faculty.class );
                startActivity(intent);
              });
        addDataButton.setOnClickListener(v -> showAddSubjectDialog());
    }

    private void showAddSubjectDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);
        EditText branch = new EditText(this);
        branch.setHint("Branch (IT / CE / EE)");
        branch.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(branch);

        EditText year = new EditText(this);
        year.setHint("Year (1 / 2 / 3 / 4)");
        year.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(year);
        EditText semester = new EditText(this);
        semester.setHint("Semester (1 - 8)");
        semester.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(semester);
        EditText subjectCode = new EditText(this);
        subjectCode.setHint("Subject Code (Example: BCS401)");
        subjectCode.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(subjectCode);
        EditText subjectName = new EditText(this);
        subjectName.setHint("Subject Name");
        subjectName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(subjectName);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Subject")
                .setView(layout)
                .setPositiveButton("ADD", null)
                .setNegativeButton("CANCEL", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(view -> {
                String branchValue = branch.getText().toString().trim().toUpperCase();
                String yearValue = year.getText().toString().trim();
                String semesterValue = semester.getText().toString().trim();
                String subjectCodeValue = subjectCode.getText().toString().trim().toUpperCase();
                String subjectNameValue = subjectName.getText().toString().trim();
                if (branchValue.isEmpty() ||
                        yearValue.isEmpty() ||
                        semesterValue.isEmpty() ||
                        subjectCodeValue.isEmpty() ||
                        subjectNameValue.isEmpty()) {
                    Toast.makeText(main_control.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                int semesterNumber;
                try {semesterNumber = Integer.parseInt(semesterValue);
                } catch (NumberFormatException e) {
                    Toast.makeText(main_control.this, "Invalid semester", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (semesterNumber < 1 || semesterNumber > 8) {
                    Toast.makeText(main_control.this, "Semester must be between 1 and 8", Toast.LENGTH_SHORT).show();
                    return;
                }
                addSubjectToAllStudents(branchValue, yearValue, semesterValue, subjectCodeValue, subjectNameValue);
                dialog.dismiss();
            });
        });
        dialog.show();
    }
    private void addSubjectToAllStudents(String branch, String year, String semester, String subjectCode, String subjectName) {
        DatabaseReference studentsReference = databaseReference.child(branch).child(year);
        studentsReference.get().addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(
                                main_control.this,
                                "No students found for " + branch + " Year " + year, Toast.LENGTH_LONG).show();
                        return;
                    }
                    HashMap<String, Object> updates = new HashMap<>();
                    int studentCount = 0;
                    for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                        String username = studentSnapshot.getKey();
                        if (username == null) {
                            continue;
                        }
                        if (!studentSnapshot.hasChild("Roll_No")) {
                            continue;
                        }
                        updates.put(username + "/Semester", semester);
                        String subjectPath = username + "/Subjects/" + subjectCode;
                        updates.put(subjectPath + "/Subject_Code", subjectCode);
                        updates.put(subjectPath + "/Subject_Name", subjectName);
                        studentCount++;
                    }
                    if (studentCount == 0) {
                        Toast.makeText(main_control.this, "No students found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final int finalStudentCount = studentCount;
                    studentsReference.updateChildren(updates).addOnSuccessListener(unused -> {
                                Toast.makeText(main_control.this, subjectCode
                                                + " added to "
                                                + finalStudentCount
                                                + " students",
                                        Toast.LENGTH_LONG
                                ).show();

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(main_control.this, "Failed: " + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            main_control.this,
                            "Firebase Error: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}