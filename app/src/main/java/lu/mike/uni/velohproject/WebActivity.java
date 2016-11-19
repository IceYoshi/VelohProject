package lu.mike.uni.velohproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collection;

/**
 * Note: WebActivity is currently not being used. May be removed later.
 */
public class WebActivity extends AppCompatActivity implements DataRetrievedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        new DataRetriever(this, RequestFactory.requestBusStations());
    }

    @Override
    public void onDataRetrieved(String result) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // TODO: This is only temporary for testing purposes
        TextView tv = (TextView) findViewById(R.id.jsonTextView);
        Collection<AbstractStation> stations = new DataParser().parseBusStations(result);

        StringBuilder stringBuilder = new StringBuilder();

        for(AbstractStation station : stations) {
            stringBuilder.append(station.getName() + '\n');
        }

        tv.setText(stringBuilder.toString());
    }
}
