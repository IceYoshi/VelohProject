package lu.mike.uni.velohproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by Dren413 on 15.11.16.
 */

interface IDialogManagerInputDialogProtocol{
    void onInputDialogOKClick(String editTextValue);
    void onInputDialogCancelClick();
}

interface IDialogManagerAlertDialogProtocol{
    void onAlertDialogCloseClick();
}

public class DialogManager {

    Activity context;

    public DialogManager(Activity a){ context = a;}

    public void showInputDialog(String text, final IDialogManagerInputDialogProtocol delegator){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(text);

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delegator.onInputDialogOKClick(input.getText().toString());
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
}
