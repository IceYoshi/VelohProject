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
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements  OnMapReadyCallback,
                                                                IDialogManagerInputDialogProtocol,
                                                                NavigationView.OnNavigationItemSelectedListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener,
                                                                RetrieveDataListener {

    private GoogleMap mMap;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;   // used for reading current location of device
    Location mLastLocation;

    // since we cannot extend markers, we use them as keys and store values for each marker
    private Map<MarkerOptions, JSONObject> allMarkers = new HashMap();
    private ClusterManager<BusStation> mClusterManager;

    // just for testing
    ArrayList<LatLng> stations = new ArrayList();

    private final int INTENT_REQUEST_CODE = 1;


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
    }


    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<BusStation>(this, mMap);
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
        LatLng luxembourg_city = new LatLng(49.6116, 6.1319);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(luxembourg , 9.0f));
        mMap.setMinZoomPreference(9.0f);

        // Add city marker
        //mMap.addMarker(new MarkerOptions().position(luxembourg_city).title(getResources().getString(R.string.luxembourg_city)));

        // Limit camera movement to Luxembourg
        LatLng swCords = new LatLng(49.41, 5.69); // southwest
        LatLng neCords = new LatLng(50.22, 6.57); // northeast
        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(swCords, neCords));



        // just testing
        stations.add(new LatLng(49.612644, 6.112260));
        stations.add(new LatLng(49.608832,6.116737));

        stations.add(new LatLng(49.624698, 6.115155));

        for(LatLng l : stations){
            //allMarkers.put(m,new JSONObject(""));
            //mMap.addMarker(new MarkerOptions().position(l).title("Test").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("custommarker", 100, 100))));
        }
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
            case R.id.nav_bus_request:
                new RetrieveData(this, RequestFactory.requestBusStations());
                break;
            case R.id.request_near:
                DialogManager d = new DialogManager(this);
                d.showInputDialog("Give a range in meters: ", this);
                break;
        }

        return true;
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

    // returns a resized Bitmap (used for custom markers :] )
    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
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
            float dist = Float.valueOf(editTextValue);

            for (LatLng l : stations) {
                Location tmp_l = new Location("");
                tmp_l.setLatitude(l.latitude);
                tmp_l.setLongitude(l.longitude);

                float deltaDist = mLastLocation.distanceTo(tmp_l);

                if (deltaDist <= dist)
                    mMap.addMarker(new MarkerOptions().position(l).title("Test").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("custommarker_green", 100, 100))));
            }
        }
    }

    @Override
    public void onInputDialogCancelClick() {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
    }

    @Override
    public void onRetrieve(String response) {
        if(mClusterManager == null) return;
        mClusterManager.clearItems();
        mClusterManager.addItems(DataParser.parseBusStations(response));
        mClusterManager.cluster();
    }
}
