package com.example.homework4;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    String API_KEY = "AIzaSyB-2m6C9xUuZHfgtKOrayNliBwGBaISxtg";
    Button btnShowCoord;
    EditText edtAddress;

    //default to Sydney AU
    double lat = -34;
    double lng = 151;
    String formatted = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowCoord = (Button)findViewById(R.id.go);
        edtAddress = (EditText)findViewById(R.id.edit_message);

        btnShowCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set lat and lng and jump to MapActivity
                new getCoords().execute(edtAddress.getText().toString().replace(" ","+"));
            }
        });
    }

    /** jump to MapsActivity*/
    public void go(){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("formatted_address", formatted);
        System.out.println("actual latln:" + lat + ", " + lng);
        startActivity(intent);
    }

    private class getCoords extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground (String...strings){
            String response;
            try {
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",address, API_KEY);
                response = http.getHTTPData(url);
                return response;
            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute (String s){
            try {
                JSONObject jsonObject = new JSONObject(s);

                lat = (double) ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat");
                lng = (double)((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng");
                formatted = (String) ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address");
                System.out.println("" + lat + ", " + lng);
                if (dialog.isShowing())
                    dialog.dismiss();
                go();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}