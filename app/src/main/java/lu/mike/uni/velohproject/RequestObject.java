package lu.mike.uni.velohproject;

/**
 * Created by Mike on 20.11.2016.
 */

public class RequestObject {

    public static enum RequestType {
        REQUEST_ALL_BUS_STATIONS,                       /* for http request */
        REQUEST_ALL_VELOH_STATIONS,                     /* for http request */
        REQUEST_ALL_BUS_STATIONS_IN_RANGE,              /* for differentiating in history */
        REQUEST_ALL_VELOH_STATIONS_IN_RANGE,            /* for differentiating in history */
        REQUEST_NEAREST_BUS_STATION,                    /* for differentiating in history */
        REQUEST_NEAREST_VELOH_STATION,                  /* for differentiating in history */
        REQUEST_BUS_STATIONS_BY_PLACE,                  /* for differentiating in history */
        REQUEST_VELOH_STATIONS_BY_PLACE,                  /* for differentiating in history */
        REQUEST_BUS_STATION_INFO,                       /* for http request */
        REQUEST_STATION_INFO_FOR_DESTINATION,       /* for stations by place feature */
        REQUEST_STATION_INFO_FOR_USER_LOCATION,     /* for stations by place feature */
        REQUEST_USER_LOCATION_FOR_STATION_RANGE,    /* for http request & differentiating in history */
        REQUEST_USER_LOCATION_FOR_NEAREST_STATION,   /* for http request & differentiating in history */
        REQUEST_STATIONS_BY_PLACE,
        REQUEST_USER_LOCATION_BY_PLACE   /* for http request & differentiating in history */
    }

    private String url;
    private RequestType requestType;

    public RequestObject(String url, RequestType requestType) {
        this.setUrl(url);
        this.setRequestType(requestType);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

}
