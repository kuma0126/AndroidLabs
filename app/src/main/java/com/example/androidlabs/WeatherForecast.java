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

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "Weather Forecast";
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
        String speed,min,max,current,iconName,uV1;
        Bitmap image;


        @Override
        protected String doInBackground(String... strings) {
            try {
                String url1 = strings[0];
                URL url = new URL(url1);
                HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
//                httpConnection.setReadTimeout(10000 /* milliseconds */);
//                httpConnection.setConnectTimeout(15000 /* milliseconds */);
//                httpConnection.setRequestMethod("GET");
//                httpConnection.setDoInput(true);
//                // Starts the query
//                httpConnection.connect();
                downloadUrl(url1);
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
                            current=xpp.getAttributeValue(null,"value");
                            Log.e("AsyncTask", "Found currentTemperature: "+ current);
                            publishProgress(25);

                            min=xpp.getAttributeValue(null,"min");
                            Log.e("AsyncTask", "Found minTemperature: "+ min);
                            publishProgress(50);

                            max=xpp.getAttributeValue(null,"max");
                            Log.e("AsyncTask", "Found maxTemperature: "+ max);
                            publishProgress(75);


                        }

                        else if(tagName.equals("weather")){
                           iconName= xpp.getAttributeValue(null,"icon");
                            Log.e("AsyncTask", "Found iconName: "+ iconName);
                            publishProgress(1);

                        }

                    }
                    xpp.next();

                }

                //End of XML reading

                //Start of JSON reading of UV factor:

                //create the network connection:
                URL UVurl = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");
                HttpURLConnection UVConnection = (HttpURLConnection) UVurl.openConnection();
                inStream = UVConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                //now a JSON table:
                JSONObject jObject = new JSONObject(result);
                double aDouble = jObject.getDouble("value");
                 uV1 = aDouble+"";
                Log.i("UV is:", ""+ aDouble);
                Log.e("UV is:", ""+ uV1);
                //END of UV rating

                //connecting or searching through file to get weather image
                if(fileExistance(iconName + ".png")){
                    Log.i(ACTIVITY_NAME, "Looking for file"+iconName + ".png");
                    Log.i(ACTIVITY_NAME, "Weather image exists, found locally");
                    File file = getBaseContext().getFileStreamPath(iconName + ".png");
                    FileInputStream fis = null;
                    // FileInputStream fis = new FileInputStream(file);
                    try {    fis = openFileInput(iconName+".png");   }
                    catch (FileNotFoundException e) {    e.printStackTrace();  }
                    image = BitmapFactory.decodeStream(fis);
                }else {
                    Log.i(ACTIVITY_NAME, "Looking for file"+iconName + ".png");
                    Log.i(ACTIVITY_NAME, "Weather image does not exist, need to download");

                    URL imageUrl = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
                    HttpURLConnection  connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        image = BitmapFactory.decodeStream(connection.getInputStream());
                    }
                    FileOutputStream imageOutput = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 80, imageOutput);
                    imageOutput.flush();
                    imageOutput.close();
                    connection.disconnect();
                }

                publishProgress(100);
                Thread.sleep(2000);


            }
            catch(Exception ex){
                Log.e("Crash!",ex.getMessage());
            }




            return "Finished Task";
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("AsyncTaskExample", "update:" + values[0]);
            //messageBox.setText("At step:" + values[0]);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            //the parameter String s will be "Finished task" from line 27
            currentTemp.setText(current + "℃");
            minTemp.setText(min + "℃");
            maxTemp.setText(max + "℃");
            uV.setText(uV1);
            iv.setImageBitmap(image);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }


    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();   }
}




