package lu.mike.uni.velohproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.places.Place;

import org.json.JSONObject;

import java.util.Date;


import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_USER_LOCATION_FOR_NEAREST_STATION;
import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_USER_LOCATION_FOR_STATION_RANGE;
import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_USER_LOCATION_BY_PLACE;

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
            Log.i("","INFO: Template JSON found: \n"+loadHistoryTemplate());
            try {history = new JSONObject(loadHistoryTemplate());} catch (Exception e) {e.printStackTrace();}
        }
    }

    public synchronized void appendStationByPlaceHistory(Location userLocation, Place place, RequestObject.RequestType requestType){
        if(this.shouldLogHistory){
            try {
                int id = history.getInt("id") + 1;
                history.put("id",id);

                current_record = new JSONObject();
                current_record.put("id",id);
                current_record.put("date",new Date());

                JSONObject latlng = new JSONObject();
                latlng.put("lat",userLocation.getLatitude());
                latlng.put("lng",userLocation.getLongitude());
                current_record.put("user_location",latlng);

                latlng.put("lat",place.getLatLng().latitude);
                latlng.put("lng",place.getLatLng().longitude);
                current_record.put("destination_location",latlng);
                current_record.put("destination_name",place.getName());

                switch(requestType){
                    case REQUEST_BUS_STATIONS_BY_PLACE:
                        current_record.put("type","bus");
                        break;
                    case REQUEST_VELOH_STATIONS_BY_PLACE:
                        current_record.put("type","veloh");
                        break;
                }

                new DataRetriever(this,RequestFactory.requestAddressInfo(userLocation, REQUEST_USER_LOCATION_BY_PLACE));
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public synchronized void appendAllStationsHistory(RequestObject.RequestType requestType){
        if(this.shouldLogHistory){
            try {
                int id = history.getInt("id") + 1;
                history.put("id",id);

                current_record = new JSONObject();
                current_record.put("id",id);
                current_record.put("date",new Date());

                switch(requestType){
                    case REQUEST_ALL_BUS_STATIONS:
                        current_record.put("type","bus");
                        history.getJSONArray(context.getResources().getString(R.string.HISTORY_ALLBUSSTAIONS_KEY)).put(current_record);
                        break;
                    case REQUEST_ALL_VELOH_STATIONS:
                        current_record.put("type","veloh");
                        history.getJSONArray(context.getResources().getString(R.string.HISTORY_ALLVELOHSTATIONS_KEY)).put(current_record);
                        break;
                }

                saveHistory();
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public synchronized void appendRangeHistory(Location l, double range, RequestObject.RequestType requestType){
        if(this.shouldLogHistory){
            try {
                int id = history.getInt("id") + 1;
                history.put("id",id);

                current_record = new JSONObject();
                current_record.put("id",id);
                JSONObject latlng = new JSONObject();
                latlng.put("lat",l.getLatitude());
                latlng.put("lng",l.getLongitude());
                current_record.put("location",latlng);
                current_record.put("range",range);
                current_record.put("date",new Date());

                switch(requestType){
                    case REQUEST_ALL_BUS_STATIONS_IN_RANGE:     current_record.put("type","bus"); break;
                    case REQUEST_ALL_VELOH_STATIONS_IN_RANGE:   current_record.put("type","veloh"); break;
                }
            } catch (Exception e) {e.printStackTrace();}
        }
        new DataRetriever(this,RequestFactory.requestAddressInfo(l, REQUEST_USER_LOCATION_FOR_STATION_RANGE));
    }

    public synchronized void appendNearestStationsHistory(Location l, RequestObject.RequestType requestType){
        if(shouldLogHistory){
            try {
                int id = history.getInt("id") + 1;
                history.put("id",id);

                JSONObject latlng = new JSONObject();
                current_record = new JSONObject();
                current_record.put("id",id);
                latlng.put("lat",l.getLatitude());
                latlng.put("lng",l.getLongitude());
                current_record.put("location",latlng);
                current_record.put("date",new Date());

                switch(requestType){
                    case REQUEST_NEAREST_BUS_STATION:     current_record.put("type","bus"); break;
                    case REQUEST_NEAREST_VELOH_STATION:   current_record.put("type","veloh"); break;
                }
            } catch (Exception e) {e.printStackTrace();}
        }
        new DataRetriever(this,RequestFactory.requestAddressInfo(l,REQUEST_USER_LOCATION_FOR_NEAREST_STATION));
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

    public String getHistoryString(){
        return history.toString();
    }

    // used for loading the history json template in case there is no history
    public String loadHistoryTemplate() {
        String json = "{\n" +
                "  \"id\":0,\n" +
                "  \"allbusstations\":[],\n" +
                "  \"allvelohstations\":[],\n" +
                "  \"range\":[],\n" +
                "  \"neareststation\":[],\n" +
                "  \"stationsbyplace\":[]\n" +
                "}";

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
            case REQUEST_USER_LOCATION_FOR_STATION_RANGE:
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
            case REQUEST_USER_LOCATION_FOR_NEAREST_STATION:
                System.out.println("**************** NEAREST ****************");
                try {
                    JSONObject results = new JSONObject(result);
                    String street = ((JSONObject)(((JSONObject)results.getJSONArray("results").get(0)).getJSONArray("address_components").get(1))).getString("short_name");
                    String city = ((JSONObject)(((JSONObject)results.getJSONArray("results").get(0)).getJSONArray("address_components").get(2))).getString("short_name");
                    current_record.put("location_street",street);
                    current_record.put("location_city",city);
                    history.getJSONArray(context.getResources().getString(R.string.HISTORY_NEAREST_BUS_STATION_KEY)).put(current_record);
                    saveHistory();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case REQUEST_USER_LOCATION_BY_PLACE:
                try{
                    JSONObject results = new JSONObject(result);
                    String street = ((JSONObject)(((JSONObject)results.getJSONArray("results").get(0)).getJSONArray("address_components").get(1))).getString("short_name");
                    String city = ((JSONObject)(((JSONObject)results.getJSONArray("results").get(0)).getJSONArray("address_components").get(2))).getString("short_name");
                    current_record.put("user_location_street",street);
                    current_record.put("user_location_city",city);
                    history.getJSONArray(context.getResources().getString(R.string.HISTORY_STATIONS_BY_PLACE_KEY)).put(current_record);
                    saveHistory();
                }catch(Exception e){e.printStackTrace();}
                break;
        }
    }
}
