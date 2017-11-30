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

public class StockAdapter extends ArrayAdapter<Stock>{

    private List<Stock> dateList;
    private Activity context;

    public StockAdapter(Activity context, List<Stock> dates) {
        super(context,0,dates);

        this.context = context;
        this.dateList = dates;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listDateView = inflater.inflate(R.layout.list_stock,null,true);


        //TextView mMedicineDate = (TextView)listDateView.findViewById(R.id.tvMedicineUpdateDate);
        TextView mName = (TextView)listDateView.findViewById(R.id.tvName);
        TextView mQuantity = (TextView)listDateView.findViewById(R.id.tvQuantity);
        TextView mType = (TextView)listDateView.findViewById(R.id.tvType);

        Stock currentDate = getItem(position);

        assert currentDate != null;
        //mMedicineDate.setText(currentDate.getDateForm());
        mName.setText(currentDate.getName());
        mQuantity.setText(String.valueOf(currentDate.getQuantity()));
        mType.setText(String.valueOf(currentDate.getType()));

        return listDateView;
    }
}
