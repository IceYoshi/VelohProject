package lu.mike.uni.velohproject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mike on 14.11.2016.
 */

public class DataParser {

    public static Collection<AbstractStation> parseStations(String stations) {
        // TODO: Check if BusStation or VelohStation.
        return parseBusStations(stations);
    }

    public static Collection<AbstractStation> parseBusStations(String stations) {
        // Example of station: id=A=1@O=Belair, Sacré-Coeur@X=6,113204@Y=49,610280@U=82@L=200403005@B=1@p=1478177594;
        stations = stations.replaceAll("id=", "");

        Collection<AbstractStation> stationList = new ArrayList<>();
        for(String stationID : stations.split(";")) {
            stationID = stationID.trim();
            if(!stationID.equals(""))
                stationList.add(new BusStation(stationID));
        }
        return stationList;
    }

    public static Collection<AbstractStation> parseVelohStations(String stations) {
        Collection<AbstractStation> stationList = new ArrayList<>();

        try {
            JSONArray stationsJSON = new JSONArray(stations);
            for(int i = 0; i < stationsJSON.length(); i++) {
                stationList.add(new VelohStation(stationsJSON.getJSONObject(i)));
            }
        } catch (JSONException e) {}

        return stationList;
    }

}
