package com.example.android.hospitalmanagementsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Divyanshu on 16-10-2017.
 */

public class frag_new_medicine extends Fragment implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener{

    EditText mName;
    EditText mQuantity;
    Button mSubmit;
    Button mReader;

    Spinner typeSpinner;

    int mDay,mMonth,mYear;

    Calendar c;

    private TextView mSelectedDate;
    TextView mErrorEmptyField;
    TextView mErrorlargeQuantity;

    String selectedDate = "";
    String currentDate;
    String codeResult;
    String medicineType;
    public static final String shpref = "shpref";

    DatabaseReference databaseMedicines;
    DatabaseReference databaseDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_medicine, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeSpinner = (Spinner)view.findViewById(R.id.sMedicineType);

        databaseMedicines = FirebaseDatabase.getInstance().getReference("Medicines");
        databaseDate =FirebaseDatabase.getInstance().getReference("Datelist");

        mSelectedDate = (TextView)view.findViewById(R.id.tvSelectedDateForNew);
        mSelectedDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mErrorEmptyField.setVisibility(View.INVISIBLE);
                mErrorEmptyField.setVisibility(View.INVISIBLE);
                generateDatePickerDialog();
            }
        });

        mErrorEmptyField = (TextView)view.findViewById(R.id.tvErrorEmptyField);
        mErrorEmptyField.setVisibility(View.INVISIBLE);

        mErrorlargeQuantity = (TextView)view.findViewById(R.id.tvErrorLargeQuantity);
        mErrorlargeQuantity.setVisibility(View.INVISIBLE);

        mName = (EditText)view.findViewById(R.id.etMedicineName);
        if(codeResult != null && !codeResult.isEmpty()){
            mName.setText(codeResult);
        }
        mQuantity = (EditText)view.findViewById(R.id.etMedicineQuantity);
        mSubmit = (Button)view.findViewById(R.id.btSubmit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                addMedicine();
                mErrorEmptyField.setVisibility(View.INVISIBLE);
            }
        });

        mReader = (Button) view.findViewById(R.id.btReader);
        mReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mErrorlargeQuantity.setVisibility(View.INVISIBLE);
                mErrorEmptyField.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getActivity(),QRBarCodeActivity.class);
                startActivity(intent);
            }
        });

        // Spinner element
        Spinner spinner = (Spinner) view.findViewById(R.id.sMedicineType);

        // Spinner click listener
        //spinner.setOnItemSelectedListener(getActivity());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mErrorEmptyField.setVisibility(View.INVISIBLE);
                mErrorlargeQuantity.setVisibility(View.INVISIBLE);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(188,170,164));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Capsule");
        categories.add("Tablet");
        categories.add("Ointment");
        categories.add("Syrup");
        categories.add("Injection");
        categories.add("Drop");
        categories.add("Others");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        mSelectedDate.setText(selectedDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        mName.setText(((HomeActivity) getActivity()).getResult());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        medicineType = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //MenuItem item = menu.findItem(R.id.action_search);
        //item.setVisible(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addMedicine(){
        String name = mName.getText().toString().trim();
        String value = mQuantity.getText().toString();

        String typeMedicine = typeSpinner.getSelectedItem().toString();

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        currentDate = formatDate(mYear,mMonth,mDay);
        if (mSelectedDate.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please select a date", Toast.LENGTH_SHORT).show();
        }
        else {

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value) ) {

                int quantity = Integer.parseInt(value);
                if(quantity > 99999){
                    mErrorlargeQuantity.setVisibility(View.VISIBLE);
                }
                else {
                    String id = databaseMedicines.push().getKey();
                    Medicine medicine = new Medicine(id, name, quantity, typeMedicine);
                    databaseMedicines.child(id).setValue(medicine);

                    String dateStockId = databaseDate.push().getKey();
                    DateGen dateObj = new DateGen(dateStockId, currentDate, id, 0, quantity, name, typeMedicine);
                    databaseDate.child(dateStockId).setValue(dateObj);

                    Toast.makeText(getActivity(), "Medicine added", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            } else {
                mErrorEmptyField.setVisibility(View.VISIBLE);
            }
            selectedDate = "";
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String formatDate(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        return sdf.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void generateDatePickerDialog() {

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
        datePickerDialog.show();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.YEAR, year);

        selectedDate = formatDate(year, monthOfYear, dayOfMonth);

        mSelectedDate.setText(selectedDate);

        SharedPreferences.Editor editor = getActivity().getSharedPreferences(shpref, MODE_PRIVATE).edit();
        editor.putString("selectedDate", selectedDate);
        editor.apply();

    }


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getActivity().getSharedPreferences(shpref, MODE_PRIVATE);
        String selectedDate = prefs.getString("selectedDate", "No date found");
        mSelectedDate.setText(selectedDate);
    }
}
