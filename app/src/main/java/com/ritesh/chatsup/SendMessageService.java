package com.ritesh.chatsup;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SendMessageService extends Service {
//    private static final String LOCAL_CONTACT_ID = "local_contact_id";
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    public SendMessageService() {
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Service", "in service");
        sendMessages();
    }
    private void sendMessages() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msgLocal = "";
                try {
                    String serverUrl = Constants.SERVER_URL_SEND;
                    ContentResolver contentResolver = getApplicationContext().getContentResolver();
                    String numberStored = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.pref_owner_no_key), "");
                    Cursor cur = contentResolver.query(DataProvider.CONTENT_URI_PENDING_MSGS,null, null, null, null);
                    if (cur.getCount() > 0) {
                        while (cur.moveToNext()) {
                            String msgToSend = cur.getString(cur.getColumnIndex(DataProvider.COL_MSG));
                            String contact = cur.getString(cur.getColumnIndex(DataProvider.COL_CONTACT));

                            Map<String, String> params123 = new HashMap<String, String>();
                            params123.put(DataProvider.COL_MSG, msgToSend);
                            params123.put(DataProvider.COL_CONTACT, contact);
                            params123.put("sender", numberStored);
                            String s = post(serverUrl, params123, MAX_ATTEMPTS);
                            if(!s.equals("")){
                                getApplicationContext().getContentResolver().delete(DataProvider.CONTENT_URI_PENDING_MSGS,DataProvider.COL_ID+" = "+s,null);
                            }
                        }
                    }
                    cur.close();

                } catch (IOException ex) {
                    msgLocal = "Message could not be sent";
                }
                return msgLocal;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(msg)) {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
                stopService(new Intent(getApplicationContext(), SendMessageService.class));
            }
        }.execute(null, null, null);
    }


    private static String post(String endpoint, Map<String, String> params, int maxAttempts) throws IOException {
        long backoff = BACKOFF_MILLI_SECONDS;
        String s = "";
        for (int i = 1; i <= maxAttempts; i++) {
            //Log.d(TAG, "Attempt #" + i);
            try {
                s = post(endpoint, params);
                return s;
            } catch (IOException e) {
                //Log.e(TAG, "Failed on attempt " + i + ":" + e);
                if (i == maxAttempts) {
                    throw e;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    return "";
                }
                backoff *= 2;
            } catch (IllegalArgumentException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
        return s;

    }
    private static String post(String endpoint, Map<String, String> params) throws IOException {
        String id_in_pending_msg="";
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            if(param.getKey().equals(DataProvider.COL_ID)){
                id_in_pending_msg = param.getValue();
            }else {
                bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            }
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        //Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                id_in_pending_msg="";
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return id_in_pending_msg;
    }
    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
