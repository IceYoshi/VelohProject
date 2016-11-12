package lu.mike.uni.velohproject;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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

        // Initial camera position and zoom
        LatLng luxembourg = new LatLng(49.7518, 6.1319);
        LatLng luxembourg_city = new LatLng(49.6116, 6.1319);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(luxembourg , 9.0f));
        mMap.setMinZoomPreference(9.0f);

        // Add city marker
        mMap.addMarker(new MarkerOptions().position(luxembourg_city).title(getResources().getString(R.string.luxembourg_city)));

        // Limit camera movement to Luxembourg
        LatLng swCords = new LatLng(49.41, 5.69);
        LatLng noCords = new LatLng(50.22, 6.57);
        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(swCords, noCords));
    }
}
