package lu.mike.uni.velohproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// Class ListUtils taken from : http://stackoverflow.com/questions/17693578/android-how-to-display-2-listviews-in-one-activity-one-after-the-other
class ListUtils {
    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            // when adapter is null
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }
}

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ArrayList<String> l_bustations_in_range = new ArrayList<>();
        ArrayList<String> l_nearest_busstation = new ArrayList<>();

        Intent intent = getIntent();
        String historyJsonString = intent.getStringExtra(getResources().getString(R.string.HISTORY_JSON_STRING));
        System.out.println(historyJsonString);
        try {
            JSONObject jsonHistory = new JSONObject(historyJsonString);

            JSONArray jarr = jsonHistory.getJSONArray(this.getResources().getString(R.string.HISTORY_RANGE_KEY));
            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                //System.out.println("record: "+record.toString());
                l_bustations_in_range.add(record.getString(this.getResources().getString(R.string.HISTORY_LOCATION_STREET_KEY)) + "  -  "+record.getString(this.getResources().getString(R.string.HISTORY_LOCATION_CITY_KEY)) + " ("+record.getDouble(this.getResources().getString(R.string.HISTORY_RANGE_METER_VALUE_KEY))+"m)");
            }

            jarr = jsonHistory.getJSONArray(this.getResources().getString(R.string.HISTORY_NEAREST_BUS_STATION_KEY));
            for(int i = 0; i<jarr.length(); ++i){
                JSONObject record = jarr.getJSONObject(i);
                //System.out.println("record: "+record.toString());
                l_nearest_busstation.add(record.getString(this.getResources().getString(R.string.HISTORY_LOCATION_STREET_KEY)) + "  -  "+record.getString(this.getResources().getString(R.string.HISTORY_LOCATION_CITY_KEY)));
            }

            ListView listviewBusstationInRange = ((ListView) findViewById(R.id.idListViewBusStationsInRange));
            ListView listviewNearestBusstation = ((ListView) findViewById(R.id.idListViewBusNearestBusStation));
            ListUtils.setDynamicHeight(listviewBusstationInRange);
            ListUtils.setDynamicHeight(listviewNearestBusstation);

            ArrayAdapter<String> adapter_bustations_in_range = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,l_bustations_in_range);
            ArrayAdapter<String> adapter_nearest_busstation = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,l_nearest_busstation);

            listviewBusstationInRange.setAdapter(adapter_bustations_in_range);
            listviewNearestBusstation.setAdapter(adapter_nearest_busstation);


            listviewBusstationInRange.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO: go back and perform the query (is not really asked but would be a nice feature!)
                    Intent returnIntend = new Intent();
                    returnIntend.putExtra("position",position);
                    setResult(RESULT_OK, returnIntend);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
