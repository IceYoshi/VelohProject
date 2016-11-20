package lu.mike.uni.velohproject;

/**
 * Created by Mike on 13.11.2016.
 */

public class RequestFactory {

    private static final String REQUEST_STATIONS = "http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&look_maxdist=$dist$&look_x=$x$&look_y=$y$&stationProxy=yes";
    private static final String REQUEST_STATION_INFO = "http://travelplanner.mobiliteit.lu/restproxy/departureBoard?accessId=cdt&$station$&format=json";

    private static final String VELOH_API_KEY = "8b40b6abc96ba26ea4157be6a3f7c33bc54ca63f";
    private static final String REQUEST_VELOH_STATIONS = "https://api.jcdecaux.com/vls/v1/stations?contract=Luxembourg&apiKey=$api_key$";

    public static RequestObject requestBusStations() { return requestBusStationsNearby(6112550, 49610700, 150000); }

    public static RequestObject requestBusStationsNearby(int x, int y, int dist) {
        dist = Math.max( Math.min(150000, dist), 0 ); // 0 <= dist <= 150000

        return new RequestObject(
                REQUEST_STATIONS
                    .replace("$x$", String.valueOf(x))
                    .replace("$y$", String.valueOf(y))
                    .replace("$dist$", String.valueOf(dist)),
                RequestObject.RequestType.REQUEST_ALL_BUS_STATIONS);
    }

    public static RequestObject requestBusStationInfo(String station) {
        return new RequestObject(
                REQUEST_STATION_INFO
                    .replace("$station$", station),
                RequestObject.RequestType.REQUEST_BUS_STATION_INFO);
    }

    public static RequestObject requestVelohStations() {
        return new RequestObject(
                REQUEST_VELOH_STATIONS
                    .replace("$api_key$", VELOH_API_KEY),
                RequestObject.RequestType.REQUEST_ALL_VELOH_STATIONS);
    }


}
