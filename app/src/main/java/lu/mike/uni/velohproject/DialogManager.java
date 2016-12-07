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

import java.util.ArrayList;
import java.util.Map;

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

    Activity context;

    public DialogManager(Activity a){ context = a;}

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
        // Set up the buttons
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
        Toast.makeText(context,"****** "+messages,Toast.LENGTH_SHORT);
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
}
