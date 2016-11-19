package lu.mike.uni.velohproject;

/**
 * Created by Mike on 17.11.2016.
 */

/**
 * Example of stationID: id=A=1@O=Belair, Sacré-Coeur@X=6,113204@Y=49,610280@U=82@L=200403005@B=1@p=1478177594;
 * TODO: requestInfo() to get additional information about the bus station.
 */
public class BusStation extends AbstractStation {

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

}
