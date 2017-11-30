package com.example.android.hospitalmanagementsystem;

/**
 * Created by Divyanshu on 19-08-2017.
 */

public class Medicine {
    private String medicineName;
    private int medicineQuantity;
    private String id;
    private String type;

    public Medicine(String id , String medicineName, int medicineQuantity, String type) {
        this.id = id;
        this.medicineName = medicineName;
        this.medicineQuantity = medicineQuantity;
        this.type = type;
    }

    public Medicine(){

    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getMedicineQuantity() {
        return medicineQuantity;
    }

    public String getId(){
        return id;
    }

    public String getType(){return type;}
}
