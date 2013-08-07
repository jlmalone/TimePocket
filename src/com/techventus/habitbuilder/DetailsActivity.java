package com.techventus.habitbuilder;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class DetailsActivity extends SherlockActivity
{
	

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setTitle("Details");
    }
}
