package lu.mike.uni.velohproject;

/**
 * Created by Mike on 20.11.2016.
 */

public class RequestObject {

    public static enum RequestType {
        REQUEST_ALL_BUS_STATIONS,
        REQUEST_BUS_STATION_INFO,
        REQUEST_ALL_VELOH_STATIONS
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