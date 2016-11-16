package lu.mike.uni.velohproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mike on 14.11.2016.
 */

// Example of station: id=A=1@O=Belair, Sacré-Coeur@X=6,113204@Y=49,610280@U=82@L=200403005@B=1@p=1478177594;

public class BusStationParser {

    public JSONArray parseBusStations(String stations) {
        JSONArray stationsObject = new JSONArray();
        for(String station : stations.split(";")) {
            station = station.trim();
            if(!station.equals(""))
                stationsObject.put(convert(station));
        }
        return stationsObject;
        // comment
    }

    private JSONObject convert(String station) {
        station = station.replace("id=", "");

        JSONObject obj = new JSONObject();

        try {
            obj.put("id", station);
            String[] params = station.split("@");
            for(String param : params){
                String[] keyValuePair = param.split("=");

                if(isInteger(keyValuePair[1])) {
                    obj.put(keyValuePair[0], Integer.valueOf(keyValuePair[1].replace(",", ".")));
                } else if(isDouble(keyValuePair[1])) {
                    obj.put(keyValuePair[0], Double.valueOf(keyValuePair[1].replace(",", ".")));
                } else { // value is of type String
                    obj.put(keyValuePair[0], keyValuePair[1]);
                }

            }

        } catch (JSONException e) {
            Log.e("BusStationParser", "convert: " + e.getMessage());
        }

        return obj;
    }

    private boolean isDouble(String str) {
        return str.matches("-?\\d+[,|.]\\d+"); // Remark: Returns false for integers!
    }

    private boolean isInteger(String str) {
        return str.matches("-?\\d+");
    }

}
