package lu.mike.uni.velohproject;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

import lu.mike.uni.velohproject.stations.AbstractStation;

public class MapActivity extends AppCompatActivity implements   OnMapReadyCallback,
                                                                IDialogManagerInputDialogProtocol,
                                                                NavigationView.OnNavigationItemSelectedListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener,
                                                                DataRetrievedListener,
                                                                ClusterManager.OnClusterItemClickListener<BusStation>, ClusterManager.OnClusterClickListener<BusStation> {

    private final static int HISTORY_REQUEST_CODE = 42;

    private GoogleMap mMap;
    private ClusterManager<AbstractStation> mClusterManager;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;   // used for reading current location of device
    Location mLastLocation;

    private String mLastRequestResult;
    private RequestObject mLastRequest;

    private HistoryManager hm;

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


        hm = new HistoryManager(this);
        hm.clearHistory();
    }


    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<AbstractStation>(this, mMap);
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
        //mMap.setMinZoomPreference(9.0f);

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
            case R.id.nav_request_all_bus_stations:
                new DataRetriever(this, RequestFactory.requestBusStations());
                break;
            case R.id.request_stations_nearby:
                DialogManager d = new DialogManager(this);
                d.showInputDialog("Give a range in meters: ", this);
                break;
            case R.id.request_nearest_station:
                showNearestBusStation();
                break;
            case R.id.nav_request_all_veloh_stations:
                new DataRetriever(this, RequestFactory.requestVelohStations());
                break;
            case R.id.menu_history:
                Intent intent = new Intent(this, HistoryActivity.class);
                intent.putExtra(getResources().getString(R.string.HISTORY_JSON_STRING),hm.getHistoryString());
                //startActivity(intent);
                startActivityForResult(intent, HISTORY_REQUEST_CODE);
                break;
            case R.id.menu_preferences:
                break;
        }

        return true;
    }

    public void showNearestBusStation() {
        if(mLastLocation != null) {
            onDataRetrieved(mLastRequestResult, mLastRequest);
            double minDistance = Double.MAX_VALUE;
            AbstractStation nearestStation = null;

            Collection<AbstractStation> stations = mClusterManager.getAlgorithm().getItems();
            for(AbstractStation station :  stations) {
                double d = station.distanceTo(mLastLocation);
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

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(""," RETURNED !!!");
        switch(requestCode){
            case HISTORY_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    Log.i("","OK !!!");
                }
                else {
                    Log.i("","CANCELED !!!");
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
    public void onInputDialogOKClick(String editTextValue) {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();

        if(mLastLocation != null) {
            onDataRetrieved(mLastRequestResult, mLastRequest);
            double dist = Double.valueOf(editTextValue);

            Collection<AbstractStation> stations = mClusterManager.getAlgorithm().getItems();
            for(AbstractStation station :  stations) {
                if(station.distanceTo(mLastLocation) > dist)
                    mClusterManager.removeItem(station);
            }
            mClusterManager.cluster();

            hm.appendRangeHistory(mLastLocation,dist);
        }
    }

    @Override
    public void onInputDialogCancelClick() {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
    }

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
                break;
        }
    }

    @Override
    public boolean onClusterItemClick(AbstractStation station) {
        Toast.makeText(this, station.getName(), Toast.LENGTH_SHORT).show();
        return false; // false := Center camera on marker upon click
    }

    @Override
    public boolean onClusterClick(Cluster<AbstractStation> cluster) {
        Toast.makeText(this, "Cluster size: " + cluster.getSize(), Toast.LENGTH_SHORT).show();
        return false;
    }
}
