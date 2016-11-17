package lu.mike.uni.velohproject;

/**
 * Created by Mike on 17.11.2016.
 */


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Example of station: id=A=1@O=Belair, Sacré-Coeur@X=6,113204@Y=49,610280@U=82@L=200403005@B=1@p=1478177594;
 * TODO: requestInfo();
 */
public class BusStation implements ClusterItem {

    private String id;
    private String name;
    private double lat;
    private double lng;

    public BusStation(String stationID) {
        this.id = stationID;

        String[] params = id.split("@");

        for(String attrib : params) {
            String[] keyValuePair = attrib.split("=");

            switch (keyValuePair[0]) {
                case "O":
                    this.name = keyValuePair[1];
                    break;
                case "X":
                    this.lng = Double.valueOf(keyValuePair[1].replace(",", "."));
                    break;
                case "Y":
                    this.lat = Double.valueOf(keyValuePair[1].replace(",", "."));
                    break;
            }
        }

    }

    public double distanceTo(BusStation s) {
        Location sLoc = new Location("");
        sLoc.setLatitude(s.lat);
        sLoc.setLongitude(s.lng);
        return distanceTo(sLoc);
    }

    public double distanceTo(Location sLoc) {
        Location thisLoc = new Location("");
        thisLoc.setLatitude(this.lat);
        thisLoc.setLongitude(this.lng);
        return thisLoc.distanceTo(sLoc);
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(this.lat, this.lng);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
