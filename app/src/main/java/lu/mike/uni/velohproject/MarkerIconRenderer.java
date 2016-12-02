package lu.mike.uni.velohproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Mike on 18.11.2016.
 */

public class MarkerIconRenderer extends DefaultClusterRenderer<BusStation> {

    private Context context;

    public MarkerIconRenderer(Context context, GoogleMap map, ClusterManager<BusStation> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(BusStation item, MarkerOptions markerOptions) {
        // TODO: Make icon smaller to improve performance.
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("custommarkergreen", 128, 160)));
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarkergreen));
    }

    // returns a resized Bitmap (used for custom markers :] )
    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
