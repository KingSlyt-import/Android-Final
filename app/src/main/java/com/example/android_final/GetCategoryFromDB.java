package com.example.android_final;

import java.util.ArrayList;

public class GetCategoryFromDB {
    private ArrayList<String> arrayList;
    public ArrayList<String> getCategory() {
        arrayList = new ArrayList<String>();
        arrayList.add("Root");
        //////// Add more from database

        return arrayList;
    }
}
