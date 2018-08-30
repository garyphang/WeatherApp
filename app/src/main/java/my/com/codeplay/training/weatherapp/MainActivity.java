package my.com.codeplay.training.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocation, tvTemperature, tvHumidity, tvWindSpeed, tvCloudiness;
    private Button btnRefresh;
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.location);
        tvTemperature = findViewById(R.id.temperature);
        tvHumidity = findViewById(R.id.humidity);
        tvWindSpeed = findViewById(R.id.wind_speed);
        tvCloudiness = findViewById(R.id.cloudiness);
        btnRefresh = findViewById(R.id.button_refresh);
        ivIcon = findViewById(R.id.icon);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WeatherDataRetrival().execute();
            }
        });
    }

    private class WeatherDataRetrival extends AsyncTask<Void, Void, String> {

        private static final String WEATHER_SOURCE = "http://api.openweathermap.org/data/2.5/weather?APPID=82445b6c96b99bc3ffb78a4c0e17fca5&mode=json&id=1735161";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            NetworkInfo networkInfo = ((ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // network connected

                try {
                    URL url = new URL(WEATHER_SOURCE);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        if (bufferedReader != null) {
                            String readline;
                            StringBuffer stringBuffer = new StringBuffer();
                            while ((readline = bufferedReader.readLine()) != null) {
                                stringBuffer.append(readline);
                            }
                            return stringBuffer.toString();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // no connection

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                final JSONObject weatherJSON;
                try {
                    weatherJSON = new JSONObject(result);
                    tvLocation.setText(weatherJSON.getString("name") + "," + weatherJSON.getJSONObject("sys").getString("country"));

                    tvWindSpeed.setText(String.valueOf(weatherJSON.getJSONObject("wind").getDouble("speed")) + " mps");
                    tvCloudiness.setText(String.valueOf(weatherJSON.getJSONObject("clouds").getInt("all")) + "%");

                    final JSONObject mainJSON = weatherJSON.getJSONObject("main");
                    tvTemperature.setText(String.valueOf((int)(mainJSON.getDouble("temp")-273.15)));
                    tvHumidity.setText(String.valueOf(mainJSON.getInt("humidity")) + "%");

                    final JSONArray weatherJSONArray = weatherJSON.getJSONArray("weather");
                    if (weatherJSONArray.length() > 0) {
                        int code = weatherJSONArray.getJSONObject(0).getInt("id");
                        ivIcon.setImageResource(getIcon(code));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private int getIcon(int v) {
            if ( v >= 906 ){
                return R.drawable.ic_hail_large;
            } else {
                if ( v >= 905 ){
                    return R.drawable.ic_windy_large;
                }else{
                    if ( v >= 803 ){
                        return R.drawable.ic_broken_clouds_large;
                    }else{
                        if ( v >= 802 ){
                            return R.drawable.ic_scattered_clouds_large;
                        }else{
                            if (v >= 801 ){
                                return  R.drawable.ic_day_few_clouds_large;
                            }else{
                                if ( v >= 800 ){
                                    return R.drawable.ic_day_clear_large;
                                }else{
                                    if ( v >= 701){
                                        return R.drawable.ic_fog_large;
                                    }else{
                                        if ( v >= 600 ){
                                            return R.drawable.ic_snow_large;
                                        }else{
                                            if ( v >= 500 ){
                                                return R.drawable.ic_rain_large;
                                            }else{
                                                if ( v >= 300 ){
                                                    return R.drawable.ic_drizzle_large;
                                                }else{
                                                    return R.drawable.ic_thunderstorm_large;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }
}