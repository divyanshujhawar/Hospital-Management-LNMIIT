package com.example.android.hospitalmanagementsystem;

import static android.R.attr.id;

/**
 * Created by Divyanshu on 22-08-2017.
 */

public class DateGen {
    private String dateId;
    private String dateForm;
    private String medId;
    private int quantityBefore;
    private int quantityChanged;
    private String medicineNameOfDate;
    private String medicineTypeOfDate;

    public DateGen(String dateId, String dateForm, String id, int quantityBefore, int quantityChanged, String medicineNameOfDate,
                   String medicineTypeOfDate) {
        this.dateId = dateId;
        this.dateForm = dateForm;
        this.medId = id;
        this.quantityBefore = quantityBefore;
        this.quantityChanged = quantityChanged;
        this.medicineNameOfDate = medicineNameOfDate;
        this.medicineTypeOfDate = medicineTypeOfDate;
    }

    public DateGen(){

    }

    public String getDateId() {
        return dateId;
    }

    public String getDateForm() {
        return dateForm;
    }

    public String getMedId() {
        return medId;
    }

    public int getQuantityBefore() {
        return quantityBefore;
    }

    public int getQuantityChanged() {
        return quantityChanged;
    }

    public String getMedicineNameOfDate() {
        return medicineNameOfDate;
    }

    public String getMedicineTypeOfDate(){ return medicineTypeOfDate; }
}
