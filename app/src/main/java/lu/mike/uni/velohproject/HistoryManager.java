package lu.mike.uni.velohproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


import lu.mike.uni.velohproject.stations.AbstractStation;
import lu.mike.uni.velohproject.stations.BusStation;
import lu.mike.uni.velohproject.stations.VelohStation;

import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_USER_LOCATION_FOR_NEAREST_STATION;
import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_USER_LOCATION_FOR_STATION_RANGE;
import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_USER_LOCATION_BY_PLACE;

/**
 * Created by Dren413 on 01.12.16.
 */

class RequestByPlaceObject{
    private Location userLocation;
    private String destinationName;
    private LatLng latlngDestination;
    private RequestStationType requestStationType;
    private Collection<AbstractStation> stations;

    public RequestByPlaceObject(Location userLocation, String destinationName, LatLng latlngDestination, RequestStationType requestStationType){
        this.setUserLocation(userLocation);
        this.setDestinationName(destinationName);
        this.setLatlngDestination(latlngDestination);
        this.setStations(getStations());
        this.setRequestStationType(requestStationType);
    }

    public Location getUserLocation() {return userLocation;}
    public void setUserLocation(Location userLocation) {this.userLocation = userLocation;}
    public String getDestinationName() {return destinationName;}
    public void setDestinationName(String destinationName) {this.destinationName = destinationName;}
    public LatLng getLatlngDestination() {return latlngDestination;}
    public void setLatlngDestination(LatLng latlngDestination) {this.latlngDestination = latlngDestination;}
    public RequestStationType getRequestStationType() {return requestStationType;}
    public void setRequestStationType(RequestStationType requestStationType) {this.requestStationType = requestStationType;}
    public Collection<AbstractStation> getStations() {return stations;}
    public void setStations(Collection<AbstractStation> stations){this.stations = stations;}
}

public class HistoryManager implements DataRetrievedListener{
    private static HistoryManager singleton = null;
    private Activity context;
    private JSONObject history;
    private String HISTORY_KEY = "velohproject_history";
    private Boolean shouldLogHistory = true;
    private int MAX_HISTORY = 10;

    // current history record for storage
    private JSONObject current_record;

    private HistoryManager(){}

    public static HistoryManager getInstance(){
        if(singleton == null) doSync();             // first locking check
        return singleton;
    }

    private static synchronized  void doSync(){      // second locking check
        if(singleton == null)
            singleton = new HistoryManager();
    }

    public void init(Activity context){
        this.context = context;
        HISTORY_KEY = context.getResources().getString(R.string.PREFS_HISTORY_KEY);
        loadHistory();
        if(history==null){
            Log.i("","INFO: Template JSON found: \n"+loadHistoryTemplate());
            try {history = new JSONObject(loadHistoryTemplate());} catch (Exception e) {e.printStackTrace();}
        }
    }

    public void preBuildRecord(){
        try {
            if(history.getJSONArray(context.getResources().getString(R.string.HISTORY_JSON_RECORDS_KEY)).length() == MAX_HISTORY) deleteLastRecords(1);

            int id = history.getInt(context.getResources().getString(R.string.HISTORY_JSON_ID_KEY)) + 1;
            history.put(context.getResources().getString(R.string.HISTORY_JSON_ID_KEY), id);

            current_record = new JSONObject();
            current_record.put(context.getResources().getString(R.string.HISTORY_JSON_ID_KEY), id);
            DateFormat sdf = new SimpleDateFormat(context.getResources().getString(R.string.HISTORY_JSON_DATE_VALUE_FORMAT));
            current_record.put(context.getResources().getString(R.string.HISTORY_JSON_DATE_KEY), sdf.format(Calendar.getInstance().getTime()));
        }catch(Exception e){e.printStackTrace();}
    }

