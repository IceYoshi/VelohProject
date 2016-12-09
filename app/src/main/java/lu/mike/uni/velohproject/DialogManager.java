package lu.mike.uni.velohproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import lu.mike.uni.velohproject.stations.AbstractStation;
import lu.mike.uni.velohproject.stations.BusStation;
import lu.mike.uni.velohproject.stations.VelohStation;

import static lu.mike.uni.velohproject.R.id.auto;
import static lu.mike.uni.velohproject.R.id.map;

/**
 * Created by Dren413 on 15.11.16.
 */

interface IDialogManagerInputDialogProtocol{
    void onInputDialogOKClick(String editTextValue, DialogManager.InputRequest inputRequest);
    void onInputDialogCancelClick();
}

interface IDialogManagerAlertDialogProtocol{
    void onAlertDialogCloseClick();
}

interface IDialogManagerMessageDialogProtocol{
    void onMessageDialogCloseClick();
}

public class DialogManager {

    public static enum InputRequest {
        REQUEST_INPUT_FOR_STATIONS_IN_RANGE,
        REQUEST_INPUT_FOR_ADDRESS
    }

    private MapActivity context;

    public DialogManager(MapActivity a){ context = a;}

    public void showInputDialog(String text, final InputRequest inputRequest, int inputType, final IDialogManagerInputDialogProtocol delegator){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(text);

        // Set up the input
        final EditText input = new EditText(context);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setInputType(inputType);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delegator.onInputDialogOKClick(input.getText().toString(),inputRequest);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                delegator.onInputDialogCancelClick();
            }
        });


        ((DrawerLayout) context.findViewById(R.id.drawer_layout)).closeDrawers();
        builder.show();
    }

    public void showAlertDialog(String text, final IDialogManagerAlertDialogProtocol delegator){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(text);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delegator.onAlertDialogCloseClick();
            }
        });
        builder.show();
    }

    public void showMessageDialog(String title, ArrayList<String> messages, final IDialogManagerMessageDialogProtocol delegator){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //String finalMessage = "";
        Boolean first = true;
        for (String msg : messages) {
            //finalMessage += msg+"\n";
            TextView et = new TextView(context);
            et.setText("\t\t"+msg);
            et.setTextSize(16);

            if(first){
                first = false;
                et.setTextColor(Color.parseColor("#CD5C5C"));
            }
            layout.addView(et);
        }

        //builder.setMessage(finalMessage);

        // Set up the buttons
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delegator.onMessageDialogCloseClick();
            }
        });

        final ScrollView scrollView = new ScrollView(context);
        scrollView.addView(layout);

        builder.setView(scrollView);
        builder.show();
    }

    public void showStationInformation(AbstractStation station) {
        if(station instanceof VelohStation){
            ArrayList<String> l = new ArrayList<>();
            l.add("Name: \t"+station.getName());
            VelohStation v = (VelohStation)station;
            l.add("Available bikes: \t"+v.getAvailable_bikes());
            l.add("Available stands: \t"+v.getAvailable_bikes_stands());
            l.add("Total stands: \t"+v.getTotal_bikes_stand());
            this.showMessageDialog("Veloh Station Information", l,context);
        }
        else if(station instanceof BusStation)
            new DataRetriever(context, RequestFactory.requestBusStationInfo(station.getId())); // then, showFetchedBusStationInfo
        else{
            this.showAlertDialog(station.getName(),context);
        }
    }

    public void showFetchedBusStationInfo(BusStation station, String jsonString) {
        ArrayList<String> l = new ArrayList<>();
        l.add("Name: \t"+station.getName());

        try{
            JSONObject json = new JSONObject(jsonString);
            JSONArray jarr = json.getJSONArray("Departure");

            for(int i = 0; i<jarr.length(); i++){
                l.add(jarr.getJSONObject(i).getJSONObject("Product").getString("name") + " ("+jarr.getJSONObject(i).getString("rtTime").substring(0,jarr.getJSONObject(i).getString("rtTime").length()-3) +")" + " --> " + jarr.getJSONObject(i).getString("direction") );
            }
        }catch(Exception ex){}
        this.showMessageDialog("Bus Station Information", l,context);
    }

}
