package com.deepak.app;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class register extends AppCompatActivity {
 EditText name;
 EditText roll_no;
 RadioGroup radio_group_branch;
 EditText username;
 EditText password,year;
 EditText developer_code;
 Button registration_btn;

 FirebaseDatabase database;
 DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

            name=findViewById(R.id.name);
          roll_no=findViewById(R.id.roll_no);
          radio_group_branch=findViewById(R.id.radio_group);
          year=findViewById(R.id.year);
          username=findViewById(R.id.username_reg);
           password=findViewById(R.id.password_reg);
            developer_code=findViewById(R.id.developer_code);
            registration_btn=findViewById(R.id.registration_btn);

           reference=FirebaseDatabase.getInstance().getReference("RECB_DATABASE");

            registration_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    register_user();
                }
            });
    }

    private void register_user() {

        String student_name = name.getText().toString().trim();
        String student_roll = roll_no.getText().toString().trim();
        String student_year = year.getText().toString().trim();
        String student_username = username.getText().toString().trim();
        String student_password = password.getText().toString().trim();
        String student_developer_code = developer_code.getText().toString().trim();

        int selected_branch = radio_group_branch.getCheckedRadioButtonId();


        if (student_name.isEmpty() ||
                student_roll.isEmpty() ||
                student_year.isEmpty() ||
                student_username.isEmpty() ||
                student_password.isEmpty() ||
                student_developer_code.isEmpty() ||
                selected_branch == -1) {

            Toast.makeText(this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        if (!student_developer_code.equals("4565471")) {

            Toast.makeText(this,
                    "Incorrect developer code",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        RadioButton selectBranch = findViewById(selected_branch);
        String branch = selectBranch.getText().toString().trim();


        Toast.makeText(this,
                "Saving to:\n" + branch + "/" +
                        student_year + "/" +
                        student_username,
                Toast.LENGTH_LONG).show();


        DatabaseReference student_reference =
                FirebaseDatabase.getInstance()
                        .getReference("RECB_DATABASE")
                        .child(branch)
                        .child(student_year)
                        .child(student_username);

        Map<String, Object> studentData = new HashMap<>();

        studentData.put("Name", student_name);
        studentData.put("Roll_No", student_roll);
        studentData.put("Username", student_username);
        studentData.put("Password", student_password);
        studentData.put("Year", student_year);

        student_reference.setValue(studentData)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(register.this,
                            "DATA SAVED SUCCESSFULLY",
                            Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(register.this,
                            "FIREBASE ERROR:\n" + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

}