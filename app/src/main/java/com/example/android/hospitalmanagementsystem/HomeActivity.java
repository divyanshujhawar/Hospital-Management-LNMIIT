package com.example.android.hospitalmanagementsystem;

import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    Calendar c;

    String reportStr = "";

    List<Medicine> medicineList;

    public static SearchView searchView;

    int checkFlag = 0;

    boolean openF2 = false;
    int flag = 0;
    String codeResult = "";
    int check =0;

    private FileWriter writer;

    int tabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Log.d("myapp1", Log.getStackTraceString(new Exception()));

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        c = Calendar.getInstance();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        medicineList = new ArrayList<>();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                if(tabPosition != 0){
                    //searchView.setVisibility(View.GONE);
                }
                else{
                    //searchView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("openF2")) {
            openF2 = extras.getBoolean("openF2");
            flag = extras.getInt("flag");
            codeResult = extras.getString("codeResult");

            if (openF2)

            {
                Bundle bundle = new Bundle();
                bundle.putString("codeResult",codeResult);
                frag_new_medicine fnm = new frag_new_medicine();
                fnm.setArguments(bundle);


                TabLayout.Tab tab = tabLayout.getTabAt(1);
                tab.select();
            }


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.menu_home, menu);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        */
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about: {
                break;
            }

            case R.id.userAuth: {
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                break;
            }
            /*
            case R.id.stockReport: {
                if(checkFlag == 0) {
                    generateStockReport();
                }

                break;
            }
             */
        }

        return true;
    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createFile(){



        Log.i("Size of list", medicineList.size() + "");
        if(medicineList.size() != 0) {
            String currentDate = DateFormat.getDateInstance().format(new Date());
            Log.i("currentDate :", currentDate);

            SpannableStringBuilder strBuilder = new SpannableStringBuilder();
            for (int j = 0; j < 12; j++) {
                strBuilder.append(" ");
            }
            strBuilder.append(currentDate + "\n\n");

            strBuilder.append("Name");
            for (int j = 0; j < 22; j++) {
                strBuilder.append(" ");
            }
            strBuilder.append("Quantity\n\n");

            for(int i=0;i <medicineList.size();i++){
                Medicine med = medicineList.get(i);

                strBuilder.append(med.getMedicineName());
                if (med.getMedicineName().length() < 22) {
                    for (int j = 0; j < (27 - med.getMedicineName().length()); j++) {
                        strBuilder.append(" ");
                    }
                }
                strBuilder.append(med.getMedicineQuantity() + "\n");
                strBuilder.append("(" + med.getType() + ")");
                strBuilder.append("\n\n");
            }

            reportStr = strBuilder.toString();
            strBuilder.clear();

            File fileToSend = createFileWithContent(reportStr);
            if(check == 0) {
                sendIntentToApp(fileToSend);
            }

        }
        else{
            Toast.makeText(this, "Stock is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void generateStockReport(){
        Log.i("Inside generate stock"," check");
        check =0;
        checkFlag = 1;

        DatabaseReference databaseStock = FirebaseDatabase.getInstance().getReference("Medicines");
        databaseStock.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                medicineList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Medicine med = snapshot.getValue(Medicine.class);
                    medicineList.add(med);
                }
                createFile();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    */

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    frag_stock f1 = new frag_stock();
                    return f1;
                case 1:
                    frag_new_medicine f2 = new frag_new_medicine();
                    return f2;
                case 2:
                    frag_report f3 = new frag_report();
                    return f3;
                case 3:
                    frag_daily_stock f4 = new frag_daily_stock();
                    return f4;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Stock";
                case 1:
                    return "New medicine";
                case 2:
                    return "Report";
                case 3:
                    return "Stock Report";
            }
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
       // Intent intent = new Intent(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_HOME);
        //startActivity(intent);
        finish();
    }

    public String getResult(){
        return codeResult;
    }

    public SearchView getSearchView(){
        return searchView;
    }

    private void sendIntentToApp(File fileToSend) {
        if (fileToSend != null) {
           //String reportDate = mDateDisplay.getText().toString();
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_SUBJECT, "Stock List");
            email.putExtra(Intent.EXTRA_TEXT, "PFA the report");
            email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileToSend.getAbsoluteFile()));
            email.setType("message/rfc822");
            //finish();
            startActivity(Intent.createChooser(email, "Send File via"));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createFileWithContent(String content) {
        if (TextUtils.isEmpty(content)) {
            content = "No logs";
            //reportStr = "";
        }
        File file = null;
        //String reportName = mDateDisplay.getText().toString();
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/stock.txt");

            writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            check =1;
            Toast.makeText(this, "Unable create file \n Please allow storage permission in the Settings->Application Management", Toast.LENGTH_LONG).show();

        }
        return file;
    }

}
