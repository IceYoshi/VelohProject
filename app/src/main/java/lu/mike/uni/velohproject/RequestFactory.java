package lu.mike.uni.velohproject;

/**
 * Created by Mike on 13.11.2016.
 */

public class RequestFactory {

    private static final String REQUEST_STATIONS = "http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&look_maxdist=$dist$&look_x=$x$&look_y=$y$&stationProxy=yes";
    private static final String REQUEST_STATION_INFO = "http://travelplanner.mobiliteit.lu/restproxy/departureBoard?accessId=cdt&$station$&format=json";

    private static final String VELOH_API_KEY = "8b40b6abc96ba26ea4157be6a3f7c33bc54ca63f";

    public static String requestBusStations() { return requestBusStationsNearby(6112550, 49610700, 150000); }

    public static String requestBusStationsNearby(int x, int y, int dist) {
        dist = Math.max( Math.min(150000, dist), 0 ); // 0 <= dist <= 150000

        return REQUEST_STATIONS
                .replace("$x$", String.valueOf(x))
                .replace("$y$", String.valueOf(y))
                .replace("$dist$", String.valueOf(dist));
    }

    public static String requestBusStationInfo(String station) {
        return REQUEST_STATION_INFO
                .replace("$station$", station.replaceAll(";", ""));
    }



}
