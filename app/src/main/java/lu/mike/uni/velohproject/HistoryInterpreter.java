package lu.mike.uni.velohproject;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

import lu.mike.uni.velohproject.stations.AbstractStation;
import lu.mike.uni.velohproject.stations.BusStation;
import lu.mike.uni.velohproject.stations.DestinationLocation;
import lu.mike.uni.velohproject.stations.VelohStation;

/**
 * Created by Dren413 on 09.12.16.
 */

public class HistoryInterpreter {
    private MapActivity map;
    private HistoryManager hm;

    public HistoryInterpreter(MapActivity map, HistoryManager hm){
        this.map = map;
        this.hm = hm;
    }

    public void executeHistoryQuery(String jsonHistoryRecord){
        try{
            hm.dontLogHistory();

            JSONObject json = new JSONObject(jsonHistoryRecord);
            String record_type = json.getString(map.getResources().getString(R.string.HISTORY_JSON_RECORD_TYPE_KEY));

            if(record_type.equals(map.getResources().getString(R.string.HISTORY_JSON_ALLBUSSTAIONS_VALUE)))
                map.onItemRequestAllBusStationsClick();
            else if(record_type.equals(map.getResources().getString(R.string.HISTORY_JSON_ALLVELOHSTATIONS_VALUE)))
                map.onItemRequestAllVelohStationsClick();
            else replayRequest(json);

            hm.logHistory();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public void replayRequest(JSONObject jsonRecord){
       try{
           map.getClusterManager().clearItems();
           JSONArray stationsArray = jsonRecord.getJSONArray(map.getResources().getString(R.string.HISTORY_JSON_STATIONS_ARRAY_KEY));

           LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
           for(int i = 0; i<stationsArray.length(); ++i){
               JSONObject j = stationsArray.getJSONObject(i);
               String type = j.getString(map.getResources().getString(R.string.HISTORY_JSON_STATION_TYPE_KEY));

               AbstractStation a = null;
               if(type.equals(map.getResources().getString(R.string.HISTORY_JSON_BUS_STATION_TYPE_VALUE)))          a = new BusStation(j.getString(map.getResources().getString(R.string.HISTORY_JSON_BUSSTATION_ID_KEY)));
               else if(type.equals(map.getResources().getString(R.string.HISTORY_JSON_VELOH_STATION_TYPE_VALUE)))   a = new VelohStation(j);
               else if(type.equals(map.getResources().getString(R.string.HISTORY_JSON_DESTINATION_LOCATION_VALUE))) {
                   a = new DestinationLocation();
                   a.setName(j.getString(map.getResources().getString(R.string.HISTORY_JSON_STATION_NAME_KEY)));
               }
               else break;

               a.setLat(j.getJSONObject(map.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_LONGITUDE_KEY)).getDouble(map.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_KEY)));
               a.setLng(j.getJSONObject(map.getResources().getString(R.string.HISTORY_JSON_LOCATION_LATITUDE_LONGITUDE_KEY)).getDouble(map.getResources().getString(R.string.HISTORY_JSON_LOCATION_LONGITUDE_KEY)));

               map.getClusterManager().addItem(a);
               boundsBuilder.include(new LatLng(a.getLat(),a.getLng()));
               LatLngBounds markerBounds = boundsBuilder.build();
               int padding = 150;
               map.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(markerBounds, padding));
           }
           map.getClusterManager().cluster();
       }catch(Exception e){e.printStackTrace();}
    }
}
