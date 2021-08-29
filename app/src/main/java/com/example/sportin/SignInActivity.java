package com.example.sportin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private EditText signInEMail, signInPassword;
    private Button login;
    private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        signInEMail = findViewById(R.id.signIn_email);
        signInPassword = findViewById(R.id.signIn_password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        if (mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signInEMail.getText().toString();
                String password = signInPassword.getText().toString();

                if (email.isEmpty()) {
                    signInEMail.setError("Please Provide Email");
                    signInEMail.requestFocus();
                } else if (password.isEmpty()) {
                    signInPassword.setError("Please Provide Password");
                    signInPassword.requestFocus();
                } else if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Fields are Empty", Toast.LENGTH_SHORT);
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.d("aassd",task.toString());
                                        Toast.makeText(SignInActivity.this, "Login Error ,Please Login In", Toast.LENGTH_LONG).show();
                                    } else {
                                        Intent mainActivityIntent = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(mainActivityIntent);
                                    }
                                }
                            });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}