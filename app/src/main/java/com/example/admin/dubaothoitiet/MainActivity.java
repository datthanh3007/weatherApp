package com.example.admin.dubaothoitiet;

import android.content.Intent;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.ReferenceQueue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH =1000 ;
    ImageButton imgSpeech;
    EditText edtTim;
    Button btnTim, btnNgayTiepTheo;
    TextView txtTenTP, txtTenQG, txtNhietDo, txtTrangThai, txtDoAm, txtMay, txtGio, txtThoiGiaCapNhat;
    ImageView imgIcon;
    String City= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhXa();
        docDuLieu("Hanoi");
        btnTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtTim.getText().toString();
                if (city.equals("")) {
                    City = "Hanoi";
                    docDuLieu(City);
                } else {
                    City = city.replaceAll("","");
                    docDuLieu(City);
                }
            }
        });

        btnNgayTiepTheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtTim.getText().toString();
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("name", city);
                startActivity(intent);

            }
        });
        imgSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });


    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak something");

        try {
            startActivityForResult(intent,REQUEST_CODE_SPEECH);

        }
        catch (Exception e){
            Toast.makeText(this,"err"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH : {
                if(resultCode==RESULT_OK && null!= data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edtTim.setText(result.get(0));
                }
                break;
            }
        }
    }

    public void docDuLieu(String data){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+data+"&units=metric&lang=vi&appid=1217a4299219710331f5b455adcba634";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String day = jsonObject.getString("dt");
                            String name = jsonObject.getString("name").toString();
                            txtTenTP.setText("Tên thành phố: " + name);

                            long l = Long.valueOf(day);

                            Date date = new Date(l * 1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd");

                            String Day = simpleDateFormat.format(date);

                            txtThoiGiaCapNhat.setText(Day);
                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");

                            JSONObject jsonObject1Weather = jsonArrayWeather.getJSONObject(0);

                            String status = jsonObject1Weather.getString("main");
                            String icon = jsonObject1Weather.getString("icon");

                            Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + icon+".png").into(imgIcon);
                            txtTrangThai.setText(status);

                            JSONObject jsonObject1Main = jsonObject.getJSONObject("main");
                            String nhietDo = jsonObject1Main.getString("temp");
                            String doAm = jsonObject1Main.getString("humidity");

                            Double a = Double.valueOf(nhietDo);
                            String NhietDo = String.valueOf(a.intValue());
                            txtNhietDo.setText(NhietDo+"°C");
                            txtDoAm.setText(doAm+" %");

                            JSONObject jsonObject1Gio = jsonObject.getJSONObject("wind");
                            String gio = jsonObject1Gio.getString("speed");
                            txtGio.setText(gio+" m/s");

                            JSONObject jsonObject1May = jsonObject.getJSONObject("clouds");
                            String may = jsonObject1May.getString("all");
                            txtMay.setText(may+" %");

                            JSONObject jsonObject1Sys = jsonObject.getJSONObject("sys");
                            String country = jsonObject1Sys.getString("country");
                            txtTenQG.setText("Tên quốc gia: "+country);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    public void anhXa(){
        imgSpeech = (ImageButton) findViewById(R.id.btnSpeech);
        edtTim = (EditText) findViewById(R.id.edtTimKiem);
        btnTim = (Button) findViewById(R.id.btnTim);
        btnNgayTiepTheo = (Button) findViewById(R.id.btnNgayTiepTheo);
        txtTenTP = (TextView) findViewById(R.id.txtTenTP);
        txtTenQG = (TextView) findViewById(R.id.txtTenQG);
        txtNhietDo = (TextView) findViewById(R.id.txtNhietDo);
        txtTrangThai = (TextView) findViewById(R.id.txtTrangThai);
        txtDoAm = (TextView) findViewById(R.id.txtDoAm);
        txtMay = (TextView) findViewById(R.id.txtMay);
        txtGio = (TextView) findViewById(R.id.txtGio);
        txtThoiGiaCapNhat = (TextView) findViewById(R.id.txtThoiGianCapNhat);
        imgIcon = (ImageView) findViewById(R.id.icon);
    }
}
