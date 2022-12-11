package com.atry.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    Button btnSearch;
    EditText etCityName;
    ImageView iconWeather;
    TextView tvTemp,tvCity,tvlon,tvlat,tvPre,tvspeed,tvdegre,tvmin,tvmax,tvHum;

    private  static  final  String API_KEY = "40e5b0c1edeb170349723aad8737ae14";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.btnSearch);
        etCityName = findViewById(R.id.etCityName);
        iconWeather = findViewById(R.id.iconWeather);
        tvTemp = findViewById(R.id.tvTemp);
        tvCity = findViewById(R.id.tvCity);
        tvlon = findViewById(R.id.tvlong);
        tvlat = findViewById(R.id.tvlat);
        tvdegre = findViewById(R.id.tvdegre);
        tvPre = findViewById(R.id.tvPression);
        tvmax = findViewById(R.id.tvmax);
        tvmin = findViewById(R.id.tvmin);
        tvspeed = findViewById(R.id.tvspeed);
        tvHum = findViewById(R.id.tvhumi);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = etCityName.getText().toString();

                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "entrer une ville", Toast.LENGTH_SHORT).show();
                }
                else{
                    // TODO : load weather by cityName
                        loadWeatherByCity(city);
                }
            }
        });
    }
    public void loadWeatherByCity(String city) {

            Ion.with(MainActivity.this)
                    .load("http://api.openweathermap.org/data/2.5/weather?q="+city+"&&units=metrics&appid="+API_KEY)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error
                            //Log.d("result", result.toString());
                            if(e!=null){
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "ereur dans le server", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                JsonObject main = result.get("main").getAsJsonObject();
                                double temp = (main.get("temp").getAsDouble() - 273.15);
                                tvTemp.setText(new DecimalFormat("##.##").format(temp)+ " °C");

                                JsonObject sys = result.get("sys").getAsJsonObject();
                                String country = sys.get("country").getAsString();
                                tvCity.setText(country);

                                JsonArray weather = result.get("weather").getAsJsonArray();
                                String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();
                                loadImage(icon);

                                JsonObject coord = result.get("coord").getAsJsonObject();
                                String lon = coord.get("lon").getAsString();
                                tvlon.setText("longtitude : "+lon);
                                String lat = coord.get("lat").getAsString();
                                tvlat.setText("latitude : "+lat);


                                double min = main.get("temp_min").getAsDouble() - 273.15;
                                tvmin.setText("Temperature minimale :"+new DecimalFormat("##.##").format(min)+ " °C");

                                double max = main.get("temp_max").getAsDouble() - 273.15;
                                tvmax.setText("Temperature maximale :"+new DecimalFormat("##.##").format(max)+ " °C");

                                double pressure = main.get("pressure").getAsDouble();
                                tvPre.setText("la pression d'air :"+pressure +" °hPa");

                                double hum = main.get("humidity").getAsDouble();
                                tvHum.setText("humidite : "+hum +" %");

                                JsonObject wind = result.get("wind").getAsJsonObject();
                                double speed = wind.get("speed").getAsDouble();
                                tvspeed.setText( "la vitesse de wind : " +speed +" km/h");

                                double degre = wind.get("deg").getAsDouble();
                                tvdegre.setText("le degre de wind : "+degre +" °");

                            }

                        }
                    });




        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&&units=metrics&appid="+API_KEY;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                       // textView.setText("Response is: " + response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
            }
        });

    }

    private void loadImage(String icon) {
        Ion.with(this)
                .load("http://api.openweathermap.org/img/w/"+icon+".png").intoImageView(iconWeather);
    }
}