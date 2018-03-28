package com.example.demothree;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ListView lstFeed;
    private ArrayList<String> itemList;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstFeed = findViewById(R.id.lstFeed);
        itemList = new ArrayList<String>(10);

        // Loop through the 25 pages of json
        for(int i = 1; i < 26; i++) {
            String url = "https://wger.de/api/v2/exercise.json/?page=" + Integer.toString(i);
            OkHttpHandler httpHandler = new OkHttpHandler(i);
            httpHandler.execute(url);
        }
    }

    public class OkHttpHandler extends AsyncTask {
        OkHttpClient client = new OkHttpClient();
        private int count;

        public OkHttpHandler(int count) {
            this.count = count;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Request.Builder builder = new Request.Builder();
            builder.url(params[0].toString());
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            parseResponse(o.toString());

            // If its the first page create the adapter
            if(count == 1) {
                adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, itemList);
                lstFeed.setAdapter(adapter);
            }
            else {
                //Toast.makeText(MainActivity.this, Integer.toString(itemList.size()), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        }

        private void parseResponse(String response) {
            try{
                JSONObject json = new JSONObject(response);
                JSONArray results = (JSONArray) json.get("results");

                for(int i = 0; i < results.length(); i++) {
                    JSONObject jsonObj = results.getJSONObject(i);

                    String itemToAdd = jsonObj.getString("name");
                    itemList.add(itemToAdd);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("parseresponseerror", e.getMessage());
            }
        }
    }
}
