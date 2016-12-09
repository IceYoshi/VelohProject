package lu.mike.uni.velohproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import lu.mike.uni.velohproject.stations.AbstractStation;
import lu.mike.uni.velohproject.stations.BusStation;
import lu.mike.uni.velohproject.stations.VelohStation;

/**
 * Created by Mike on 18.11.2016.
 */

public class CustomClusterItemRenderer extends DefaultClusterRenderer<AbstractStation> {

    private Context context;
    private BitmapDescriptor bmdRedMarker;
    private BitmapDescriptor bmdGreenMarker;
    private BitmapDescriptor bmdBlueMarker;

    public CustomClusterItemRenderer(Context context, GoogleMap map, ClusterManager<AbstractStation> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;

        // preload for better performance
        bmdRedMarker = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("custommarkerred", 106, 160));
        bmdGreenMarker = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("custommarkergreen", 106, 160));
        bmdBlueMarker = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("custommarkerblue", 106, 160));
    }

    @Override
    protected void onBeforeClusterItemRendered(AbstractStation item, MarkerOptions markerOptions) {
        if(item instanceof VelohStation)
                markerOptions.icon(bmdBlueMarker);
        else if(item instanceof BusStation)
                markerOptions.icon(bmdGreenMarker);
        else    markerOptions.icon(bmdRedMarker);
    }

    // returns a resized Bitmap (used for custom markers :] )
    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
