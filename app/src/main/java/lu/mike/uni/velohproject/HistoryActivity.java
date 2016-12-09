package lu.mike.uni.velohproject;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {

    private String jsonHistoryString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.idHistorySpinner);
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.history_spinner_array, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);

        Intent intent = getIntent();
        jsonHistoryString = intent.getStringExtra(getResources().getString(R.string.HISTORY_JSON_STRING));
        System.out.println(jsonHistoryString);

        fillHistoryList();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillHistoryList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void fillHistoryList(){
        Spinner spinner = (Spinner) findViewById(R.id.idHistorySpinner);
        String stationType = spinner.getSelectedItem().toString().toLowerCase();
        ArrayList<String> list = new ArrayList<>();
        final ArrayList<String> list_ids = new ArrayList<>();

        try {
            JSONObject jsonHistory = new JSONObject(jsonHistoryString);

            JSONArray jarr = jsonHistory.getJSONArray(this.getResources().getString(R.string.HISTORY_RANGE_KEY));
            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                if(record.getString("type").equals(stationType)){
                    list.add(record.getString(this.getResources().getString(R.string.HISTORY_LOCATION_STREET_KEY)) + "  -  "+record.getString(this.getResources().getString(R.string.HISTORY_LOCATION_CITY_KEY)) + " with range "+record.getDouble(this.getResources().getString(R.string.HISTORY_RANGE_METER_VALUE_KEY))+"m" + "\n("+formatStringDate(record.getString(this.getResources().getString(R.string.HISTORY_DATE_KEY)))+")");
                    list_ids.add(record.getString("id"));
                }
            }

            jarr = jsonHistory.getJSONArray(this.getResources().getString(R.string.HISTORY_NEAREST_BUS_STATION_KEY));
            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                if(record.getString("type").equals(stationType)){
                    list.add(record.getString(this.getResources().getString(R.string.HISTORY_LOCATION_STREET_KEY)) + "  -  "+record.getString(this.getResources().getString(R.string.HISTORY_LOCATION_CITY_KEY)) + " / nearest \n("+formatStringDate(record.getString(this.getResources().getString(R.string.HISTORY_DATE_KEY)))+")");
                    list_ids.add(record.getString("id"));
                }
            }

            jarr = jsonHistory.getJSONArray(this.getResources().getString(R.string.HISTORY_ALLBUSSTAIONS_KEY));
            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                if(record.getString("type").equals(stationType)){
                    list.add("All bus stations \n(" +formatStringDate(record.getString(this.getResources().getString(R.string.HISTORY_DATE_KEY)))+ ")");
                    list_ids.add(record.getString("id"));
                }
            }

            jarr = jsonHistory.getJSONArray(this.getResources().getString(R.string.HISTORY_ALLVELOHSTATIONS_KEY));
            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                if(record.getString("type").equals(stationType)){
                    list.add("All veloh stations \n(" +formatStringDate(record.getString(this.getResources().getString(R.string.HISTORY_DATE_KEY)))+ ")");
                    list_ids.add(record.getString("id"));
                }
            }

            jarr = jsonHistory.getJSONArray(this.getResources().getString(R.string.HISTORY_STATIONS_BY_PLACE_KEY));
            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                if(record.getString("type").equals(stationType)){
                    list.add(record.getString("user_location_street") + " - "+ record.getString("user_location_city")+ " ---> " +record.getString("destination_name")+  "\n(" +formatStringDate(record.getString(this.getResources().getString(R.string.HISTORY_DATE_KEY)))+ ")");
                    list_ids.add(record.getString("id"));
                }
            }

            ListView lvHistory = ((ListView) findViewById(R.id.idListViewHistory));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
            lvHistory.setAdapter(adapter);

            lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO: go back and perform the query (is not really asked but would be a nice feature!)
                    Intent returnIntend = new Intent();
                    returnIntend.putExtra("id",list_ids.get(position));
                    setResult(RESULT_OK, returnIntend);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatStringDate(String date){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(date));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
