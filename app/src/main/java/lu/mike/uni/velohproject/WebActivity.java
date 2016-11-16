package lu.mike.uni.velohproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebActivity extends AppCompatActivity implements RetrieveDataListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        switch(intent.getIntExtra("test", 0)) {
            case R.id.nav_bus_request:
                new RetrieveData(this, RequestFactory.requestBusStations());
                break;
            //case R.id.nav_veloh_request:
            //    new RetrieveData(this, RequestFactory.requestVelohStations());
            //    break;
        }

    }

    @Override
    public void onRetrieve(String response) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        // TODO: This is only temporary for testing purposes
        TextView tv = (TextView) findViewById(R.id.jsonTextView);
        JSONArray responseObject = new BusStationParser().parseBusStations(response);
        String stations = "";
        for(int i = 0; i < responseObject.length(); i++) {
            try {
                stations += ((JSONObject) responseObject.get(i)).getString("O") + '\n';
            } catch (JSONException e) {}
        }
        tv.setText(stations);
    }
}
