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

    private int total_bikes_stand;
    private int available_bikes;
    private int available_bikes_stands;

    public VelohStation(JSONObject station) {
        try {
            setId(station.getString("number")); // Verify if getString really returns the number
            setName(station.getString("name"));

            JSONObject position = station.getJSONObject("position");
            setLat(position.getDouble("lat"));
            setLng(position.getDouble("lng"));

            setTotal_bikes_stand(station.getInt("bike_stands"));
            setAvailable_bikes(station.getInt("available_bikes"));
            setAvailable_bikes_stands(station.getInt("available_bike_stands"));

        } catch (JSONException e) {e.printStackTrace();}

    }

    public int getAvailable_bikes() {
        return available_bikes;
    }

    public void setAvailable_bikes(int available_bikes) {
        this.available_bikes = available_bikes;
    }

    public int getAvailable_bikes_stands() {
        return available_bikes_stands;
    }

    public void setAvailable_bikes_stands(int available_bikes_stands) {
        this.available_bikes_stands = available_bikes_stands;
    }

    public int getTotal_bikes_stand() {
        return total_bikes_stand;
    }

    public void setTotal_bikes_stand(int total_bikes_stand) {
        this.total_bikes_stand = total_bikes_stand;
    }
}
