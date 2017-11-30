package com.example.android.hospitalmanagementsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.text.style.ForegroundColorSpan;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class frag_daily_stock extends Fragment implements DatePickerDialog.OnDateSetListener{

    int mDay, mMonth, mYear;
    Calendar c;
    String reportStr = "";
    String selectedDate = "";
    List<Stock> dateList, currentList;
    List<Stock> newList;
    ListView listViewDate;
    TextView mErrorEmptyList;
    TextView mErrorNoDate;
    Button mGenerateReport;
    Button mGenerateList;
    TextView mDateDisplay;
    ProgressBar mProgressbar;
    StockAdapter adapter;
    private FileWriter writer;
    int flag =0;

    public frag_daily_stock() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String formatDate(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));

        return sdf.format(date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_stock, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mErrorEmptyList = (TextView)view.findViewById(R.id.tvErrorEmptyList);
        mErrorEmptyList.setVisibility(View.GONE);

        mErrorNoDate = (TextView)view.findViewById(R.id.tvErrorNoDate);
        mErrorNoDate.setVisibility(View.GONE);

        mDateDisplay = (TextView) view.findViewById(R.id.tvDateDisplay);
        mDateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorNoDate.setVisibility(View.GONE);
                mErrorEmptyList.setVisibility(View.GONE);
                mGenerateList.setVisibility(View.VISIBLE);
                mGenerateReport.setVisibility(View.GONE);
                generateDatePickerDialog();
            }
        });

        dateList = new ArrayList<>();
        newList = new ArrayList<>();



        mGenerateReport = (Button) view.findViewById(R.id.btGenerateReport);
        mGenerateReport.setVisibility(View.GONE);
        mGenerateReport.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                mErrorEmptyList.setVisibility(View.GONE);
                mErrorNoDate.setVisibility(View.GONE);
                File fileToSend = createFileWithContent(reportStr);

                if(flag == 0) {
                    sendIntentToApp(fileToSend);
                }
            }
        });

        mGenerateList = (Button) view.findViewById(R.id.btGenerateList);
        mGenerateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mErrorEmptyList.setVisibility(View.GONE);
                mErrorNoDate.setVisibility(View.GONE);
                createReport();
            }
        });

        listViewDate = (ListView) view.findViewById(R.id.listDate);

        //generateDatePickerDialog();

        mProgressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressbar.setVisibility(View.GONE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //MenuItem item = menu.findItem(R.id.action_search);
        //item.setVisible(false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.YEAR, year);

        selectedDate = formatDate(year, monthOfYear, dayOfMonth);
        mDateDisplay.setText(formatDate(year, monthOfYear, dayOfMonth));

    }

    public void createReport() {

        flag =0;

        if(selectedDate.isEmpty()){
            mErrorNoDate.setVisibility(View.VISIBLE);
            return;
        }

        mProgressbar.setVisibility(View.VISIBLE);

        DatabaseReference databaseRoot = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseDate = databaseRoot.child("DailyStock");

        databaseDate.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dateList.clear();
                newList.clear();

                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    if(dateSnapshot.getKey().equals(selectedDate)) {
                        Log.i("check:","inside date");
                        for(DataSnapshot snapshot: dateSnapshot.getChildren()) {
                            Stock stock = snapshot.getValue(Stock.class);
                            dateList.add(stock);
                        }

                        Log.i("check: ", dateList.size() + "");
                        break;
                    }

                }
                adapter = new StockAdapter(getActivity(), dateList);

                listViewDate.setAdapter(adapter);
                mProgressbar.setVisibility(View.GONE);

                String date = mDateDisplay.getText().toString();
                currentList = dateList;

                if (currentList.size() <= 0) {
                    mErrorEmptyList.setVisibility(View.VISIBLE);
                } else {

                    SpannableStringBuilder strBuilder = new SpannableStringBuilder();
                    for (int j = 0; j < 18; j++) {
                        strBuilder.append(" ");
                    }
                    int start = strBuilder.length();
                    strBuilder.append(date);
                    strBuilder.setSpan(new ForegroundColorSpan(0xFFCC5500), start, strBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    strBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, strBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //StringBuilder strBuilder = new StringBuilder();

                    mProgressbar.setVisibility(View.GONE);

                    for (int j = 0; j < 18; j++) {
                        strBuilder.append(" ");
                    }
                    strBuilder.append("\n\n");
                    strBuilder.append("Name");
                    for (int j = 0; j < 18; j++) {
                        strBuilder.append(" ");
                    }
                    strBuilder.append("Quantity   Type\n\n");

                    for (int i = 0; i < currentList.size(); i++) {
                        Stock obj = currentList.get(i);

                        strBuilder.append("" + obj.getName() + "");
                        if (obj.getName().length() < 22) {
                            for (int j = 0; j < (22 - obj.getName().length()); j++) {
                                strBuilder.append(" ");
                            }
                        }
                        strBuilder.append("" + obj.getQuantity());

                        for (int j = 0; j < (11 - String.valueOf(obj.getQuantity()).length()); j++) {
                            strBuilder.append(" ");
                        }

                        strBuilder.append("" + obj.getType() + "\n\n");

                    }

                    reportStr = strBuilder.toString();
                    strBuilder.clear();
                    //Toast.makeText(DateStockActivity.this, reportStr + "", Toast.LENGTH_SHORT).show();
                    mGenerateList.setVisibility(View.GONE);
                    mGenerateReport.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendIntentToApp(File fileToSend) {
        if (fileToSend != null) {
            String reportDate = mDateDisplay.getText().toString();
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_SUBJECT, "Stock: " + reportDate);
            email.putExtra(Intent.EXTRA_TEXT, "PFA the stock report");
            email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileToSend.getAbsoluteFile()));
            email.setType("message/rfc822");

            startActivity(Intent.createChooser(email, "Send File via"));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createFileWithContent(String content) {
        if (TextUtils.isEmpty(content)) {
            content = "No logs";
            reportStr = "";
        }
        File file = null;
        String reportName = mDateDisplay.getText().toString();
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/stock-" + reportName + ".txt");

            writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            flag =1;
            Toast.makeText(getActivity(), "Unable create file \n Please allow storage permission in the Settings->Application Management", Toast.LENGTH_LONG).show();

        }
        return file;
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
}
