package com.example.android.hospitalmanagementsystem;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.string.no;
import static com.example.android.hospitalmanagementsystem.R.mipmap.ic_checkmark_green;
import static com.example.android.hospitalmanagementsystem.R.mipmap.ic_checkmark_grey;

public class NotificationActivity extends AppCompatActivity {

    ListView listViewNotification;

    List<Notification> notificationList;

    DatabaseReference databaseNotification;

    NotificationAdapter adapter;

    String checkId = "";

    TextView mCheckMark,mUpdateCheckMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mCheckMark = (TextView)findViewById(R.id.checkMark);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            checkId = bundle.getString("checkId");

        notificationList = new ArrayList<>();

        databaseNotification = FirebaseDatabase.getInstance().getReference("Appointments");

        listViewNotification = (ListView)findViewById(R.id.notificationList);
        listViewNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification currentNotification = adapter.getItem(position);

                assert currentNotification != null;
                String name = currentNotification.getName();
                String rollNo = currentNotification.getRollNo();
                String description = currentNotification.getDescription();
                String fromTime = currentNotification.getFromTime();
                String toTime = currentNotification.getToTime();
                String idNotification = currentNotification.getId();
                String email = currentNotification.getEmail();

                Intent intent = new Intent(NotificationActivity.this,AssignActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("rollNo",rollNo);
                intent.putExtra("description",description);
                intent.putExtra("fromTime",fromTime);
                intent.putExtra("toTime",toTime);
                intent.putExtra("idNotification",idNotification);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        mUpdateCheckMark = (TextView)findViewById(R.id.checkMark);
        databaseNotification.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!notificationList.isEmpty()) {
                    adapter = new NotificationAdapter(NotificationActivity.this, notificationList);
                    listViewNotification.setAdapter(adapter);
                }
                notificationList.clear();


                for(DataSnapshot medicineSnapshot : dataSnapshot.getChildren()){
                    Notification notification = medicineSnapshot.getValue(Notification.class);
                    /*if(mUpdateCheckMark != null && notification.getStatus()){
                        mUpdateCheckMark.setBackgroundResource(R.drawable.ic_checkmark_green);
                        Toast.makeText(NotificationActivity.this, "Color changed", Toast.LENGTH_SHORT).show();
                    }*/
                    notificationList.add(notification);
                }


                adapter = new NotificationAdapter(NotificationActivity.this,notificationList);

                listViewNotification.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
