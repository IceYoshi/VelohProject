package lu.mike.uni.velohproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Mike on 18.11.2016.
 */

public class CustomClusterItemRenderer extends DefaultClusterRenderer<AbstractStation> {

    private Context context;

    public CustomClusterItemRenderer(Context context, GoogleMap map, ClusterManager<AbstractStation> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(AbstractStation item, MarkerOptions markerOptions) {
        // TODO: Make icon smaller to improve performance.
        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("custommarker", 100, 100)));
    }

    // returns a resized Bitmap (used for custom markers :] )
    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
