
package com.deepak.app;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button login_btn;
    TextView register;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.loginbtn);
        register = findViewById(R.id.registerbtn);

        reference = FirebaseDatabase.getInstance().getReference("RECB_DATABASE");
        login_btn.setOnClickListener(v -> loginUser());
        register.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, register.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String enteredUsername = username.getText().toString().trim();
        String enteredPassword = password.getText().toString().trim();
        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredUsername.equals("29") && enteredPassword.equals("29")) {
            Toast.makeText(MainActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, main_control.class);
            startActivity(intent);
            return;
        }

        loginFaculty(enteredUsername, enteredPassword);
    }


    private void loginFaculty(String enteredUsername, String enteredPassword) {
        DatabaseReference facultyReference = reference.child("Faculty").child(enteredUsername);
        facultyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {String firebaseUsername = snapshot.child("Username").getValue(String.class);
                            String firebasePassword = snapshot.child("Password").getValue(String.class);
                            String facultyName = snapshot.child("Name").getValue(String.class);
                            if (firebaseUsername != null && firebasePassword != null && firebaseUsername.equals(enteredUsername) && firebasePassword.equals(enteredPassword)) {
                                Toast.makeText(MainActivity.this, "Faculty Login Successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MainActivity.this, faculty_dashboard.class);

                                intent.putExtra("username", firebaseUsername);
                                intent.putExtra("facultyName", facultyName);
                                startActivity(intent);

                            } else {

                                Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            loginStudent(
                                    enteredUsername,
                                    enteredPassword
                            );
                        }
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                MainActivity.this,
                                "Database Error: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }



    private void loginStudent(
            String enteredUsername,
            String enteredPassword) {

        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {
                        boolean loginSuccess = false;
                        for (DataSnapshot branchSnapshot : snapshot.getChildren()) {
                            if (branchSnapshot
                                    .getKey()
                                    .equals("Faculty")) {
                                continue;
                            }
                            for (DataSnapshot yearSnapshot : branchSnapshot.getChildren()) {
                                for (DataSnapshot studentSnapshot : yearSnapshot.getChildren()) {
                                    String firebaseUsername = studentSnapshot.child("Username").getValue(String.class);
                                    String firebasePassword = studentSnapshot.child("Password").getValue(String.class);
                                    if (firebaseUsername != null &&
                                            firebasePassword != null &&
                                            firebaseUsername.equals(
                                                    enteredUsername) &&
                                            firebasePassword.equals(
                                                    enteredPassword)) {
                                        loginSuccess = true;
                                        String studentName = studentSnapshot.child("Name").getValue(String.class);
                                        String rollNo = studentSnapshot.child("Roll_No").getValue(String.class);
                                        String branch = branchSnapshot.getKey();
                                        String year = yearSnapshot.getKey();
                                        String semester = studentSnapshot.child("Semester").getValue(String.class);

                                        Toast.makeText(MainActivity.this, "Student Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, student_dashboard.class);
                                        intent.putExtra("username", firebaseUsername);
                                        intent.putExtra("name", studentName);
                                        intent.putExtra("roll_no", rollNo);
                                        intent.putExtra("branch", branch);
                                        intent.putExtra("year", year);
                                        intent.putExtra("semester", semester);
                                        startActivity(intent);
                                        return;
                                    }
                                }
                            }
                        }

                        if (!loginSuccess) {
                            Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
