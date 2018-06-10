package com.libnanman.liftingplanner;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
private EditText mEmailCreateField;
private EditText mPswCreateField;
private EditText mNameField;
private Button mCreateBtn;
private FirebaseAuth mAuth;
private DatabaseReference mDatabase;
private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mEmailCreateField = (EditText) findViewById(R.id.emailCreateField);
        mPswCreateField = (EditText) findViewById(R.id.pswCreateField);
        mCreateBtn = (Button) findViewById(R.id.createBtn);
        mNameField = (EditText) findViewById(R.id.nameField);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        final String email = mEmailCreateField.getText().toString();
        String password = mPswCreateField.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgress.setMessage("Signing up...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        FirebaseAuth.getInstance()
                        DatabaseReference myRef = database.getReference("message");

                        myRef.setValue("Hello, World!");


                    }
                }
            });




        }
    }
}
