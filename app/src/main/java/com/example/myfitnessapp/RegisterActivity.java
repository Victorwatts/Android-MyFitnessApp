package com.example.myfitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    boolean z;
    EditText Email, UserName, Password, ConfirmPassword, Fname, Sname;
    Button register;
    Button GoLogin;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userID;
    FirebaseUser fuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Email = findViewById(R.id.edtEmailAddress);
        UserName = findViewById(R.id.edtUserName);
        Fname = findViewById(R.id.edtFirstName);
        Sname = findViewById(R.id.edtSurName);
        Password = findViewById(R.id.edtPassword);
        ConfirmPassword = findViewById(R.id.edtConfirmPassword);
        register = findViewById(R.id.btnSignUp);
        GoLogin = findViewById(R.id.btnGoToLogin);
        progressBar = findViewById(R.id.pbbar);


        GoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        fuser = mAuth.getCurrentUser();

        if(mAuth.getCurrentUser() != null)
        {
            if (!fuser.isEmailVerified())
            {


            }
            else
            {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }

        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataEntered();

            }
        });


    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    void checkDataEntered()
    {

        int length = Password.length();
        String Pw = Password.getText().toString();
        String cPw = ConfirmPassword.getText().toString();

        if (isEmail(Email) == false) {
            Email.setError("Enter valid email!");

        }else

        if (isEmpty(UserName)) {
            Toast t = Toast.makeText(this, "You must enter a username to register!", Toast.LENGTH_SHORT);
            t.show();
        }else

        if (isEmpty(Fname)) {
            Toast t = Toast.makeText(this, "You must enter a First name to register!", Toast.LENGTH_SHORT);
            t.show();
        }else

        if (isEmpty(Sname)) {
            Toast t = Toast.makeText(this, "You must enter a Surname to register!", Toast.LENGTH_SHORT);
            t.show();
        }else

        if (isEmpty(Password)) {
            Toast t = Toast.makeText(this, "You must enter a password to register!", Toast.LENGTH_SHORT);
            t.show();
        }else
        if (isEmpty(ConfirmPassword)) {
            Toast t = Toast.makeText(this, "You must confirm your password to register!", Toast.LENGTH_SHORT);
            t.show();
        }else

        if (length > 16)
        {
            Toast t = Toast.makeText(this, "The password can't be over 16 characters long!", Toast.LENGTH_SHORT);
            t.show();
        }else
        if (length < 8)
        {
            Toast t = Toast.makeText(this, "The password can't be less than 8 characters long!", Toast.LENGTH_SHORT);
            t.show();
        }else

            if (!Pw.equals(cPw))
            {
                Toast t = Toast.makeText(this,  "The passwords do not match!", Toast.LENGTH_SHORT);
                t.show();
            }else
            {
                WriteData();
            }

    }

     void WriteData() {


        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(Email.getText().toString(), Password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                            auth.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this,  "Verification Email Has Been Sent", Toast.LENGTH_LONG);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email Not Sent" + e.getMessage());

                                }
                            });
                            Toast.makeText(RegisterActivity.this, "Verification Email Has Been Sent.", Toast.LENGTH_LONG).show();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String, Object> user =  new HashMap<>();
                            user.put("Username",UserName.getText().toString());
                            user.put("Firstname",Fname.getText().toString());
                            user.put("Surname",Sname.getText().toString());
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User profile and data saved" + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: User Not Saved" + e.getMessage());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                });


    }




}
