package com.example.authregapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    protected EditText eMail, pWrd;
    protected Button logIn;
    protected TextView create, reset;
    protected ProgressBar progressBar;
    protected FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eMail = findViewById(R.id.emailTxt);
        pWrd = findViewById(R.id.pwTxt);
        logIn = findViewById(R.id.logBtn);
        create = findViewById(R.id.createTxt);
        reset = findViewById(R.id.resetTxt);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        logIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String Email = eMail.getText().toString().trim();
                String Password = pWrd.getText().toString().trim();

                if(TextUtils.isEmpty(Email)){
                    eMail.setError("Email required");
                    return;
                }
                if(TextUtils.isEmpty(Password)){
                    pWrd.setError("Password required");
                    return;
                }
                if(Password.length() < 6){
                    pWrd.setError("Password must be greater than 6 characters");
                    return;
                }

                progressBar.setVisibility(view.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getBaseContext(), MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(Login.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(), Register.class));
            }
        });

        //reset password dialog; need to implement a way to prevent the user from hitting positive without email
        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder pwrdReset = new AlertDialog.Builder(view.getContext());
                pwrdReset.setTitle("Reset Password?");
                pwrdReset.setMessage("Enter email to receive reset link");
                pwrdReset.setView(resetMail);

                pwrdReset.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //extract email and send reset link
                        String mail = resetMail.getText().toString().trim();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Reset link sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                pwrdReset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close dialog
                    }
                });

                pwrdReset.create().show();
            }
        });
    }
}
