package com.flipoku.Util;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Switch;
import android.widget.TextView;

public class StaticMethodsHelper {
    public static void setSwitchValues(boolean isChecked, Switch selectedSwitch, TextView selectedTextView, String textChecked, String textNotChecked) {
        //selectedSwitch.setText("");// por las dudas borramos el text del switch
        if (isChecked) {
            selectedSwitch.setChecked(true);
            selectedTextView.setText(textChecked);
        } else {
            selectedSwitch.setChecked(false);
            selectedTextView.setText(textNotChecked);
        }
    }

    public static void executeBundleOnThread(final Handler handler, final Bundle bundle, AlertDialog alertDialog) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    Message message = handler.obtainMessage();
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        };
        new Thread(runnable).start();
        alertDialog.dismiss();
    }

    public static String getTimeFromMillis(long durationInMillis) {
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }
}