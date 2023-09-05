package com.example.weatherforecasttsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RelativeLayout RHome;
    TextView city, mainTempText, ConditionText;
    TextInputLayout TICityName;
    TextInputEditText EDTCityName;
    ImageView Search, weatherIcon;
    RecyclerView RecycleWeather;
    ProgressBar ProgressBar;

    ArrayList<WeatherRVModel> weatherRVModelArrayList;

    WeatherAdapter weatherAdapter;

    LocationManager locationManager;

    String cityName;
    int PERMISSION_CODE =1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        //finding their id's
        RHome  =findViewById(R.id.RHome);
        city  =findViewById(R.id.city);
        mainTempText  =findViewById(R.id.mainTempText);
        ConditionText  =findViewById(R.id.ConditionText);
        RHome  =findViewById(R.id.RHome);
        TICityName =findViewById(R.id.TICityName);
        EDTCityName =findViewById(R.id.EDTCityName);
        Search = findViewById(R.id.Search);
        weatherIcon = findViewById(R.id.weatherIcon);
        RecycleWeather = findViewById(R.id.RecycleWeather);
        ProgressBar = findViewById(R.id.ProgressBar);
//        back_bg = findViewById(R.id.back_bg);



        weatherRVModelArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherRVModelArrayList);

        //setting weather adapter on rcycle view
        RecycleWeather.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //checking if the user has granted permission
        // if user has not granted permission for location
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location!=null){
            cityName = getCityName(location.getLongitude(),location.getLatitude());
            getWeatherInfo(cityName);
        }else{
            cityName = "London";
            getWeatherInfo(cityName);
        }



        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = EDTCityName.getText().toString();
                if(cityName.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }else{
                    city.setText(cityName);
                    getWeatherInfo(cityName);
                }
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "PLease Access Permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void getWeatherInfo(String cityName){
        String url ="http://api.weatherapi.com/v1/forecast.json?key=7202281d3212418bb3b122509232708&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        city.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ProgressBar.setVisibility(View.GONE);
                RHome.setVisibility(View.VISIBLE);

                weatherRVModelArrayList.clear();

                try{
                    String currTemp = response.getJSONObject("current").getString("temp_c");
                    mainTempText.setText(currTemp+"°C ");

                    int is_day = response.getJSONObject("current").getInt("is_day");

                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    ConditionText.setText(condition);

                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(weatherIcon);




                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject fprcast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = fprcast0.getJSONArray("hour");

                    for(int i =0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time= hourObj.getString("time");
                        String temperature = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time, temperature, img,wind));
                    }

                    weatherAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Enter Valid City name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    public String getCityName(double longitude, double latitude){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for(Address adr : addresses){
                if(adr!=null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName = city;
                    }else{
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return  cityName;
    }

//    public void getLocation() {
//        if (isLocationEnabled(MainActivity.this)) {
//            locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
//            criteria = new Criteria();
//            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
//
//            //You can still do this if you like, you might get lucky:
//            Location location = locationManager.getLastKnownLocation(bestProvider);
//            if (location != null) {
//                Log.e("TAG", "GPS is on");
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//                Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
//                searchNearestPlace(voice2text);
//            }
//            else{
//                //This is what you need:
//                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
//            }
//        }
//        else
//        {
//            //prompt user to enable location....
//            //.................
//        }
//    }
}