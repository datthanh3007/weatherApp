package com.example.admin.dubaothoitiet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {
    String tenThanhPho = "";
    ImageView imgBack;
    TextView txtName;
    ListView lv;
    CustomAdapter customAdapter;
    ArrayList<ThoiTiet> mangThoiTiet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        anhXa();
        Intent intent = getIntent();
        String city = intent.getStringExtra("name");

        if (city.equals("")) {
            tenThanhPho = "Hanoi";
            getDulieu7Ngay(tenThanhPho);
        } else {
            city = city.trim();
            city = city.replaceAll("\\s","");
            tenThanhPho = city;
            getDulieu7Ngay(tenThanhPho);
            Log.d("aaa", "Du lieu truyen ra: " + city);
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void anhXa(){
        imgBack = (ImageView) findViewById(R.id.imageViewBack);
        txtName = (TextView) findViewById(R.id.txtTenThanhPho);
        lv = (ListView) findViewById(R.id.list);
        mangThoiTiet = new ArrayList<ThoiTiet>();
        customAdapter = new CustomAdapter(Main2Activity.this,mangThoiTiet);
        lv.setAdapter(customAdapter);
    }


    public void getDulieu7Ngay(String data) {
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+data+"&cnt=7&units=metric&lang=vi&appid=be8d3e323de722ff78208a7dbb2dcd6f";
        RequestQueue requestQueue = Volley.newRequestQueue(Main2Activity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject1City = jsonObject.getJSONObject("city");
                            String name = jsonObject1City.getString("name").toString();
                            txtName.setText(name);

                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                            for (int i= 0; i< jsonArrayList.length(); i++){

                                JSONObject jsonObject1List = jsonArrayList.getJSONObject(i);

                                String ngay = jsonObject1List.getString("dt");
                                long l = Long.valueOf(ngay);
                                Date date = new Date(l * 1000L);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd");
                                String Day = simpleDateFormat.format(date);

                                JSONObject jsonObject1NhietDo = jsonObject1List.getJSONObject("temp");
                                String max = jsonObject1NhietDo.getString("max");
                                String min = jsonObject1NhietDo.getString("min");
                                Double a = Double.valueOf(max);
                                Double b = Double.valueOf(min);
                                String NhietDoMax = String.valueOf(a.intValue());
                                String NhietDoMin = String.valueOf(b.intValue());

                                JSONArray jsonArrayThoiTiet = jsonObject1List.getJSONArray("weather");
                                JSONObject jsonObject1ThoiTiet = jsonArrayThoiTiet.getJSONObject(0);
                                String status = jsonObject1ThoiTiet.getString("description");
                                String icon   = jsonObject1ThoiTiet.getString("icon");

                                mangThoiTiet.add(new ThoiTiet(Day,status,icon,NhietDoMax,NhietDoMin));
                            }
                            customAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }
}
