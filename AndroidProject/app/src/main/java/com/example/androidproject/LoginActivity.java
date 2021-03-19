package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.navigation_drawer_activity.NavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailTxt,passwordTxt;
    Button loginBtn;
    TextView createAccount;
    private  FirebaseAuth myAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailTxt =findViewById(R.id.email_txt);
        passwordTxt =findViewById(R.id.password_txt);
        loginBtn = findViewById(R.id.login_btn);
        createAccount =findViewById(R.id.create_account);
        myAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailString = emailTxt.getText().toString();
                String passwordString = passwordTxt.getText().toString();
                myAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = myAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login successfuly", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(LoginActivity.this, NavigationActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } //end onClick
                });
    createAccount.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(getApplicationContext(),SignupActivity.class);
            startActivity(intent);
        }
    });


}

}








