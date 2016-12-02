package lu.mike.uni.velohproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Date;

import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_ADDRESS_INFO_FOR_BUS_NEAREST_STATION;
import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_ADDRESS_INFO_FOR_BUS_STATION_RANGE;

/**
 * Created by Dren413 on 01.12.16.
 */

public class HistoryManager implements DataRetrievedListener{
    private Activity context;
    private JSONObject history;
    private final String HISTORY_KEY = "velohproject_history";
    private Boolean shouldLogHistory = true;

    // current history record for storage
    private JSONObject current_record;

    public HistoryManager(Activity context){
        this.context = context;
        loadHistory();
        if(history==null){
            Log.i("","INFO: Template JSON found: \n"+loadHistoryTemplateFromAsset());
            try {history = new JSONObject(loadHistoryTemplateFromAsset());} catch (Exception e) {e.printStackTrace();}
        }
    }

    public void appendRangeHistory(Location l, double range){
        if(this.shouldLogHistory){
            try {
                int id = 0;
                try{id = history.getJSONArray(context.getResources().getString(R.string.HISTORY_RANGE_KEY)).length()-1;}catch(NullPointerException ex){ex.printStackTrace();}

                JSONObject latlng = new JSONObject();
                current_record = new JSONObject();
                current_record.put("id",(id==0)?0:++id);
                latlng.put("lat",l.getLatitude());
                latlng.put("lng",l.getLongitude());
                current_record.put("location",latlng);
                current_record.put("range",range);
                current_record.put("date",new Date());

            } catch (Exception e) {e.printStackTrace();}
        }
        new DataRetriever(this,RequestFactory.requestAddressInfo(l, REQUEST_ADDRESS_INFO_FOR_BUS_STATION_RANGE));

    }

    public void appendNearestBusStationsHistory(Location l) {
        if(shouldLogHistory){
            try {
                int id = 0;
                try{id = history.getJSONArray(context.getResources().getString(R.string.HISTORY_NEAREST_BUS_STATION_KEY)).length()-1;}catch(NullPointerException ex){ex.printStackTrace();}

                JSONObject latlng = new JSONObject();
                current_record = new JSONObject();
                current_record.put("id",(id==0)?0:++id);
                latlng.put("lat",l.getLatitude());
                latlng.put("lng",l.getLongitude());
                current_record.put("location",latlng);
                current_record.put("date",new Date());

            } catch (Exception e) {e.printStackTrace();}
        }
        new DataRetriever(this,RequestFactory.requestAddressInfo(l,REQUEST_ADDRESS_INFO_FOR_BUS_NEAREST_STATION));
    }

    public void clearHistory(){
        // get SharedPreferences instance
        SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.PREFS_FILENAME),Context.MODE_PRIVATE);
        // call edit to prepare for changes
        SharedPreferences.Editor editorPrefs = prefs.edit();
        editorPrefs.remove(HISTORY_KEY).commit();
    }

    // saves persistently the history JSON object as a String using the SharedPreferences Framework
    public void saveHistory(){
        // get SharedPreferences instance
        SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.PREFS_FILENAME),Context.MODE_PRIVATE);
        // call edit to prepare for changes
        SharedPreferences.Editor editorPrefs = prefs.edit();
        // update/set key/value pairs
        editorPrefs.putString(HISTORY_KEY,history.toString());
        // write changes (atomic)
        editorPrefs.commit();
    }

    public void loadHistory(){
        try {
            history = new JSONObject(context.getSharedPreferences(context.getResources().getString(R.string.PREFS_FILENAME), Context.MODE_PRIVATE).getString(HISTORY_KEY, null));
            Log.i("","INFO: History found: \n"+history.toString());
        } catch (Exception e) {
            //e.printStackTrace();
            Log.i("","INFO: No history found...");
        }
    }

    public JSONObject getHistoryJSONObject(){
        return history;
    }

    public String getHistoryString(){
        return history.toString();
    }

    // used for loading the history json template in case there is no history
    public String loadHistoryTemplateFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open(context.getResources().getString(R.string.JSON_TEMPLATE_FILENAME));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            //ex.printStackTrace();
            Log.e("","No history template found... Please put the json file in the /assets folder");
            return null;
        }
        return json;
    }


    public void dontLogHistory(){
        shouldLogHistory = false;
    }
    public void logHistory(){
        shouldLogHistory = true;
    }

    @Override
    public void onDataRetrieved(String result, RequestObject request) {
        switch(request.getRequestType()){
            case REQUEST_ADDRESS_INFO_FOR_BUS_STATION_RANGE:
                System.out.println("**************** RANGE ****************");
                try {
                    JSONObject results = new JSONObject(result);
                    String street = ((JSONObject)(((JSONObject)results.getJSONArray("results").get(0)).getJSONArray("address_components").get(1))).getString("short_name");
                    String city = ((JSONObject)(((JSONObject)results.getJSONArray("results").get(0)).getJSONArray("address_components").get(2))).getString("short_name");
                    current_record.put("location_street",street);
                    current_record.put("location_city",city);
                    history.getJSONArray(context.getResources().getString(R.string.HISTORY_RANGE_KEY)).put(current_record);
                    saveHistory();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case REQUEST_ADDRESS_INFO_FOR_BUS_NEAREST_STATION:
                System.out.println("**************** NEAREST ****************");
                try {
                    JSONObject results = new JSONObject(result);
                    String street = ((JSONObject)(((JSONObject)results.getJSONArray("results").get(0)).getJSONArray("address_components").get(1))).getString("short_name");
                    String city = ((JSONObject)(((JSONObject)results.getJSONArray("results").get(0)).getJSONArray("address_components").get(2))).getString("short_name");
                    current_record.put("location_street",street);
                    current_record.put("location_city",city);
                    history.getJSONArray(context.getResources().getString(R.string.HISTORY_NEAREST_BUS_STATION_KEY)).put(current_record);
                    System.out.println("History:\n"+history.toString());
                    //saveHistory();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
