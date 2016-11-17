package lu.mike.uni.velohproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collection;

public class WebActivity extends AppCompatActivity implements RetrieveDataListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        new RetrieveData(this, RequestFactory.requestBusStations());
    }

    @Override
    public void onRetrieve(String response) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // TODO: This is only temporary for testing purposes
        TextView tv = (TextView) findViewById(R.id.jsonTextView);
        Collection<BusStation> stationList = new DataParser().parseBusStations(response);

        StringBuilder stringBuilder = new StringBuilder();

        for(BusStation station : stationList) {
            stringBuilder.append(station.getName() + '\n');
        }

        tv.setText(stringBuilder.toString());
    }
}
