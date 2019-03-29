package com.example.androidlabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


public class ProfileActivity extends AppCompatActivity {

    ImageButton mImageButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String ACTIVITY_NAME = "ProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proflile_activity);



        Intent intentFromOne= getIntent();
        String one= intentFromOne.getStringExtra("Email");
        EditText et1 = (EditText)findViewById(R.id.editTextEmail);
        et1.setText(one);

        mImageButton = (ImageButton)findViewById(R.id.button_camera);
        mImageButton.setOnClickListener(b -> dispatchTakePictureIntent());

        Log.e(ACTIVITY_NAME," In Function onCreate()");


        Button chatButton = (Button)findViewById(R.id.chatButton);
        Button weatherButton=(Button)findViewById(R.id.weatherButton);

        weatherButton.setOnClickListener(b -> {
            Intent toWeather = new Intent(ProfileActivity.this,WeatherForecast.class);
            startActivity(toWeather);
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent itLab4 = new Intent(ProfileActivity.this, Fragment.class);
                startActivityForResult(itLab4, 345);
            }
        });

        }


    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageButton.setImageBitmap(imageBitmap);
        }
    }
    @Override
    protected void onPause () {

        super.onPause();
       // mImageButton.setOnClickListener(b -> dispatchTakePictureIntent());
        Log.e(ACTIVITY_NAME," In Function onPause()");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(ACTIVITY_NAME," In Function onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(ACTIVITY_NAME," In Function onStop()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(ACTIVITY_NAME," In Function onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(ACTIVITY_NAME," In Function onStart()");
    }
}








