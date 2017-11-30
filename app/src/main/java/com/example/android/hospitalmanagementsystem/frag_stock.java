package com.example.android.hospitalmanagementsystem;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Divyanshu on 16-10-2017.
 */

public class frag_stock extends Fragment{

    DatabaseReference databaseRoot;
    DatabaseReference databaseMedicines;

    ProgressBar mProgressbarMed;

    Context c = null;

    ListView listViewMedicine;

    List<Medicine> medicineList,stockList;

    MedicineAdapter adapter;

    private FirebaseAuth mAuth;

    public HashMap<String , Integer> mapForDate = new HashMap<String , Integer>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c= context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mProgressbarMed = (ProgressBar) view.findViewById(R.id.progressBarMed);
        //mProgressbarMed.setVisibility(View.VISIBLE);
        Log.d("Start", mProgressbarMed.getVisibility() + " ");

        medicineList = new ArrayList<>();
        stockList = new ArrayList<>();




        databaseRoot = FirebaseDatabase.getInstance().getReference();
        databaseMedicines = databaseRoot.child("Medicines");

        listViewMedicine = (ListView)view.findViewById(R.id.list);
        listViewMedicine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Medicine currentMedicine = adapter.getItem(position);

                assert currentMedicine != null;
                String name = currentMedicine.getMedicineName();
                int quantity = currentMedicine.getMedicineQuantity();
                String id = currentMedicine.getId();
                String type = currentMedicine.getType();

                Intent intent = new Intent(getActivity(),UpdateActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("mapForDate",mapForDate);
                intent.putExtra("quantity",quantity);
                intent.putExtra("id",id);
                intent.putExtra("type",type);
                startActivity(intent);
            }
        });

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void databaseUpdate(){


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DailyStock");


        String currentDate = DateFormat.getDateInstance().format(new Date());
        String date = "06 Oct 2017";

        Log.i("List size:" , stockList.size() + "");
        for(int i=0;i<stockList.size();i++){

            String id = databaseReference.push().getKey();
            Stock stock = new Stock(stockList.get(i).getMedicineName(),stockList.get(i).getMedicineQuantity(),
                    stockList.get(i).getType(),id);
            databaseReference.child(date).child(id).setValue(stock);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mProgressbarMed.setVisibility(View.VISIBLE);
        //Log.d("Start2", mProgressbarMed.getVisibility() + " ");
        if (getActivity() != null) {


            databaseMedicines.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!medicineList.isEmpty()) {
                        adapter = new MedicineAdapter(c, medicineList);
                        listViewMedicine.setAdapter(adapter);
                    }
                    medicineList.clear();


                    for (DataSnapshot medicineSnapshot : dataSnapshot.getChildren()) {
                        Medicine medicine = medicineSnapshot.getValue(Medicine.class);
                        medicineList.add(medicine);
                    }
                    stockList = medicineList;
                    adapter = new MedicineAdapter(c, medicineList);

                    adapter.sort(new Comparator<Medicine>() {
                        @Override
                        public int compare(Medicine o1, Medicine o2) {
                            return o1.getMedicineName().compareTo(o2.getMedicineName()) ;
                        }
                    });
                    listViewMedicine.setAdapter(adapter);
                    //databaseUpdate();
                    mProgressbarMed.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (mSearchMenuItem != null) {
            searchView = (SearchView) mSearchMenuItem.getActionView();
        }

        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<Medicine> newList = new ArrayList<Medicine>();
                    for (int i=0;i<medicineList.size();i++)
                    {
                        if (medicineList.get(i).getMedicineName().toLowerCase().contains(newText.toLowerCase()))
                        {
                            newList.add(medicineList.get(i));
                        }
                    }

                    if(newList.isEmpty()){
                        //Toast.makeText(c, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    adapter = new MedicineAdapter(getActivity(),newList);

                    listViewMedicine.setAdapter(adapter);
                    return false;
                }
            });
        }

    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_home,menu);
    }
}
