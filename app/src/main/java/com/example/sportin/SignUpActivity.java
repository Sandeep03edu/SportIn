package com.example.sportin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportin.model.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private EditText signUpEmail, signUpPasswords, userName;
    private Spinner professionalSportSpinner, funSportSpinner;
    private Button signUpButton;
    private TextView signInButton;
    private String TAG="SignUpActivity";
    private ArrayList<String> profList, funList;

    @Override
    protected void onStart() {
        super.onStart();
        setSpinnerValues();
    }

    private void setSpinnerValues() {
        profList = new ArrayList<>();
        profList.add(0, "Select");
        profList.add("Cricket");
        profList.add("Football");
        profList.add("Hockey");

        funList = new ArrayList<>();
        funList.add(0, "Select");
        funList.add("Chess");
        funList.add("Kabaddi");
        funList.add("Golf");

        ArrayAdapter<String> profArrayAdapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_spinner_item, profList);
        profArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        professionalSportSpinner.setAdapter(profArrayAdapter);

        ArrayAdapter<String> funAdapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_spinner_item, funList);
        funAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        funSportSpinner.setAdapter(funAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        signUpEmail = findViewById(R.id.signUp_email);
        signUpPasswords = findViewById(R.id.signUp_password);
        userName = findViewById(R.id.user_name);
        professionalSportSpinner = findViewById(R.id.professional_sport);
        funSportSpinner = findViewById(R.id.fun_sport);
        signUpButton = findViewById(R.id.signUp);
        signInButton = findViewById(R.id.signIn);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName.getText().toString();
                String email = signUpEmail.getText().toString();
                String password = signUpPasswords.getText().toString();
//                String professionSport = professionalSport.getSelectedItem().toString();
//                String fun_Sport = funSport.getSelectedItem().toString();

                if (name.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "user name is empty", Toast.LENGTH_SHORT);
                } else if (email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "email is empty", Toast.LENGTH_SHORT);
                }
                else if (password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Password is empty", Toast.LENGTH_SHORT);
                }
                else if (!(name.isEmpty() && email.isEmpty() && password.isEmpty())){
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
//                                        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                                        Toast.makeText(SignUpActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
//                                        UserDetail userDetail = new UserDetail(name);
                                        String uid=task.getResult().getUser().getUid();
                                        Log.d(TAG,"task succesful with"+uid);
//                                        firebaseDatabase.getReference(uid).setValue(userDetail)
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void Void) {
//                                                        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
//                                                    }
//                                                });
                                        UserDetail userDetail = new UserDetail(name, professionalSportSpinner.getSelectedItem().toString(), funSportSpinner.getSelectedItem().toString());
                                        firebaseFirestore = FirebaseFirestore.getInstance();
                                        DocumentReference userRef = firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        userRef.set(userDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignUpActivity.this, "Registration Succesfull", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    }
                                    else {
                                        Toast.makeText(SignUpActivity.this,"SignUp Unsuccessful, Please Try Again!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            }
        });

    }
}