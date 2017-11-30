package com.example.android.hospitalmanagementsystem;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Divyanshu on 22-08-2017.
 */

public class DateAdapter extends ArrayAdapter<DateGen>{

    private List<DateGen> dateList;
    private Activity context;

    public DateAdapter(Activity context, List<DateGen> dates) {
        super(context,0,dates);

        this.context = context;
        this.dateList = dates;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listDateView = inflater.inflate(R.layout.list_date,null,true);


        //TextView mMedicineDate = (TextView)listDateView.findViewById(R.id.tvMedicineUpdateDate);
        TextView mMedicineName = (TextView)listDateView.findViewById(R.id.tvMedicineName);
        TextView mMedicineQuantityBeforeChange = (TextView)listDateView.findViewById(R.id.tvQuantityBefore);
        TextView mMedicineQuantityChanged = (TextView)listDateView.findViewById(R.id.tvQuantityChanged);

        DateGen currentDate = getItem(position);

        assert currentDate != null;
        //mMedicineDate.setText(currentDate.getDateForm());
        mMedicineName.setText(currentDate.getMedicineNameOfDate());
        mMedicineQuantityBeforeChange.setText(String.valueOf(currentDate.getQuantityBefore()));
        mMedicineQuantityChanged.setText(String.valueOf(currentDate.getQuantityChanged()));

        return listDateView;
    }
}
