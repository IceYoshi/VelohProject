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
    void onDataRetrieved(String result, RequestObject request);
}

public class DataRetriever extends AsyncTask<Void, Void, String> {

    private RequestObject request;
    private DataRetrievedListener listener;

    public DataRetriever(DataRetrievedListener listener, RequestObject request) {
        this.request = request;
        this.listener = listener;
        this.execute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            URL url = new URL(this.request.getUrl());
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
        listener.onDataRetrieved(result, this.request);
    }
}
