package com.ritesh.chatsup;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;

import java.util.Vector;

public class SyncContactsService extends Service {
    public SyncContactsService() {
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        syncContacts();
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
                stopService(new Intent(getApplicationContext(), SyncContactsService.class));
            }
        }.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
