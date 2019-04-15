package com.example.androidlabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EmptyActivity extends AppCompatActivity {

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.empty_activity);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from Fragment


        DetailFragment dFragment = new DetailFragment();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        dFragment.setTablet(false); //tell the Fragment that it's on a phone.
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, dFragment)
                .commit();
    }
}
