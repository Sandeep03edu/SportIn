package com.example.sportin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportin.model.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private EditText signUpEmail, signUpPasswords, userName;
    private Spinner professionalSport, funSport;
    private Button signUpButton;
    private TextView signInButton;
    private String TAG="SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        signUpEmail = findViewById(R.id.signUp_email);
        signUpPasswords = findViewById(R.id.signUp_password);
        userName = findViewById(R.id.user_name);
        professionalSport = findViewById(R.id.professional_sport);
        funSport = findViewById(R.id.fun_sport);
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
                                        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                                        Toast.makeText(SignUpActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                                        UserDetail userDetail = new UserDetail(name);
                                        String uid=task.getResult().getUser().getUid();
                                        Log.d(TAG,"task succesful with"+uid);
                                        firebaseDatabase.getReference(uid).setValue(userDetail)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                                                    }
                                                });
                                    }
                                    else {
                                        Toast.makeText(SignUpActivity.this,"SignUp Unsuccessful, Please TRy Again!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
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