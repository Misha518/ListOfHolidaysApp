package com.example.listholidaysapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listholidaysapp.model.ListHolidaysInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GetDataFromInternet.AsyncResponse, MyAdapter.ListItemCLickListener{

    private static final String TAG = "MainActivity";
    private Toast toast;
    private ListHolidaysInfo listHolidaysInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            URL url = new URL("https://calendarific.com/api/v2/holidays?&api_key=37fc371d99f09598b919bfec9efda48629c4bbef&country=US&year=2022");
            new GetDataFromInternet(this).execute(url);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void proccessFinish(String output) {
        Log.d(TAG, "proccessFinish: "+output);

        try {
            JSONObject outputJSON = new JSONObject(output);
            JSONObject responseJSON = outputJSON.getJSONObject("response");
            JSONArray array = responseJSON.getJSONArray("holidays");
            int length = array.length();

            listHolidaysInfo = new ListHolidaysInfo(length);
            ArrayList <String> nameHolidays = new ArrayList<>();
            for (int i = 0; i < length; ++i)
            {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");

                JSONObject obj_date = obj.getJSONObject("date");
                String date_iso = obj_date.getString("iso");

                nameHolidays.add(name);
                Log.d(TAG, "proccessFinish: " + name + " " + date_iso);
                listHolidaysInfo.addHoliday(name,date_iso,i);
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, nameHolidays);
            //ListView listHolidays = findViewById(R.id.listHolidays);
            //listHolidays.setAdapter(adapter);

            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(new MyAdapter(listHolidaysInfo, length, this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        CharSequence text = listHolidaysInfo.ListHolidaysInfo[clickedItemIndex].getHoliday_name();
        int duration = Toast.LENGTH_SHORT;
        if (toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(this,text,duration);
        toast.show();
    }
}