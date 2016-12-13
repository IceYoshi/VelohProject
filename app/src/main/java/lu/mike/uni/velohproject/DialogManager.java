package lu.mike.uni.velohproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lu.mike.uni.velohproject.stations.AbstractStation;
import lu.mike.uni.velohproject.stations.Bus;
import lu.mike.uni.velohproject.stations.BusStation;
import lu.mike.uni.velohproject.stations.VelohStation;

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
        builder.setPositiveButton(context.getResources().getString(R.string.DIALOG_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delegator.onInputDialogOKClick(input.getText().toString(),inputRequest);
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.DIALOG_CANCEL), new DialogInterface.OnClickListener() {
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

        builder.setPositiveButton(context.getResources().getString(R.string.DIALOG_CLOSE), new DialogInterface.OnClickListener() {
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
        layout.setPadding(75,30,75,0);

        Boolean first = true;
        for (String msg : messages) {
            TextView et = new TextView(context);
            et.setText(msg);
            et.setTextSize(16);

            if(first){
                first = false;
                et.setTextColor(Color.parseColor(context.getResources().getString(R.string.DIALOG_BUSSTATION_NAME_COLOR_HEX)));
                et.setTextSize(18);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            layout.addView(et);
        }
        // Set up the buttons
        builder.setPositiveButton(context.getResources().getString(R.string.DIALOG_CLOSE), new DialogInterface.OnClickListener() {
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
            VelohStation v = (VelohStation)station;
            ArrayList<String> l = new ArrayList<>();
            l.add(station.getName());
            l.add(context.getResources().getString(R.string.VELOH_AVAILABLE_BIKES) + " \t"+v.getAvailable_bikes());
            l.add(context.getResources().getString(R.string.VELOH_EMPTY_STANDS) + " \t"+v.getAvailable_bikes_stands());
            l.add(context.getResources().getString(R.string.VELOH_TOTAL_STANDS) + " \t"+v.getTotal_bikes_stand());
            this.showMessageDialog(context.getResources().getString(R.string.DIALOG_TITLE_VELOHSTATION_INFO), l, context);
        }
        else if(station instanceof BusStation)
            new DataRetriever(context, RequestFactory.requestBusStationInfo(station.getId(),RequestObject.RequestType.REQUEST_BUS_STATION_INFO)); // then, showFetchedBusStationInfo
        else{
            this.showAlertDialog(station.getName(),context);
        }
    }

    public void showFetchedBusStationInfo(BusStation station, String jsonString) {
        ArrayList<String> l = new ArrayList<>();
        ArrayList<Bus> l_bus = new ArrayList<>();

        try{
            JSONObject json = new JSONObject(jsonString);
            JSONArray jarr = json.getJSONArray("Departure");

            for(int i = 0; i<jarr.length(); i++){
                JSONObject bus_obj = jarr.getJSONObject(i);

                String name = bus_obj.getString("name");
                String time = bus_obj.has("rtTime") ? bus_obj.getString("rtTime") : bus_obj.getString("time");
                time = time.substring(0, time.length()-3);
                String dest = bus_obj.getString("direction");

                l_bus.add(new Bus(name, time, dest));
            }
        }catch(JSONException ex){
            Log.e("DialogManager", "showFetchedBusStationInfo: " + ex);
        }

        l.add(station.getName());
        if(l_bus.isEmpty()) l.add(context.getResources().getString(R.string.DIALOG_NO_BUSSES));
        else for(Bus b : l_bus)
            l.add(b.getName() + " ("+b.getRtTime()+") --> " + b.getDirection());

        this.showMessageDialog(context.getResources().getString(R.string.DIALOG_TITLE_BUSSTATION_INFO), l,context);
    }

}
