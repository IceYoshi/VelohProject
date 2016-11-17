package lu.mike.uni.velohproject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mike on 14.11.2016.
 */

// Example of station: id=A=1@O=Belair, Sacré-Coeur@X=6,113204@Y=49,610280@U=82@L=200403005@B=1@p=1478177594;

public class DataParser {

    public static Collection<BusStation> parseBusStations(String stations) {
        stations = stations.replaceAll("id=", "");

        Collection<BusStation> stationList = new ArrayList<>();
        for(String stationID : stations.split(";")) {
            stationID = stationID.trim();
            if(!stationID.equals(""))
                stationList.add(new BusStation(stationID));
        }
        return stationList;
    }

}
