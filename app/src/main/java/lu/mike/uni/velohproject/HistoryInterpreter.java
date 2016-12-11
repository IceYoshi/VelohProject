package lu.mike.uni.velohproject;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

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
            JSONObject json = new JSONObject(jsonHistoryRecord);
            hm.dontLogHistory();
            switch(json.getString("querykey")){
                case "allbusstations":       map.onItemRequestAllBusStationsClick();       break;
                case "allvelohstations":     map.onItemRequestAllVelohStationsClick();     break;
                case "range":                doRequestStationsInRange(json);               break;
                case "neareststation":       doRequestNearestStations(json);               break;
                case "stationsbyplace":      doRequestStationByPlace(json);                break;
            }
            hm.logHistory();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void doRequestStationByPlace(JSONObject json) {
        try{
            LatLng latlng = new LatLng(json.getJSONObject("destination_location").getDouble("lat"),json.getJSONObject("destination_location").getDouble("lng"));
            Location userLocation = new Location("");
            userLocation.setLatitude(json.getJSONObject("user_location").getDouble("lat"));
            userLocation.setLongitude(json.getJSONObject("user_location").getDouble("lng"));

            if(json.getString("type").equals("bus"))
                map.doRequestStationByPlace(userLocation, json.getString("destination_name"), latlng, RequestStationType.BUS);
            else if(json.getString("type").equals("veloh"))
                map.doRequestStationByPlace(userLocation, json.getString("destination_name"), latlng, RequestStationType.VELOH);

        }catch(Exception e){e.printStackTrace();}
    }

    private void doRequestNearestStations(JSONObject json){
        try{
            Location location = new Location("");
            location.setLongitude(json.getJSONObject("location").getDouble("lng"));
            location.setLatitude(json.getJSONObject("location").getDouble("lat"));

            if(json.getString("type").equals("bus"))
                map.onItemRequestAllBusStationsClick();
            else if(json.getString("type").equals("veloh"))
                map.onItemRequestAllVelohStationsClick();

            map.onItemRequestNearestBusStationClick(location);
        }catch(Exception e){e.printStackTrace();}
    }

    private void doRequestStationsInRange(JSONObject json) {
        try{
            double dist = json.getDouble("range");
            Location location = new Location("");
            location.setLongitude(json.getJSONObject("location").getDouble("lng"));
            location.setLatitude(json.getJSONObject("location").getDouble("lat"));

            if(json.getString("type").equals("bus"))
                map.onItemRequestAllBusStationsClick();
            else if(json.getString("type").equals("veloh"))
                map.onItemRequestAllVelohStationsClick();

            map.doRequestStationsInRange(dist, location);
        }catch(Exception e){e.printStackTrace();}
    }

    public MapActivity getMap() {
        return map;
    }

    public void setMap(MapActivity map) {
        this.map = map;
    }
}
