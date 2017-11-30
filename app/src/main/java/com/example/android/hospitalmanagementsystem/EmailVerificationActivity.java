package com.example.android.hospitalmanagementsystem;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {

    private TextView mUserEmail;
    private Button mSendemail;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mUserEmail = (TextView)findViewById(R.id.tvUserEmail);
        mUserEmail.setText(currentUser.getEmail() + "");
        mSendemail = (Button)findViewById(R.id.btResendEmail);
        mSendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // NOTE: this Activity should get onpen only when the user is not signed in, otherwise
                    // the user will receive another verification email.
                    sendEmailVerification();
                } else {
                    // User is signed out

                }
                // ...
            }
        };
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailVerificationActivity.this, "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(EmailVerificationActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
