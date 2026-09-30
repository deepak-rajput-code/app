
package com.deepak.app;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
public class add_faculty extends AppCompatActivity {
    EditText etFacultyName;
    EditText etUsername;
    EditText etPassword;
    Button btnSaveFaculty;
    DatabaseReference facultyReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);
        etFacultyName = findViewById(R.id.etFacultyName);
        etUsername = findViewById(R.id.etFacultyUsername);
        etPassword = findViewById(R.id.etFacultyPassword);
        btnSaveFaculty = findViewById(R.id.btnSaveFaculty);
        facultyReference = FirebaseDatabase.getInstance().getReference("RECB_DATABASE").child("Faculty");
        btnSaveFaculty.setOnClickListener(v -> saveFaculty());
    }

    private void saveFaculty() {
        String facultyName = etFacultyName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(facultyName)) {
            etFacultyName.setError("Enter faculty name");
            etFacultyName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Enter username");
            etUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            etPassword.requestFocus();
            return;
        }
        username = username.replace(" ", "").toLowerCase();
        String finalUsername = username;
        facultyReference.child(finalUsername).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(add_faculty.this, "Faculty username already exists", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Map<String, Object> facultyData = new HashMap<>();
                                facultyData.put("Name", facultyName);
                                facultyData.put("Username", finalUsername);
                                facultyData.put("Password", password);

                                facultyReference.child(finalUsername).setValue(facultyData).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(add_faculty.this, "Faculty added successfully", Toast.LENGTH_SHORT).show();
                                                etFacultyName.setText("");
                                                etUsername.setText("");
                                                etPassword.setText("");

                                            } else {

                                                Toast.makeText(add_faculty.this, "Failed to add faculty", Toast.LENGTH_SHORT).show();
                                            }

                                        });
                            }
                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {

                                Toast.makeText(
                                        add_faculty.this,
                                        "Firebase Error: "
                                                + error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}
