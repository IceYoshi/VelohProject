package lu.mike.uni.velohproject.stations;

/**
 * Created by Mike on 17.11.2016.
 */

import java.util.ArrayList;

/**
 * Example of stationID: id=A=1@O=Belair, Sacré-Coeur@X=6,113204@Y=49,610280@U=82@L=200403005@B=1@p=1478177594;
 * TODO: requestInfo() to get additional information about the bus station.
 */
public class BusStation extends AbstractStation {

    private ArrayList<Bus> busList = new ArrayList<>();

    public BusStation(String stationID) {
        setId(stationID);

        String[] params = getId().split("@");

        for(String attrib : params) {
            String[] keyValuePair = attrib.split("=");

            switch (keyValuePair[0]) {
                case "O":
                    setName(keyValuePair[1]);
                    break;
                case "X":
                    setLng(Double.valueOf(keyValuePair[1].replace(",", ".")));
                    break;
                case "Y":
                    setLat(Double.valueOf(keyValuePair[1].replace(",", ".")));
                    break;
            }
        }

    }

    public ArrayList<Bus> getBusList() {
        return busList;
    }

    public void appendBusList(ArrayList<Bus> busList) {

    }

    public void setBusList(ArrayList<Bus> busList) {
        this.busList = busList;
    }
}
