package lu.mike.uni.velohproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.HashMap;

import lu.mike.uni.velohproject.stations.AbstractStation;
import lu.mike.uni.velohproject.stations.BusStation;
import lu.mike.uni.velohproject.stations.VelohStation;

/**
 * Created by Mike on 18.11.2016.
 */

public class CustomClusterItemRenderer extends DefaultClusterRenderer<AbstractStation> {

    private Context context;
    SharedPreferences pref;
    private HashMap<String, BitmapDescriptor> markermap;

    public CustomClusterItemRenderer(Context context, GoogleMap map, ClusterManager<AbstractStation> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        markermap = new HashMap<>();

        // preload icons for better performance
        String[] markers = context.getResources().getStringArray(R.array.PREF_MARKER_COLOR_VALUES);
        int width = 106;
        int height = 160;

        for(String marker : markers) {
            markermap.put(marker, BitmapDescriptorFactory.fromBitmap(resizeMapIcons(marker, width, height)));
        }
    }

    @Override
    protected void onBeforeClusterItemRendered(AbstractStation item, MarkerOptions markerOptions) {
        if(pref == null) pref = PreferenceManager.getDefaultSharedPreferences(context);
        String marker;

        if(item instanceof VelohStation) {
            marker = pref.getString(context.getResources().getString(R.string.PREF_VELOH_MARKER_COLOR_KEY), "custommarkerblue");
            markerOptions.icon(markermap.get(marker));
        }
        else if(item instanceof BusStation) {
            marker = pref.getString(context.getResources().getString(R.string.PREF_BUS_MARKER_COLOR_KEY), "custommarkergreen");
            markerOptions.icon(markermap.get(marker));
        }
        else {
            marker = pref.getString(context.getResources().getString(R.string.PREF_DEST_MARKER_COLOR_KEY), "custommarkerred");
            markerOptions.icon(markermap.get(marker));
        }

    }

    // returns a resized Bitmap (used for custom markers :] )
    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
