package com.example.android.hospitalmanagementsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static android.R.attr.checkable;
import static android.R.attr.data;
import static android.R.attr.x;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UpdateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    DatabaseReference databaseMedicines;
    DatabaseReference databaseDate;
    DatabaseReference databaseDailyStock;
    Date selectedDate;

    int mDay, mMonth, mYear;
    Calendar c;

    String medicineName;
    int quantityBefore = 0;
    int quantity = 0;
    String id;
    int quantityChanged = 0;
    String type;

    int checkFlag;
    int count;

    private TextView mName;
    private TextView mQuantity;
    private TextView mSelectedDate;
    private TextView mFinalQuantity;

    EditText mSubtractQuantity;
    EditText mAddQuantity;

    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    Button mSubmitButton;
    Button mSubtract;
    Button mAdd;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Date formatDate(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        //sdf.setTimeZone(TimeZone.getTimeZone("IST"));

        //return sdf.format(date);
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseDailyStock = FirebaseDatabase.getInstance().getReference("DailyStock");

        Bundle bundle = getIntent().getExtras();
        medicineName = bundle.getString("name");
        quantity = bundle.getInt("quantity");
        id = bundle.getString("id");
        type = bundle.getString("type");
        //mapForDate = bundle.getString("mapForDate");

        quantityBefore = quantity;

        mFinalQuantity = (TextView) findViewById(R.id.tvFinalQuantity);

        mSelectedDate = (TextView) findViewById(R.id.tvSelectedDate);
        mSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateDatePickerDialog();
            }
        });

        mName = (TextView) findViewById(R.id.name);
        mName.setText(medicineName);

        mQuantity = (TextView) findViewById(R.id.quantity);
        mQuantity.setText(quantity + "");

        mSubtractQuantity = (EditText) findViewById(R.id.etSubtractQuantity);
        mAddQuantity = (EditText) findViewById(R.id.etAddQuantity);

        mSubmitButton = (Button) findViewById(R.id.btSubmitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                update();

            }
        });

        mSubtract = (Button) findViewById(R.id.btSubtract);
        mSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subtract();
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
            }
        });

        mAdd = (Button) findViewById(R.id.btAdd);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Add();
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
            }
        });

        generateDatePickerDialog();
    }

    public void Subtract() {
        String temp = mSubtractQuantity.getText().toString().trim();
        String updateDate = dateFormat.format(selectedDate);

        if (updateDate.isEmpty()) {
            Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
        } else {


            if (temp.isEmpty() || temp.length() == 0 || temp.equals("") || temp == null) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            } else {
                int x = Integer.parseInt(mSubtractQuantity.getText().toString());
                if (x > 0) {
                    if (x > quantity) {
                        Toast.makeText(this, "You don't have that much stock!", Toast.LENGTH_SHORT).show();
                    } else {

                        quantity = quantity - x;
                        mFinalQuantity.setText(quantity + "");
                        quantityChanged = quantityChanged - x;
                        mSubtractQuantity.setText("");
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    mAddQuantity.setText("");
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Add() {
        String temp = mAddQuantity.getText().toString().trim();
        String updateDate = dateFormat.format(selectedDate);


        if (updateDate.isEmpty()) {
            Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
        } else {

            if (temp.isEmpty() || temp.length() == 0 || temp.equals("") || temp == null) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            } else {
                int x = Integer.parseInt(mAddQuantity.getText().toString());
                if (x > 0) {

                    quantity = quantity + x;
                    if (quantityBefore == quantity) {
                        Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        mFinalQuantity.setText(quantity + "");
                        quantityChanged = quantityChanged + x;
                        mAddQuantity.setText("");
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    mAddQuantity.setText("");
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean update() {

        //DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = new Date();
        Log.i("current date: ", date.toString() + "");
        String todayDate = dateFormat.format(date);
        Log.i("today's date: " , todayDate + "");
        final String updateDate = dateFormat.format(selectedDate);
        Log.i("selected date: ", updateDate + "");
        long duration = date.getTime() - selectedDate.getTime();

        long diffInDays = (TimeUnit.MILLISECONDS.toHours(duration)) / 24;
        Log.i("Difference in days: ", diffInDays + "");
        if (diffInDays < 40 && diffInDays > 0 || updateDate.equals(todayDate)) {

            if (!updateDate.isEmpty()) {
                if (quantityBefore == quantity) {
                    Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                databaseMedicines = FirebaseDatabase.getInstance().getReference("Medicines").child(id);
                Medicine medicine = new Medicine(id, medicineName, quantity, type);
                databaseMedicines.setValue(medicine);
                databaseDate = FirebaseDatabase.getInstance().getReference("Datelist");
                String dateStockId = databaseDate.push().getKey();
                DateGen dateObj = new DateGen(dateStockId, dateFormat.format(selectedDate), id, quantityBefore, quantityChanged, medicineName, type);
                databaseDate.child(dateStockId).setValue(dateObj);

                updateDailyStock(dateFormat.format(selectedDate),quantityChanged,medicineName,type);

                Toast.makeText(this, "Medicine quantity updated!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateActivity.this, HomeActivity.class);
                startActivity(intent);
                selectedDate = null;
                quantityChanged = 0;
            } else {
                Toast.makeText(this, "Please fill the date", Toast.LENGTH_SHORT).show();
                quantityChanged = 0;
            }
        }


        return true;
}

    public void updateDailyStock(final String queryDate, final int queryQuantity, final String queryName, final String queryType){
        checkFlag = 0;
        count =0;
        Date date = new Date();
        Log.i("current date: ", date.toString() + "");
        final String todayDate = dateFormat.format(date);
        Log.i("method call:" , "In the method");
        databaseDailyStock.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Get complete json part inside date
                    Log.i("No of datasnapshot:" , dataSnapshot.getChildrenCount() + "");
                    Log.i("MyTag", "These are names: " + snapshot.getValue().toString() + "");
                    Log.i("key: " ,snapshot.getKey() + " ");
                    Log.i("query date:" , queryDate + " ");
                    long x = snapshot.getChildrenCount();
                    Log.i("count value:" , x + " ");
                    if(snapshot.getKey().equals(queryDate) || checkFlag == 1) {
                        count =1;
                        checkFlag = 1;

                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Log.i("MyTag 2", "These are names: " + snapshot1.getValue().toString() + "");
                            Stock st = snapshot1.getValue(Stock.class);
                            if(st.getName().equals(queryName) && st.getType().equals(queryType)) {
                                int temp = st.getQuantity() + queryQuantity;
                                snapshot1.getRef().child("quantity").setValue(temp);
                            }
                        }

                        if(snapshot.getKey().equals(todayDate)){
                            checkFlag = 0;
                            break;
                        }
                    }
                }

                if(count ==0){
                    createDailyStock(queryDate,queryQuantity,queryName,queryType);
                }
            }

            @Override
            public void onCancelled (DatabaseError databaseError){

            }
        });
    }

    public void createDailyStock(final String queryDate, final int queryQuantity, final String queryName, final String queryType){

    }


    public void onBackPressed() {
        Intent intent = new Intent(UpdateActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getTodayDate() {

        Calendar c;

        c = Calendar.getInstance();

        Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));

        return sdf.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void generateDatePickerDialog() {

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateActivity.this, UpdateActivity.this, mYear, mMonth, mDay);
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

        mSelectedDate.setText(dateFormat.format(selectedDate));
    }
}