    public void appendAllStationLocations(Collection<AbstractStation> stations){
        try{
            JSONArray jarr = new JSONArray();
            for(AbstractStation a : stations){
                JSONObject abstractStation = new JSONObject();
                abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_NAME_KEY),a.getName());

                if(a instanceof BusStation) {
                    abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY), context.getResources().getString(R.string.HISTORY_JSON_BUS_STATION_TYPE_VALUE));
                    abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_BUSSTATION_ID_KEY), a.getId());
                }
                else if(a instanceof VelohStation) {
                    abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY), context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_TYPE_VALUE));
                    abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_VELOHSTATION_NUMBER_KEY), a.getId());
                    JSONObject position = new JSONObject();
                    position.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_KEY),a.getLat());
                    position.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LONGITUDE_KEY),a.getLng());
                    abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_LOCATION_POSITION_KEY), position);
                    abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_TOTAL_BIKE_STANDS_KEY), ((VelohStation) a).getTotal_bikes_stand());
                    abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_AVAILABLE_BIKE_KEY), ((VelohStation) a).getAvailable_bikes());
                    abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_AVAILABLE_BIKE_STANDS_KEY), ((VelohStation) a).getAvailable_bikes_stands());
                }
                else abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_DESTINATION_LOCATION_VALUE));

                JSONObject latlng = new JSONObject();
                latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_KEY),a.getLat());
                latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LONGITUDE_KEY),a.getLng());
                abstractStation.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_LONGITUDE_KEY),latlng);
                jarr.put(abstractStation);
            }
            current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATIONS_ARRAY_KEY), jarr);
        }catch(Exception e){e.printStackTrace();}
    }

    public void appendStationByPlaceHistory(RequestByPlaceObject p){
        if(getShouldLogHistory()){
            try {
                preBuildRecord();
                appendAllStationLocations(p.getStations());

                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_RECORD_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_STATIONS_BY_PLACE_KEY));

                JSONObject tmp_latlng = new JSONObject();
                tmp_latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_KEY),p.getUserLocation().getLatitude());
                tmp_latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LONGITUDE_KEY),p.getUserLocation().getLongitude());
                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_USER_LOCATION_KEY),tmp_latlng);

                tmp_latlng = new JSONObject();
                tmp_latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_KEY),p.getLatlngDestination().latitude);
                tmp_latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LONGITUDE_KEY),p.getLatlngDestination().longitude);
                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_DESTINATION_LOCATION_KEY),tmp_latlng);
                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_DESTINATION_NAME_KEY),p.getDestinationName());

                switch(p.getRequestStationType()){
                    case BUS:
                        current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_BUS_STATION_TYPE_VALUE));
                        break;
                    case VELOH:
                        current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_TYPE_VALUE));
                        break;
                }

                new DataRetriever(this,RequestFactory.requestAddressInfo(p.getUserLocation(), REQUEST_USER_LOCATION_BY_PLACE));
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void appendAllStationsHistory(RequestObject.RequestType requestType){
        if(getShouldLogHistory()){
            try {
                preBuildRecord();

                switch(requestType){
                    case REQUEST_ALL_BUS_STATIONS:
                        current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_BUS_STATION_TYPE_VALUE));
                        current_record.put(context.getResources().getString(R.string.HISTORY_JSON_RECORD_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_ALLBUSSTAIONS_VALUE));
                        history.getJSONArray(context.getResources().getString(R.string.HISTORY_JSON_RECORDS_KEY)).put(current_record);
                        break;
                    case REQUEST_ALL_VELOH_STATIONS:
                        current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_TYPE_VALUE));
                        current_record.put(context.getResources().getString(R.string.HISTORY_JSON_RECORD_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_ALLVELOHSTATIONS_VALUE));
                        history.getJSONArray(context.getResources().getString(R.string.HISTORY_JSON_RECORDS_KEY)).put(current_record);
                        break;
                }

                saveHistory();
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void appendRangeHistory(Location l, double range, RequestObject.RequestType requestType, Collection<AbstractStation> stations){
        if(getShouldLogHistory()){
            try {
                preBuildRecord();
                appendAllStationLocations(stations);

                JSONObject latlng = new JSONObject();
                latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_KEY),l.getLatitude());
                latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LONGITUDE_KEY),l.getLongitude());
                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_KEY),latlng);
                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_RANGE_KEY),range);
                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_RECORD_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_RANGE_METER_VALUE_KEY));

                switch(requestType){
                    case REQUEST_ALL_BUS_STATIONS_IN_RANGE:
                        current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_BUS_STATION_TYPE_VALUE)); break;
                    case REQUEST_ALL_VELOH_STATIONS_IN_RANGE:
                        current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_TYPE_VALUE));break;
                }
                new DataRetriever(this,RequestFactory.requestAddressInfo(l, REQUEST_USER_LOCATION_FOR_STATION_RANGE));
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void appendNearestStationsHistory(Location l, RequestObject.RequestType requestType, Collection<AbstractStation> stations){
        if(getShouldLogHistory()){
            try {
                preBuildRecord();
                appendAllStationLocations(stations);

                JSONObject latlng = new JSONObject();
                latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_KEY),l.getLatitude());
                latlng.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_LONGITUDE_KEY),l.getLongitude());
                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_KEY),latlng);
                current_record.put(context.getResources().getString(R.string.HISTORY_JSON_RECORD_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_NEAREST_STATION_VALUE));

                switch(requestType){
                    case REQUEST_NEAREST_BUS_STATION:     current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_BUS_STATION_TYPE_VALUE)); break;
                    case REQUEST_NEAREST_VELOH_STATION:   current_record.put(context.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY),context.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_TYPE_VALUE)); break;
                }
                new DataRetriever(this,RequestFactory.requestAddressInfo(l,REQUEST_USER_LOCATION_FOR_NEAREST_STATION));
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void clearHistory(){
        // get SharedPreferences instance
        SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.PREFS_FILENAME),Context.MODE_PRIVATE);
        // call edit to prepare for changes
        SharedPreferences.Editor editorPrefs = prefs.edit();
        editorPrefs.remove(HISTORY_KEY).commit();
        loadHistoryTemplate();
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
        //loadHistory();
    }

    public void loadHistory(){
        try {
            history = new JSONObject(context.getSharedPreferences(context.getResources().getString(R.string.PREFS_FILENAME), Context.MODE_PRIVATE).getString(HISTORY_KEY, null));
            Log.i("","INFO: History found: \n"+history.toString());
        } catch (Exception e) {
            //e.printStackTrace();
            Log.i("","INFO: No history found... Now creating from template...");
            loadHistoryTemplate();
        }
    }

    // used for loading the history json template in case there is no history
    public String loadHistoryTemplate() {
        String json = "{\n" +
                "  \"id\":0,\n" +
                "  \"records\":[]\n" +
                "}";

        try{history = new JSONObject(json);}catch(Exception e){e.printStackTrace();}

        return json;
    }


    public void dontLogHistory(){
        shouldLogHistory = false;
    }

    public void logHistory(){
        shouldLogHistory = true;
    }

    public boolean getShouldLogHistory() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(pref.getBoolean(context.getResources().getString(R.string.PREF_REQUEST_LOGGING_KEY), true)) {
            return this.shouldLogHistory;
        } else {
            return false;
        }
    }

    @Override
    public void onDataRetrieved(String result, RequestObject request) {
        if(getShouldLogHistory()) {
            try {
                JSONObject results = new JSONObject(result);
                String street = ((JSONObject) (((JSONObject) results.getJSONArray("results").get(0)).getJSONArray("address_components").get(1))).getString("short_name");
                String city = ((JSONObject) (((JSONObject) results.getJSONArray("results").get(0)).getJSONArray("address_components").get(2))).getString("short_name");
                switch (request.getRequestType()) {
                    case REQUEST_USER_LOCATION_FOR_STATION_RANGE:
                    case REQUEST_USER_LOCATION_FOR_NEAREST_STATION:
                        try{
                            current_record.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_STREET_KEY), street);
                            current_record.put(context.getResources().getString(R.string.HISTORY_JSON_LOCATION_CITY_KEY), city);
                            history.getJSONArray(context.getResources().getString(R.string.HISTORY_JSON_RECORDS_KEY)).put(current_record);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case REQUEST_USER_LOCATION_BY_PLACE:
                        try {
                            current_record.put(context.getResources().getString(R.string.HISTORY_JSON_USER_LOCATION_STREET_KEY), street);
                            current_record.put(context.getResources().getString(R.string.HISTORY_JSON_USER_LOCATION_CITY_KEY), city);
                            history.getJSONArray(context.getResources().getString(R.string.HISTORY_JSON_RECORDS_KEY)).put(current_record);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;
                }
                saveHistory();
            } catch (Exception e) {e.printStackTrace();}
        }
    }


    public String getHistoryRecordById(String id){
        try{
                JSONArray jarr = history.getJSONArray(context.getResources().getString(R.string.HISTORY_JSON_RECORDS_KEY));
                for(int i = 0; i<jarr.length(); ++i){
                    if(jarr.getJSONObject(i).getString(context.getResources().getString(R.string.HISTORY_JSON_ID_KEY)).equals(id)){
                        return jarr.getJSONObject(i).toString();
                    }
                }

        }catch(Exception e){e.printStackTrace();}
        return null;
    }

    public ArrayList<JSONObject> getHistoryBySortedDate(final Boolean ascendingOrder){
        try {
            ArrayList<JSONObject> l = new ArrayList<>();
                JSONArray jarr = history.getJSONArray(context.getResources().getString(R.string.HISTORY_JSON_RECORDS_KEY));
                for(int i = 0; i<jarr.length(); ++i)
                    l.add(jarr.getJSONObject(i));

            final SimpleDateFormat format = new SimpleDateFormat(context.getResources().getString(R.string.HISTORY_JSON_DATE_VALUE_FORMAT));
            final int weight = (ascendingOrder?1:-1);

            Collections.sort(l,new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject j1, JSONObject j2) {
                    try {
                        Date date1 = format.parse(j1.getString(context.getResources().getString(R.string.HISTORY_JSON_DATE_KEY)));
                        Date date2 = format.parse(j2.getString(context.getResources().getString(R.string.HISTORY_JSON_DATE_KEY)));

                        if (date1.compareTo(date2) < 0) return -1*weight;
                        else if (date1.compareTo(date2) > 0) return 1*weight;
                    }catch(Exception e){e.printStackTrace();}
                    return 0;
                }
            });
            return l;

        }catch(Exception e){e.printStackTrace();}
        return null;
    }

    public JSONArray getHistoryBySortedDateAsJSONArray(final Boolean ascendingOrder){
        JSONArray jarr = new JSONArray();
        ArrayList<JSONObject> list = getHistoryBySortedDate(ascendingOrder);

        if(list != null)
            for(JSONObject j : list)
                jarr.put(j);

        return jarr;
    }

    public void deleteLastRecords(int numberOfLastRecords2Delete){
        ArrayList<JSONObject> list = getHistoryBySortedDate(true);
        if(list != null){
            //System.out.println("\n***** Deleting last "+numberOfLastRecords2Delete+" records... \t Currently: #"+list.size() + "");
            if(numberOfLastRecords2Delete < list.size())
                for(int i=0; i<Math.min(numberOfLastRecords2Delete, list.size()); ++i)
                    list.remove(i);
            else list.clear();
            //System.out.println("**** Now #"+list.size()+"");

            this.clearHistory();
            for(JSONObject j : list){
                try {
                    history.getJSONArray(context.getResources().getString(R.string.HISTORY_JSON_RECORDS_KEY)).put(j);
                }catch(Exception e){e.printStackTrace();}
            }
            this.saveHistory();
        }
    }
}
