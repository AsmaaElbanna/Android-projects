package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidproject.navigation_drawer_activity.NavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    EditText emailTxt ,passwordTxt;
    Button signUpBtn;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        emailTxt = findViewById(R.id.email_signUp);
        passwordTxt = findViewById(R.id.password_signUp);
        signUpBtn =findViewById(R.id.SignUp_btn);
        myAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String emailString = emailTxt.getText().toString();
                String passwordString = passwordTxt.getText().toString();
                myAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "Registered successfuly", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(getApplicationContext(), NavigationActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(SignupActivity.this, "Registeration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }  //end onClick
        });
    }

}