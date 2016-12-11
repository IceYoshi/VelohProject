package lu.mike.uni.velohproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import lu.mike.uni.velohproject.stations.AbstractStation;
import lu.mike.uni.velohproject.stations.Bus;
import lu.mike.uni.velohproject.stations.BusStation;
import lu.mike.uni.velohproject.stations.DestinationLocation;
import lu.mike.uni.velohproject.stations.VelohStation;

import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_ALL_BUS_STATIONS;
import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_ALL_VELOH_STATIONS;
import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_STATION_INFO_FOR_DESTINATION;
import static lu.mike.uni.velohproject.RequestObject.RequestType.REQUEST_STATION_INFO_FOR_USER_LOCATION;

public class MapActivity extends AppCompatActivity implements   OnMapReadyCallback,
                                                                IDialogManagerInputDialogProtocol,
                                                                IDialogManagerAlertDialogProtocol,
                                                                IDialogManagerMessageDialogProtocol,
                                                                ICountDownTerminatorProtocol,
                                                                NavigationView.OnNavigationItemSelectedListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener,
                                                                DataRetrievedListener,
                                                                ClusterManager.OnClusterItemClickListener<AbstractStation>, ClusterManager.OnClusterClickListener<AbstractStation> {

    private final static int HISTORY_REQUEST_CODE = 42;
    private final static int ABOUT_REQUEST_CODE = 43;
    private final static int PREFERENCES_REQUEST_CODE = 44;
    private final static int GOOGLE_PLACE_AUTO_COMPLETE_CODE = 413;

    private GoogleMap mMap;
    private ClusterManager<AbstractStation> mClusterManager;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;   // used for reading current location of device
    Location mLastLocation;

    private String mLastRequestResult;
    private RequestObject mLastRequest;
    private AbstractStation currentStationClicked;

    private Collection<AbstractStation> stationsDestination;
    private Collection<AbstractStation> stationsUser;

    private CountDownTerminator countDownTerminator;
    private HistoryManager historyManager;
    private DialogManager dialogManager;
    private HistoryInterpreter historyInterpreter;
    private RequestStationType requestedStationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stationsDestination = new ArrayList<>();
        stationsUser = new ArrayList<>();
        requestedStationType = RequestStationType.BUS;

        dialogManager = new DialogManager(this);
        historyManager = HistoryManager.getInstance();
        historyManager.init(this);
        historyInterpreter = new HistoryInterpreter(this,historyManager);
        countDownTerminator = new CountDownTerminator(this);
        historyManager.clearHistory();
    }


    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setRenderer(new CustomClusterItemRenderer(this, mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Show "my location" button if permission granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        } else {
            // Request for permission
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Initial camera position and zoom
        LatLng luxembourg = new LatLng(49.7518, 6.1319);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(luxembourg , 9.0f));
        mMap.setMinZoomPreference(9.0f);

        // Limit camera movement to Luxembourg
        LatLng swCords = new LatLng(49.41, 5.69); // southwest
        LatLng neCords = new LatLng(50.22, 6.57); // northeast
        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(swCords, neCords));

        new DataRetriever(this, RequestFactory.requestBusStations());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();

        switch (item.getItemId()) {
            case R.id.nav_request_all_bus_stations:     onItemRequestAllBusStationsClick();     break;
            case R.id.nav_request_all_veloh_stations:   onItemRequestAllVelohStationsClick();   break;
            case R.id.request_stations_in_range:        onItemRequestStationsInRangeClick();    break;
            case R.id.request_nearest_station:          onItemRequestNearestBusStationClick(mLastLocation);  break;
            case R.id.nav_request_stations_by_place:    onItemRequestStationsByPlaceClick();    break;
            case R.id.menu_history:                     onItemHistoryClick();                   break;
            case R.id.menu_preferences:                 onItemPreferencesClick();               break;
            case R.id.menu_about:                       onItemAboutClick();                     break;
        }
        return true;
    }

    public void onItemPreferencesClick(){
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intent, PREFERENCES_REQUEST_CODE);
    }

    public void onItemAboutClick(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivityForResult(intent, ABOUT_REQUEST_CODE);
    }

    public void onItemHistoryClick(){
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra(getResources().getString(R.string.HISTORY_JSON_STRING),historyManager.getHistoryString());
        startActivityForResult(intent, HISTORY_REQUEST_CODE);
    }

    public void onItemRequestAllBusStationsClick(){
        requestedStationType = RequestStationType.BUS;
        historyManager.appendAllStationsHistory(REQUEST_ALL_BUS_STATIONS);
        new DataRetriever(this, RequestFactory.requestBusStations());
    }

    public void onItemRequestAllVelohStationsClick(){
        requestedStationType = RequestStationType.VELOH;
        historyManager.appendAllStationsHistory(REQUEST_ALL_VELOH_STATIONS);
        new DataRetriever(this, RequestFactory.requestVelohStations());
    }

    public void onItemRequestStationsInRangeClick(){
        dialogManager.showInputDialog(getResources().getString(R.string.DIALOG_ASK_USER_FOR_RANGE), DialogManager.InputRequest.REQUEST_INPUT_FOR_STATIONS_IN_RANGE, InputType.TYPE_CLASS_NUMBER, this);
    }

    public void onItemRequestNearestBusStationClick(Location location) {
        if(isLocationKnown()) {
            onDataRetrieved(mLastRequestResult, mLastRequest);
            double minDistance = Double.MAX_VALUE;
            AbstractStation nearestStation = null;

            Collection<AbstractStation> stations = mClusterManager.getAlgorithm().getItems();
            for(AbstractStation station :  stations) {
                double d = station.distanceTo(location);
                if(d < minDistance) {
                    minDistance = d;
                    nearestStation = station;
                }
            }

            mClusterManager.clearItems();
            if(nearestStation != null) {
                mClusterManager.addItem(nearestStation);
            }
            mClusterManager.cluster();

            if(requestedStationType.equals(RequestStationType.BUS))
                    historyManager.appendNearestStationsHistory(location, RequestObject.RequestType.REQUEST_NEAREST_BUS_STATION);
            else if(requestedStationType.equals(RequestStationType.VELOH))
                    historyManager.appendNearestStationsHistory(location, RequestObject.RequestType.REQUEST_NEAREST_VELOH_STATION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(""," RETURNED !!!");
        switch(requestCode){
            case HISTORY_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    String id = data.getStringExtra("id");
                    System.out.println("Clicked on id: "+id);
                    System.out.println("JSON Object: \n"+historyManager.getHistoryRecordById(id));
                    historyInterpreter.executeHistoryQuery(historyManager.getHistoryRecordById(id));

                }
                else {
                    Log.d("","CANCELED !!!");
                }
                break;
            case GOOGLE_PLACE_AUTO_COMPLETE_CODE:
                if(PlaceAutocomplete.getPlace(this, data) != null) {
                    doRequestStationByPlace(mLastLocation, PlaceAutocomplete.getPlace(this, data).getName().toString(), PlaceAutocomplete.getPlace(this, data).getLatLng(), requestedStationType);
                }
                    break;
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("MapActivity", "onBackPressed: ");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // called when location permission is granted to create in instance of a GoogleApiClient that is needed for requesting Location updates
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        System.out.println("Location changed! ");
    }

    // called when connected to Google Play Service
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onInputDialogOKClick(String editTextValue, DialogManager.InputRequest inputRequest) {
        if(!editTextValue.isEmpty() && inputRequest.equals(inputRequest.REQUEST_INPUT_FOR_STATIONS_IN_RANGE)){
            doRequestStationsInRange(Double.valueOf(editTextValue), mLastLocation);
        }
    }

    public void doRequestStationsInRange(double distance, Location location){
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();

        if(!isLocationKnown()) return;
        onDataRetrieved(mLastRequestResult, mLastRequest);

        Collection<AbstractStation> stations = mClusterManager.getAlgorithm().getItems();
        for(AbstractStation station :  stations) {
            if(station.distanceTo(location) > distance)
                mClusterManager.removeItem(station);
        }
        mClusterManager.cluster();

        if(!stations.isEmpty())
            if((stations.toArray()[0]) instanceof BusStation)
                historyManager.appendRangeHistory(location, distance, RequestObject.RequestType.REQUEST_ALL_BUS_STATIONS_IN_RANGE);
            else if((stations.toArray()[0]) instanceof VelohStation)
                historyManager.appendRangeHistory(location, distance, RequestObject.RequestType.REQUEST_ALL_VELOH_STATIONS_IN_RANGE);
    }

    @Override
    public void onInputDialogCancelClick() {}

    @Override
    public void onDataRetrieved(String result, RequestObject request) {
        if(mClusterManager == null) return;

        switch (request.getRequestType()) {
            case REQUEST_ALL_BUS_STATIONS:
            case REQUEST_ALL_VELOH_STATIONS:
                mLastRequestResult = result;
                mLastRequest = request;
                mClusterManager.clearItems();
                mClusterManager.addItems(StationDataParser.parseStations(result, request.getRequestType()));
                mClusterManager.cluster();
                break;
            case REQUEST_BUS_STATION_INFO:
                showFetchedBusStationInfo(result);
                break;
            case REQUEST_STATION_INFO_FOR_DESTINATION:
            case REQUEST_STATION_INFO_FOR_USER_LOCATION:
                processBusStationInformatonForDestination(result, request.getRequestType());
                break;
        }
    }

    private void processBusStationInformatonForDestination(String jsonString, RequestObject.RequestType requestType){
        ArrayList<Bus> busList = new ArrayList<>();
        try{
            JSONObject json = new JSONObject(jsonString);
            JSONArray jarr = json.getJSONArray("Departure");

            for(int i = 0; i<jarr.length(); i++) {
                JSONObject bus_obj = jarr.getJSONObject(i);

                String name = bus_obj.getString("name");
                String time = bus_obj.has("rtTime") ? bus_obj.getString("rtTime") : bus_obj.getString("time");
                time = time.substring(0, time.length() - 3);
                String dest = bus_obj.getString("direction");

                busList.add(new Bus(name, time, dest));
            }

            if(requestType.equals(REQUEST_STATION_INFO_FOR_USER_LOCATION)){
                for(AbstractStation station  : stationsUser) {
                    if(station.getName().equals(jarr.getJSONObject(0).getString("stop")))
                        ((BusStation) station).setBusList(busList);
                }
                countDownTerminator.incProgress("stationsUser");
            }
            else if(requestType.equals(REQUEST_STATION_INFO_FOR_DESTINATION)){
                for(AbstractStation station  : stationsDestination){
                    if(station.getName().equals(jarr.getJSONObject(0).getString("stop")))
                        ((BusStation) station).setBusList(busList);
                }

                countDownTerminator.incProgress("stationsDestination");
            }

        }catch(Exception ex){
            if(requestType.equals(REQUEST_STATION_INFO_FOR_USER_LOCATION)) countDownTerminator.incProgress("stationsUser");
            if(requestType.equals(REQUEST_STATION_INFO_FOR_DESTINATION)) countDownTerminator.incProgress("stationsDestination");
            //ex.printStackTrace();
        }
    }

    public void doHistoryRequestStationByPlace(Location userLocation, String destinationName, LatLng latlngDestination, RequestStationType requestStationType){
        if(requestStationType.equals(RequestStationType.BUS)) this.onItemRequestAllBusStationsClick();
        else if(requestStationType.equals(RequestStationType.VELOH)) this.onItemRequestAllVelohStationsClick();
    }

    public void doRequestStationByPlace(Location userLocation, String destinationName, LatLng latlngDestination, RequestStationType requestStationType){
        if(destinationName == null || !isLocationKnown()) return;

        onDataRetrieved(mLastRequestResult, mLastRequest);

        try{

            countDownTerminator.clear();
            stationsUser.clear();
            stationsDestination.clear();

            DestinationLocation dl = new DestinationLocation(); // red marker
            dl.setLat(latlngDestination.latitude);
            dl.setLng(latlngDestination.longitude);

            dl.setName(destinationName);

            Location destinationLocation = new Location("");    // Location object
            destinationLocation.setLongitude(dl.getLng());
            destinationLocation.setLatitude(dl.getLat());

            Collection<AbstractStation> stations = mClusterManager.getAlgorithm().getItems();
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(latlngDestination);
            boundsBuilder.include(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));

            for (AbstractStation station : stations) {
                if (!(station instanceof DestinationLocation)) {
                    if (station.distanceTo(destinationLocation) <= 500) {
                        stationsDestination.add(station);
                        boundsBuilder.include(station.getPosition());
                    }

                    if (station.distanceTo(mLastLocation) <= 500) {
                        stationsUser.add(station);
                        boundsBuilder.include(station.getPosition());
                    }

                }
            }

                countDownTerminator.addCounter("stationsUser",stationsUser.size());
                countDownTerminator.addCounter("stationsDestination",stationsDestination.size());

                for(AbstractStation station  : stationsUser)
                    new DataRetriever(this, RequestFactory.requestBusStationInfo(station.getId(),REQUEST_STATION_INFO_FOR_USER_LOCATION));

                for(AbstractStation station  : stationsDestination)
                    new DataRetriever(this, RequestFactory.requestBusStationInfo(station.getId(),REQUEST_STATION_INFO_FOR_DESTINATION));

                mClusterManager.clearItems();
                mClusterManager.addItem(dl);
                mClusterManager.cluster();
                LatLngBounds markerBounds = boundsBuilder.build();
                int padding = 150;
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(markerBounds, padding));
                historyManager.appendStationByPlaceHistory(userLocation, destinationName, latlngDestination, requestStationType);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showFetchedBusStationInfo(String jsonString){
        dialogManager.showFetchedBusStationInfo((BusStation) currentStationClicked, jsonString);
    }

    @Override
    public void onAlertDialogCloseClick() {}

    @Override
    public boolean onClusterItemClick(AbstractStation station) {
        currentStationClicked = station;
        dialogManager.showStationInformation(station);
        return false; // false := Center camera on marker upon click
    }

    @Override
    public boolean onClusterClick(Cluster<AbstractStation> cluster) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition() , mMap.getCameraPosition().zoom + 1.5f));
        return true;
    }

    @Override
    public void onMessageDialogCloseClick() {

    }

    @Override
    public void didFinishCountdown() {
        didFinishRequestByPlace();
    }

    public void didFinishRequestByPlace(){
        HashSet<AbstractStation> intersectionSet = new HashSet<>();

        for(AbstractStation destinationStation :  stationsDestination) {
            for (AbstractStation userStation : stationsUser) {
                if(userStation instanceof BusStation) {
                    ArrayList<Bus> busListUser = ((BusStation) userStation).getBusList();
                    ArrayList<Bus> busListDest = ((BusStation) destinationStation).getBusList();

                    for (Bus b_user : busListUser) {
                        for (Bus b_destination : busListDest) {
                            if (b_user.getName().equals(b_destination.getName())) {
                                intersectionSet.add(userStation);
                            }
                        }
                    }
                }
                else{   // for veloh stations there is no criteria for intersection
                    intersectionSet.add(userStation);
                    intersectionSet.add(destinationStation);
                }
            }
        }

        if(intersectionSet.isEmpty()){
            dialogManager.showAlertDialog(getResources().getString(R.string.DIALOG_NO_BUSSSTATIONS_FOUND_FOR_LOCATION),this);
            mClusterManager.clearItems();
        } else {
            for(AbstractStation a : intersectionSet){
                mClusterManager.addItem(a);
            }
        }
        mClusterManager.cluster();

        stationsUser.clear();
        stationsDestination.clear();
        countDownTerminator.clear();
    }

    public void onItemRequestStationsByPlaceClick(){
        try {  AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                //.setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
            Intent intent =  new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);

            startActivityForResult(intent, GOOGLE_PLACE_AUTO_COMPLETE_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean isLocationKnown(){
        if(mLastLocation==null){
            dialogManager.showAlertDialog(getResources().getString(R.string.DIALOG_UNKNOWN_USER_LOCATION),this);
            return false;
        }
        return true;
    }

    public GoogleMap getMap(){return mMap;}
    public ClusterManager getClusterManager(){return mClusterManager;}
}