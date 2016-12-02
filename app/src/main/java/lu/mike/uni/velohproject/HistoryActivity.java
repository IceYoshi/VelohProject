package lu.mike.uni.velohproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ArrayList<String> l = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,l);
        Intent intent = getIntent();
        String historyJsonString = intent.getStringExtra(getResources().getString(R.string.HISTORY_JSON_STRING));

        try {
            JSONObject jsonHistory = new JSONObject(historyJsonString);
            JSONArray jarr = jsonHistory.getJSONArray("range");

            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                System.out.println("record: "+record.toString());
                l.add(record.getString("location_street") + "  -  "+record.getString("location_city") + " ("+record.getDouble("range")+"m)");
            }

            ListView listview = ((ListView) findViewById(R.id.idListView));
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO: go back and perform the query (is not really asked but would be a nice feature!)
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
