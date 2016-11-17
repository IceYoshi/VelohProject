package lu.mike.uni.velohproject;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mike on 13.11.2016.
 */

interface DataRetrievedListener {
    void onDataRetrieved(String result);
}

public class DataRetriever extends AsyncTask<Void, Void, String> {

    private String address;
    private DataRetrievedListener listener;

    public DataRetriever(DataRetrievedListener listener, String address) {
        this.address = address;
        this.listener = listener;
        this.execute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            URL url = new URL(this.address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onDataRetrieved(result);
    }
}
