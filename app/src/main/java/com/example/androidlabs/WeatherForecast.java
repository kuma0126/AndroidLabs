package com.example.androidlabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    private TextView currentTemp,minTemp,maxTemp,uV;
    private ProgressBar progressBar;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ForestQuery networkThread = new ForestQuery();
        networkThread.execute( "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_forecast);
        iv=findViewById(R.id.imageView);
        currentTemp=findViewById(R.id.currentTemp);
        minTemp=findViewById(R.id.minTemp);
        maxTemp=findViewById(R.id.maxTemp);
        uV=findViewById(R.id.Uv);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);  //show the progress bar





    }

    // a subclass of AsyncTask                  Type1    Type2    Type3
    private class ForestQuery extends AsyncTask<String, Integer, String>{
        String speed,min,max,current,iconName;
        Bitmap image;


        @Override
        protected String doInBackground(String... strings) {
            try {
                String url1 = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
                URL url = new URL(url1);
                HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
                InputStream inStream = httpConnection.getInputStream();

                //create a pull parser:
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( inStream  , "UTF-8");

                while(xpp.getEventType()!=XmlPullParser.END_DOCUMENT){
                    if(xpp.getEventType()==XmlPullParser.START_TAG){

                        String tagName=xpp.getName();

                        if(tagName.equals("temperature")){
                            min=xpp.getAttributeValue(null,"min");
                            publishProgress(50);
                            max=xpp.getAttributeValue(null,"max");
                            publishProgress(75);
                            current=xpp.getAttributeValue(null,"value");
                            publishProgress(25);

                        }

                        else if(tagName.equals("weather")){
                           iconName= xpp.getAttributeValue(null,"icon");

                        }

                    }
                    xpp.next();

                }

                Bitmap image = null;
                URL url2 = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(connection.getInputStream());
                }
                publishProgress(100);

                image  = HTTPUtils.getImage(url2);
                FileOutputStream outputStream = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();




            }
            catch(Exception ex){
                Log.e("Crash!",ex.getMessage());
            }




            return null;
        }
    }


}
