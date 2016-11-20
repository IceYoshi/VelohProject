package lu.mike.uni.velohproject.stations;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Mike on 19.11.2016.
 */

public abstract class AbstractStation implements ClusterItem {

    private String id;
    private String name;

    private double lat;
    private double lng;

    @Override
    public LatLng getPosition() {
        return new LatLng(this.lat, this.lng);
    }

    public double distanceTo(BusStation s) {
        Location sLoc = new Location("");
        sLoc.setLatitude(s.getLat());
        sLoc.setLongitude(s.getLng());
        return distanceTo(sLoc);
    }

    public double distanceTo(Location sLoc) {
        Location thisLoc = new Location("");
        thisLoc.setLatitude(this.lat);
        thisLoc.setLongitude(this.lng);
        return thisLoc.distanceTo(sLoc);
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
