package com.ritesh.chatsup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends ActionBarActivity {
//    private static final int INITIALIZE_REQUEST_CODE = 999;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private final String LOG_TAG = MainActivity.class.getSimpleName();
//    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = Constants.SENDER_ID;

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    GoogleCloudMessaging googleCloudMessaging;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String registrationIdCloudMessaging;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        registrationIdCloudMessaging = getRegistrationId(context);
        syncContacts();
        if (!registrationIdCloudMessaging.isEmpty()) {
            //TODO: sync not here but via gcm!!
            startRecentContactsActivityAndFinishMainActivity();
            return;
        }
        String numberStored = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.pref_owner_no_key),"");
        if(!numberStored.isEmpty()){
            registerOnGcm();
        }else {
            setContentView(R.layout.activity_main);

            // Check device for Play Services APK. If check succeeds, proceed with
            //  GCM registration.
            if (checkPlayServices()) {

                Button button = (Button) findViewById(R.id.registration_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) findViewById(R.id.phone_number);
                        String number = editText.getText().toString();
                        if (number.length() != 10) {
                            Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        prefs.putString(getString(R.string.pref_owner_no_key), number);
                        prefs.apply();
                        registerOnGcm();
                    }
                });
            } else {
                Toast.makeText(this, "Please Get Valid Google Play Services!!!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startRecentContactsActivityAndFinishMainActivity() {
        startActivity(new Intent(context,RecentContactsActivity.class));
        finish();
    }

    private void registerOnGcm() {
        //TODO:Please w8 dialog box;
        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
        registrationIdCloudMessaging = getRegistrationId(context);
        if (registrationIdCloudMessaging.isEmpty()) {
            registerInBackground();
        } else {
            startRecentContactsActivityAndFinishMainActivity();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
//                Log.i("TAG", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
//            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
//            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    private void registerInBackground() {
         new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                //String msg = "";
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                    }
                    registrationIdCloudMessaging = googleCloudMessaging.register(SENDER_ID);
                    //msg = "Device registered, registration ID=" + registrationIdCloudMessaging;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
//                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, registrationIdCloudMessaging);
                } catch (IOException ex) {
                    //msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                registrationIdCloudMessaging = getRegistrationId(context);
                if(registrationIdCloudMessaging.isEmpty()) {
                    Toast.makeText(context, "Unable to register! Check your internet Connection and Try again Later!", Toast.LENGTH_LONG).show();
                    //TODO: Try again dialog box
                    finish();
                }else {
                    //TODO:save on net db.
                    storeOnNetRegisteredValues();
                    Toast.makeText(context, "Successfully Registered!!!", Toast.LENGTH_LONG).show();
                    //TODO:dialog showing syncing contacts!!!
//                    syncContacts();
                }
            }
        }.execute(null, null, null);
//        ...
    }

    private void storeOnNetRegisteredValues() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msgLocal = "";
                try {
                    String serverUrl = Constants.SERVER_URL_REGISTER;
                    Map<String, String> params123 = new HashMap<String, String>();
                    String numberStored = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.pref_owner_no_key),"");
                    params123.put("contact", numberStored);
                    params123.put("registration_id", registrationIdCloudMessaging);
                    post(serverUrl, params123, MAX_ATTEMPTS);

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
                startRecentContactsActivityAndFinishMainActivity();
            }
        }.execute(null, null, null);
    }


    private static void post(String endpoint, Map<String, String> params, int maxAttempts) throws IOException {
        long backoff = BACKOFF_MILLI_SECONDS;
        String s = "";
        for (int i = 1; i <= maxAttempts; i++) {
            //Log.d(TAG, "Attempt #" + i);
            try {
                post(endpoint, params);
                return;
            } catch (IOException e) {
                //Log.e(TAG, "Failed on attempt " + i + ":" + e);
                if (i == maxAttempts) {
                    throw e;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    return;
                }
                backoff *= 2;
            } catch (IllegalArgumentException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

    }
    private static void post(String endpoint, Map<String, String> params) throws IOException {
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
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
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
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void syncContacts() {
        new AsyncTask<Void, Void, Void>() {
            Vector<ContentValues> contentValues = new Vector<ContentValues>();
            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver contentResolver = getContentResolver();
                Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);

                int i=0;
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String nameGot = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String name = nameGot.substring(0, 1).toUpperCase() + nameGot.substring(1).toLowerCase();
                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                            // get the phone number
                            Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                ContentValues values = new ContentValues();
                                String phone = pCur.getString(
                                        pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                for(int i1=0;i1<phone.length();i1++){
                                    if(phone.charAt(i1)<'0' || phone.charAt(i1)>'9'){
                                        phone = phone.substring(0,i1)+phone.substring(i1+1);
                                    }
                                }
                                if (phone.length() == 12 && phone.substring(0, 2).equals("91")) {
                                    phone = phone.substring(2);
                                }
                                if(phone.length()!=10){
                                    continue;
                                }
                                values.put(DataProvider.COL_CONTACT, phone);
                                values.put(DataProvider.COL_NAME, name);
                                contentValues.add(values);
                            }
                            pCur.close();
                        }
                    }
                }
                cur.close();
                ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
                contentValues.toArray(contentValuesArray);
                getContentResolver().bulkInsert(DataProvider.CONTENT_URI_USERS, contentValuesArray);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //TODO:dialog showing all done successfully.. .continue!!!!
            }
        }.execute();
    }
//
//    private void sendRegistrationIdToBackend() {
//        // Your implementation here.
//    }
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
//        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

}

