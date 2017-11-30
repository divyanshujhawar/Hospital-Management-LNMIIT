package com.example.android.hospitalmanagementsystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SignInActivity extends AppCompatActivity {

    EditText mPassword;
    EditText mEmail;
    Button mLogIn;
    TextView mSignUp;
    Button mGoogleSignin;
    int flag;
    public ProgressDialog dialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        flag = 0;

        mEmail = (EditText) findViewById(R.id.etEmail);
        mPassword = (EditText) findViewById(R.id.etPassword);
        mLogIn = (Button) findViewById(R.id.btLogIn);
        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(SignInActivity.this);
                dialog.setMessage("Please wait !!");
                dialog.show();
                signIn();
            }
        });

        mSignUp = (TextView) findViewById(R.id.tvGoToSignUp);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            //Toast.makeText(this, "Status " + currentUser.isEmailVerified(), Toast.LENGTH_SHORT).show();
            //currentUser.sendEmailVerification();
            if (currentUser.isEmailVerified()) {
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        }
    }

    private void signIn() {
        flag = 0;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (!validateForm()) {
            return;
        }
        assert currentUser != null;

        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();

        Log.i("Email", email + "\n");
        Log.i("Password", password + "\n");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User us = snapshot.getValue(User.class);
                    assert us != null;
                    //Log.i("email check: " , us.getEmail() + " ");
                    //Log.i("check : ", us.getEmail().equals(email) + " ");
                    //Log.i("Verified", currentUser.isEmailVerified() + "");
                    if (us.getEmail().equals(email)) {
                        //Toast.makeText(SignInActivity.this, currentUser.isEmailVerified() + "", Toast.LENGTH_SHORT).show();

                        logIn(email, password, us.getPermission());
                        flag = 1;
                        break;


                    }

                }

                if(flag == 0) {
                    dialog.dismiss();
                    Toast.makeText(SignInActivity.this, "Email id is not registered!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void logIn(String email, String password, final String permission) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (task.isSuccessful()) {
                            assert currentUser != null;
                            if (currentUser.isEmailVerified()) {
                                if(permission.equals("admin")) {
                                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                                }
                                else{
                                    Toast.makeText(SignInActivity.this, "Sorry You don't have the permission to enter!", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            } else {
                                startActivity(new Intent(SignInActivity.this, EmailVerificationActivity.class));
                                dialog.dismiss();
                            }
                        } else {

                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        if (!task.isSuccessful()) {

                        }
                        //hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
    }
}
