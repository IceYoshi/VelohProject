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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        jsonHistoryString = getIntent().getStringExtra(getResources().getString(R.string.HISTORY_JSON_STRING));
        System.out.println("\n"+jsonHistoryString+"\n");
        fillHistoryList();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {fillHistoryList();}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void fillHistoryList(){
        ListView lvHistory = ((ListView) findViewById(R.id.idListViewHistory));
        Spinner spinner = (Spinner) findViewById(R.id.idHistorySpinner);
        String stationType = spinner.getSelectedItem().toString().toLowerCase();
        ArrayList<String> list = new ArrayList<>();
        final ArrayList<String> list_ids = new ArrayList<>();

        try {
            JSONArray jarr = new JSONArray(jsonHistoryString);

            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                String record_type = record.getString(getResources().getString(R.string.HISTORY_JSON_RECORD_TYPE_KEY));

                if(record.getString(getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY)).equals(stationType)){
                    if(record_type.equals(getResources().getString(R.string.HISTORY_JSON_ALLBUSSTAIONS_VALUE)))
                        list.add(this.getResources().getString(R.string.BUS_STATIONS_ALL) + " \n(" +record.getString(this.getResources().getString(R.string.HISTORY_JSON_DATE_KEY))+ ")");
                    else if(record_type.equals(getResources().getString(R.string.HISTORY_JSON_ALLVELOHSTATIONS_VALUE)))
                        list.add(this.getResources().getString(R.string.VELOH_STATIONS_ALL) + " \n(" +record.getString(this.getResources().getString(R.string.HISTORY_JSON_DATE_KEY))+ ")");
                    else if(record_type.equals(getResources().getString(R.string.HISTORY_JSON_RANGE_KEY)))
                        list.add(record.getString(this.getResources().getString(R.string.HISTORY_JSON_LOCATION_STREET_KEY)) + "  -  "+record.getString(this.getResources().getString(R.string.HISTORY_JSON_LOCATION_CITY_KEY)) + " " + this.getResources().getString(R.string.RANGE) + " " + record.getDouble(this.getResources().getString(R.string.HISTORY_JSON_RANGE_METER_VALUE_KEY))+"m" + "\n("+record.getString(this.getResources().getString(R.string.HISTORY_JSON_DATE_KEY))+")");
                    else if(record_type.equals(getResources().getString(R.string.HISTORY_JSON_NEAREST_STATION_VALUE)))
                        list.add(record.getString(this.getResources().getString(R.string.HISTORY_JSON_LOCATION_STREET_KEY)) + "  -  "+record.getString(this.getResources().getString(R.string.HISTORY_JSON_LOCATION_CITY_KEY)) + " / " + this.getResources().getString(R.string.NEAREST) + "\n("+record.getString(this.getResources().getString(R.string.HISTORY_JSON_DATE_KEY))+")");
                    else if(record_type.equals(getResources().getString(R.string.HISTORY_JSON_STATIONS_BY_PLACE_KEY)))
                        list.add(record.getString(getResources().getString(R.string.HISTORY_JSON_USER_LOCATION_STREET_KEY)) + " - "+ record.getString(getResources().getString(R.string.HISTORY_JSON_USER_LOCATION_CITY_KEY))+ " ---> " +record.getString(getResources().getString(R.string.HISTORY_JSON_LOCATION_DESTINATION_NAME_KEY))+  "\n(" +record.getString(this.getResources().getString(R.string.HISTORY_JSON_DATE_KEY))+ ")");

                    list_ids.add(record.getString(getResources().getString(R.string.HISTORY_JSON_ID_KEY)));
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
            lvHistory.setAdapter(adapter);

            lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent returnIntend = new Intent();
                    returnIntend.putExtra(getResources().getString(R.string.HISTORY_JSON_ID_KEY),list_ids.get(position));
                    setResult(RESULT_OK, returnIntend);
                    finish();
                }
            });
        } catch (Exception e) {e.printStackTrace();}
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
