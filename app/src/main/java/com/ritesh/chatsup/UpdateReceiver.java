package com.ritesh.chatsup;

/**
 * Created by Ritesh on 3/28/2015.
 */

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cur = contentResolver.query(DataProvider.CONTENT_URI_PENDING_MSGS,null, null, null, null);

//        Log.e("Update Receiver", "in service"+cur.getCount());
        if (cur.getCount() > 0 && isOnline(context)) {
            context.startService(new Intent(context, SendMessageService.class));
        }
        cur.close();
    }
    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }
}