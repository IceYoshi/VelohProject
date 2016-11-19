package lu.mike.uni.velohproject;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Mike on 19.11.2016.
 */

public abstract class AbstractStation implements ClusterItem {

    protected String id;
    protected String name;

    protected double lat;
    protected double lng;

    @Override
    public LatLng getPosition() {
        return new LatLng(this.lat, this.lng);
    }

    protected double distanceTo(BusStation s) {
        Location sLoc = new Location("");
        sLoc.setLatitude(s.lat);
        sLoc.setLongitude(s.lng);
        return distanceTo(sLoc);
    }

    protected double distanceTo(Location sLoc) {
        Location thisLoc = new Location("");
        thisLoc.setLatitude(this.lat);
        thisLoc.setLongitude(this.lng);
        return thisLoc.distanceTo(sLoc);
    }

    protected String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected double getLat() {
        return lat;
    }

    protected void setLat(double lat) {
        this.lat = lat;
    }

    protected double getLng() {
        return lng;
    }

    protected void setLng(double lng) {
        this.lng = lng;
    }

}
