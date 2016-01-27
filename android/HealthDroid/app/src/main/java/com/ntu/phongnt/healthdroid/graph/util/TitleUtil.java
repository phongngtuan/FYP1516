package com.ntu.phongnt.healthdroid.graph.util;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

public class TitleUtil {
    public static void setSupportActionBarTitle(Activity activity, String title) {
        //Set title
        if (activity instanceof AppCompatActivity)
            ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
    }
}
