package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tvCity, tvTemp, tvCondition, tvHum, tvWindSpeed;
    String city, condition, cityName;
    EditText city_name;
    double temp;
    double humidity, wind_speed;

    RelativeLayout layout;

    FusedLocationProviderClient flpc;

    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCity = findViewById(R.id.tv_city_name);
        tvTemp = findViewById(R.id.tv_temperature);
        tvCondition = findViewById(R.id.tv_condition);
        tvHum = findViewById(R.id.tv_humidity);
        tvWindSpeed = findViewById(R.id.tv_windSpeed);
        layout = (RelativeLayout) findViewById(R.id.background);

        flpc = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            flpc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            city = addresses.get(0).getLocality();
                            tvCity.setText(city);
                            String apikey = "b5c560d6dfacb9c0043fb3ad1ef26e09";
                            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apikey;

                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject tempMain = response.getJSONObject("main");
                                        JSONObject windMain = response.getJSONObject("wind");
                                        JSONArray condMain = response.getJSONArray("weather");
                                        JSONObject conditionDesc = condMain.getJSONObject(0);
                                        temp = tempMain.getDouble("temp");
                                        wind_speed = windMain.getDouble("speed");
                                        humidity = tempMain.getDouble("humidity");
                                        temp -= 273.15;
                                        condition = conditionDesc.getString("main");
                                        tvTemp.setText(String.valueOf(Math.round(temp)));
                                        tvHum.setText(String.valueOf(humidity));
                                        tvWindSpeed.setText(String.valueOf(wind_speed));
                                        tvCondition.setText(condition);
                                        changeBack(condition);

                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("Test", e.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                            queue.add(request);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        } else {
            askPermission();
        }
    }

    public void changeCity(View v) {
        city_name = findViewById(R.id.edittext_city_name);
        cityName = city_name.getText().toString().strip();

        String apikey = "b5c560d6dfacb9c0043fb3ad1ef26e09";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apikey;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject tempMain = response.getJSONObject("main");
                    JSONObject windMain = response.getJSONObject("wind");
                    JSONArray condMain = response.getJSONArray("weather");
                    JSONObject conditionDesc = condMain.getJSONObject(0);
                    temp = tempMain.getDouble("temp");
                    wind_speed = windMain.getDouble("speed");
                    humidity = tempMain.getDouble("humidity");
                    temp -= 273.15;
                    condition = conditionDesc.getString("main");
                    tvTemp.setText(String.valueOf(Math.round(temp)));
                    tvHum.setText(String.valueOf(humidity));
                    tvWindSpeed.setText(String.valueOf(wind_speed));
                    tvCondition.setText(condition);
                    tvCity.setText(cityName);
                    changeBack(condition);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Enter valid city name", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);
    }

    private void changeBack(String str) {
        switch (str){
            case "Clear":
                layout.setBackgroundResource(R.drawable.clear);
                break;
            case "Clouds":
                layout.setBackgroundResource(R.drawable.clouds);
                break;
            case "Drizzle":
                layout.setBackgroundResource(R.drawable.drizzle);
                break;
            case "Rain":
                layout.setBackgroundResource(R.drawable.rain);
                break;
            case "Snow":
                layout.setBackgroundResource(R.drawable.snow);
                break;
            case "Thunderstorm":
                layout.setBackgroundResource(R.drawable.thunderstorm);
                break;
            default:
                layout.setBackgroundResource(R.drawable.defaultbackground);
                break;
        }
    }
    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }
}