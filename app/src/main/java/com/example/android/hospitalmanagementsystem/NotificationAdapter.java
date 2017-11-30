package com.example.android.hospitalmanagementsystem;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Divyanshu on 04-10-2017.
 */

public class NotificationAdapter extends ArrayAdapter<Notification>{
    private List<Notification> notList;
    private Activity context;

    public NotificationAdapter(Activity context, List<Notification> nots){
        super(context,0,nots);

        this.context = context;
        this.notList = nots;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listNotificationView = inflater.inflate(R.layout.list_notification,null,true);


        TextView mRollNo = (TextView)listNotificationView.findViewById(R.id.detail);

        Notification currentNotification = getItem(position);

        assert currentNotification != null;
        mRollNo.setText(currentNotification.getRollNo());

        return listNotificationView;
    }
}
