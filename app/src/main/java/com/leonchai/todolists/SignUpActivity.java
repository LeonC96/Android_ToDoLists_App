package com.leonchai.todolists;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText nameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private ProgressBar progressBar;
    private Button signUpBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        signUpBtn = (Button) findViewById(R.id.signUpButton);
        cancelBtn = (Button) findViewById(R.id.cancelButton);
        nameView = (EditText) findViewById(R.id.nameEditText);
        emailView = (EditText)findViewById(R.id.emailEditText);
        passwordView = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordView = (EditText) findViewById(R.id.confirmPasswordeditText);
        progressBar = (ProgressBar) findViewById(R.id.SignUpProgressBar);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String name = nameView.getText().toString().trim();
                String email = emailView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();
                String confirmPassword = confirmPasswordView.getText().toString().trim();

                if(TextUtils.isEmpty(name) || !name.contains(" ")){
                    Toast.makeText(getApplicationContext(), "Enter First Name and Last Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email) || !email.contains("@")){
                    Toast.makeText(getApplicationContext(), "Enter an Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Password too short, minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!confirmPassword.equals(password)){
                    Toast.makeText(getApplicationContext(), "Passwords does not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.putExtra(MainActivity.EXTRA_USERNAME, name);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
