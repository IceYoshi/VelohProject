package lu.mike.uni.velohproject.stations;

/**
 * Created by Mike on 19.11.2016.
 */

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Station example:
 * {
 *  "number":24,
 *  "name":"BRICHERHAFF",
 *  "address":"BRICHERHAFF - AVENUE JF KENNEDY / RUE ALPHONSE WEICKER",
 *  "position":{"lat":49.632,"lng":6.1704},
 *  "banking":true,
 *  "bonus":false,
 *  "status":"OPEN",
 *  "contract_name":"Luxembourg",
 *  "bike_stands":20,
 *  "available_bike_stands":16,
 *  "available_bikes":4,
 *  "last_update":1479556821000
 * }
 */
public class VelohStation extends AbstractStation {

    public VelohStation(JSONObject station) {
        try {
            setId(station.getString("number")); // Verify if getString really returns the number
            setName(station.getString("name"));

            JSONObject position = station.getJSONObject("position");
            setLat(position.getDouble("lat"));
            setLng(position.getDouble("lng"));

        } catch (JSONException e) {}

    }
}
