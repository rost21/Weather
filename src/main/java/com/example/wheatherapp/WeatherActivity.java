package com.example.wheatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.wheatherapp.json.Sys;
import com.example.wheatherapp.json.Weather;
import com.example.wheatherapp.json.WeatherResults;
import com.github.pavlospt.CircleView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.wheatherapp.adapters.RecyclerViewAdapter;
import com.example.wheatherapp.database.DatabaseQuery;
import com.example.wheatherapp.entity.WeatherObject;
import com.example.wheatherapp.helpers.CustomSharedPreference;
import com.example.wheatherapp.helpers.Helper;
import com.example.wheatherapp.json.FiveDaysForecast;
import com.example.wheatherapp.json.FiveWeathers;
import com.example.wheatherapp.json.Forecast;
import com.example.wheatherapp.json.LocationMapObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = WeatherActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private TextView cityCountry;
    private TextView currentDate;
    private ImageView weatherImage;
    private TextView windResult;
    private TextView humidityResult;
    private RequestQueue queue;
    private LocationMapObject locationMapObject;
    private LocationManager locationManager;
    private Location location;
    private final int REQUEST_LOCATION = 200;
    private CustomSharedPreference sharedPreference;
    private String isLocationSaved;
    private DatabaseQuery query;
    private String apiUrl;
    private TextView result_city;
    private TextView result_description;
    private TextView result_sunrise;
    private TextView result_sunset;
    private FiveDaysForecast fiveDaysForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        queue = Volley.newRequestQueue(this);
        query = new DatabaseQuery(WeatherActivity.this);
        sharedPreference = new CustomSharedPreference(WeatherActivity.this);
        isLocationSaved = sharedPreference.getLocationInPreference();
        cityCountry = findViewById(R.id.city_country);
        currentDate = findViewById(R.id.current_date);
        weatherImage = findViewById(R.id.weather_icon);

        windResult = findViewById(R.id.wind_result);
        humidityResult = findViewById(R.id.humidity_result);
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            if (isLocationSaved.equals("")) {
                // make API call with longitude and latitude
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    apiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&APPID=62f6de3f7c0803216a3a13bbe4ea9914&units=metric";
                    makeJsonObject(apiUrl);
                }
            } else {
                // make API call with city name
                String storedCityName = sharedPreference.getLocationInPreference();
                //String storedCityName = "Enugu";
                System.out.println("Stored city " + storedCityName);
                String[] city = storedCityName.split(",");
                if (!TextUtils.isEmpty(city[0])) {
                    System.out.println("Stored city " + city[0]);
                    String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city[0] + "&APPID=62f6de3f7c0803216a3a13bbe4ea9914&units=metric";
                    makeJsonObject(url);
                }
            }
        }
        result_city = findViewById(R.id.result_city);
        result_description = findViewById(R.id.result_description);
        result_sunrise = findViewById(R.id.sunrise);
        result_sunset = findViewById(R.id.sunset);

        ImageButton addLocation = findViewById(R.id.add_location);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addLocationIntent = new Intent(WeatherActivity.this, AddLocationActivity.class);
                startActivity(addLocationIntent);
            }
        });
        recyclerView = findViewById(R.id.weather_daily_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    @Override
    public void onResume() {
        super.onResume();
        // make API call with city name
        String storedCityName = sharedPreference.getLocationInPreference();
        System.out.println("Stored city " + storedCityName);
        String[] city = storedCityName.split(",");
        if (!TextUtils.isEmpty(city[0])) {
            System.out.println("Stored city " + city[0]);
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city[0] + "&APPID=" + Helper.API_KEY + "&units=metric";
            makeJsonObject(url);
        }
    }


    private void makeJsonObject(final String apiUrl) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response " + response);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                locationMapObject = gson.fromJson(response, LocationMapObject.class);
                if (null == locationMapObject) {
                    Toast.makeText(getApplicationContext(), "Nothing was returned", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Response Good", Toast.LENGTH_LONG).show();

                    String city = locationMapObject.getName() + ", " + locationMapObject.getSys().getCountry();
                    String todayDate = getTodayDateInStringFormat();
                    Long tempVal = Math.round(Math.floor(Double.parseDouble(locationMapObject.getMain().getTemp())));
                    String weatherTemp = String.valueOf(tempVal) + "°";
                    String weatherDescription = Helper.capitalizeFirstLetter(locationMapObject.getWeather().get(0).getDescription());
                    String windSpeed = locationMapObject.getWind().getSpeed();
                    String humidityValue = locationMapObject.getMain().getHumidity();
                    String sunriseValue = locationMapObject.getSys().getSunrise();
                    String sunsetValue = locationMapObject.getSys().getSunset();

                    //save location in database
                    if (apiUrl.contains("lat")) {
                        query.insertNewLocation(locationMapObject.getName());
                    }
                    // populate View data
                    cityCountry.setText(Html.fromHtml(city));
                    currentDate.setText(Html.fromHtml(todayDate));
                    result_city.setText(Html.fromHtml(weatherTemp).toString());
                    result_description.setText(Html.fromHtml(weatherDescription).toString());

                    //weatherImage.setImageResource(R.drawable.sun);
                    windResult.setText(Html.fromHtml(windSpeed) + " km/h");
                    humidityResult.setText(Html.fromHtml(humidityValue) + " %");

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
                    format.setTimeZone(java.util.TimeZone.getDefault());

                    String sunrise = format.format(new Date(Long.parseLong(sunriseValue) * 1000L));
                    String sunset = format.format(new Date(Long.parseLong(sunsetValue) * 1000L));


                    result_sunrise.setText(sunrise);
                    result_sunset.setText(sunset);

                    String icon = locationMapObject.getWeather().get(0).getIcon();
                    String load = "http://openweathermap.org/img/w/" + icon + ".png";
                    Glide.with(getApplicationContext()).load(load).into(weatherImage);

                    fiveDaysApiJsonObjectCall(locationMapObject.getName());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error " + error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

    public int getImage(String description, String sunrise, String sunset) {


        String sr[] = sunrise.split(":");
        String tempSR = sr[0];
        Integer sunriseToday = Integer.parseInt(tempSR);
        System.out.print(sunriseToday);

        String ss[] = sunset.split(":");
        String tempSS = ss[0];
        Integer sunsetToday = Integer.parseInt(tempSS);
        System.out.print(sunsetToday);


        DateFormat df = new SimpleDateFormat("HH");
        String date = df.format(Calendar.getInstance().getTime());
        Integer nowHour = Integer.parseInt(date);
        System.out.print(nowHour);

        String str = "";
        if (nowHour > sunriseToday && nowHour < sunsetToday) {
            str = "day";
            if (description.contains("overcast clouds")) {
                return R.drawable.cloud;
            }
        } else {
            str = "night";

        }


        return 0;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //make api call
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        apiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&APPID=" + Helper.API_KEY + "&units=metric";
                        makeJsonObject(apiUrl);
                    } else {
                        apiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=46.48&lon=30.73&APPID=" + Helper.API_KEY + "&units=metric";
                        makeJsonObject(apiUrl);
                    }
                }
            } else {
                Toast.makeText(WeatherActivity.this, getString(R.string.permission_notice), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();

        alert.show();
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(R.color.colorPrimaryDark);
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(R.color.colorPrimaryDark);
    }

    private String getTodayDateInStringFormat() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("E, d MMMM", Locale.US);
        return df.format(c.getTime());
    }

    private void fiveDaysApiJsonObjectCall(String city) {
        String apiUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "&APPID=" + Helper.API_KEY + "&units=metric";
        final ImageView weather_icon = findViewById(R.id.weather_icon);
        final List<WeatherObject> daysOfTheWeek = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response 5 days" + response);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Forecast forecast = gson.fromJson(response, Forecast.class);

                if (null == forecast) {
                    Toast.makeText(getApplicationContext(), "Nothing was returned", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Response Good", Toast.LENGTH_LONG).show();

                    int[] everyday = new int[]{0, 0, 0, 0, 0, 0, 0};

                    List<FiveWeathers> weatherInfo = forecast.getList();

                    if (null != weatherInfo) {
                        for (int i = 0; i < weatherInfo.size(); i++) {


                            String time = weatherInfo.get(i).getDt_txt();

                            if (time.contains("12:00:00")) {
                                String shortDay = convertTimeToDay(time);
                                String temp = weatherInfo.get(i).getMain().getTemp();
                                String icon = weatherInfo.get(i).getConditions().getIcon();

                                if (convertTimeToDay(time).equals("Mon") && everyday[0] < 1) {
                                    daysOfTheWeek.add(new WeatherObject(shortDay, icon, temp));
                                    everyday[0] = 1;
                                }
                                if (convertTimeToDay(time).equals("Tue") && everyday[1] < 1) {
                                    daysOfTheWeek.add(new WeatherObject(shortDay, icon, temp));
                                    everyday[1] = 1;
                                }
                                if (convertTimeToDay(time).equals("Wed") && everyday[2] < 1) {
                                    daysOfTheWeek.add(new WeatherObject(shortDay, icon, temp));
                                    everyday[2] = 1;
                                }
                                if (convertTimeToDay(time).equals("Thu") && everyday[3] < 1) {
                                    daysOfTheWeek.add(new WeatherObject(shortDay, icon, temp));
                                    everyday[3] = 1;
                                }
                                if (convertTimeToDay(time).equals("Fri") && everyday[4] < 1) {
                                    daysOfTheWeek.add(new WeatherObject(shortDay, icon, temp));
                                    everyday[4] = 1;
                                }
                                if (convertTimeToDay(time).equals("Sat") && everyday[5] < 1) {
                                    daysOfTheWeek.add(new WeatherObject(shortDay, icon, temp));
                                    everyday[5] = 1;
                                }
                                if (convertTimeToDay(time).equals("Sun") && everyday[6] < 1) {
                                    daysOfTheWeek.add(new WeatherObject(shortDay, icon, temp));
                                    everyday[6] = 1;
                                }
                                recyclerView.setAdapter(new RecyclerViewAdapter(WeatherActivity.this, daysOfTheWeek));

                            }
                        }

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error " + error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

    private String convertTimeToDay(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSSS", Locale.US);
        String days = "";
        try {
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            days = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }
}