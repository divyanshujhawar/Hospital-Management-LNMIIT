package com.example.android.hospitalmanagementsystem;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Divyanshu on 19-08-2017.
 */

public class MedicineAdapter extends ArrayAdapter<Medicine> {

    private Context context;
    private Filter medicineFilter;
    private List<Medicine> medicineList;

    public MedicineAdapter(Context context, List<Medicine> medicines) {
        super(context,0,medicines);
        this.context = context;
        this.medicineList = medicines;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listItemView = inflater.inflate(R.layout.list_item,null,true);

        if (listItemView == null) {
            listItemView = inflater.inflate(R.layout.list_item,null,true);
        }

        TextView medicineNameTV = (TextView)listItemView.findViewById(R.id.medicineName);
        TextView medicineQuantityTV = (TextView)listItemView.findViewById(R.id.medicineQuantity);
        TextView medicineType = (TextView)listItemView.findViewById(R.id.medicineType);

        Medicine currentMedicine = getItem(position);

        GradientDrawable quantityCircle = (GradientDrawable) medicineQuantityTV.getBackground();


        assert currentMedicine != null;
        int quantityColor = getQuantityColor(currentMedicine.getMedicineQuantity());

        quantityCircle.setColor(quantityColor);

        assert currentMedicine != null;
        medicineNameTV.setText(currentMedicine.getMedicineName());
        medicineQuantityTV.setText(String.valueOf(currentMedicine.getMedicineQuantity()));
        medicineType.setText(currentMedicine.getType());

        return listItemView;
    }

    public int getQuantityColor(int quantity){
        int quanColor = 0;
        if(quantity <= 30){
            quanColor = ContextCompat.getColor(getContext(), R.color.quantity1);
        }
        else if(quantity > 30 && quantity < 100){
            quanColor = ContextCompat.getColor(getContext(), R.color.quantity2);
        }
        else if(quantity >= 100){
            quanColor = ContextCompat.getColor(getContext(), R.color.quantity3);
        }
        else{
            quanColor = ContextCompat.getColor(getContext(), R.color.quantity4);
        }

        return quanColor;
    }

    @Override
    public void sort(@NonNull Comparator<? super Medicine> comparator) {
        super.sort(comparator);
    }
}
