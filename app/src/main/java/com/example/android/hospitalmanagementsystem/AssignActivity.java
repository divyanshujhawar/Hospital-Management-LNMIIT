package com.example.android.hospitalmanagementsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AssignActivity extends AppCompatActivity {
    private String name,rollNo,description,fromTime,toTime,idNotification,email;

    DatabaseReference databaseAppointment;

    TextView mName, mRollNo, mDescription, mFromTime, mToTime;

    EditText mReply;

    Button mFix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        rollNo = bundle.getString("rollNo");
        description = bundle.getString("description");
        fromTime = bundle.getString("fromTime");
        toTime = bundle.getString("toTime");
        idNotification = bundle.getString("idNotification");
        email = bundle.getString("email");

        mReply = (EditText)findViewById(R.id.etReply);

        mName = (TextView)findViewById(R.id.tvName);
        mName.setText(name);
        mRollNo = (TextView)findViewById(R.id.tvRollNo);
        mRollNo.setText(rollNo);
        mDescription = (TextView)findViewById(R.id.tvDescription);
        mDescription.setText(description);
        mFromTime = (TextView)findViewById(R.id.tvFromTime);
        mFromTime.setText(fromTime);
        mToTime = (TextView)findViewById(R.id.tvToTime);
        mToTime.setText(toTime);

        mFix = (Button)findViewById(R.id.btFixApp);
        mFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    public void update(){
        String comment = mReply.getText().toString();
        databaseAppointment = FirebaseDatabase.getInstance().getReference("Appointments").child(idNotification);
        Notification nots = new Notification(idNotification,name,rollNo,description,fromTime,toTime,comment,true,email);
        databaseAppointment.setValue(nots);

        Toast.makeText(this, "Appointment set!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AssignActivity.this,NotificationActivity.class);
        intent.putExtra("checkId",idNotification);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
