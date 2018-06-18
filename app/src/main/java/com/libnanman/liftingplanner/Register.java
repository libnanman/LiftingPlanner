package com.libnanman.liftingplanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.lang.Object;

public class Register extends AppCompatActivity {
    private Button mRegister;
    private EditText mEmail, mPassword, mName, mWeight, mHeight, mAge;
    private RadioGroup mRadioGroup;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fireAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    Intent intent =  new Intent(Register.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mName = (EditText) findViewById(R.id.name);
        mHeight = (EditText) findViewById(R.id.height);
        mWeight = (EditText) findViewById(R.id.weight);
        mAge = (EditText) findViewById(R.id.age);



        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectId = mRadioGroup.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(radioButton.getText() == null){
                    return;
                }
                final Integer weight = Integer.parseInt(mWeight.getText().toString());
                final Integer height = Integer.parseInt(mHeight.getText().toString());
                final Integer age = Integer.parseInt(mAge.getText().toString());

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(Register.this, "sign_up_error", Toast.LENGTH_SHORT).show();
                        }else{
                            String Gender = radioButton.getText().toString();

                            User user = new User(name, age,height,weight, Gender);
                            writeUserData(user);
                        }
                    }
                });

            }
        });
    }

    protected void writeUserData(User user){
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().
                getReference().child("Users").child(userId);
        currentUserDb.setValue(user);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(fireAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireAuthStateListener);
    }
}