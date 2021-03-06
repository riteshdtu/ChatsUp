package com.ritesh.chatsup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ChatActivity extends ActionBarActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOCAL_CONTACT_ID = "local_contact_id";
    private MyChatAdapter adapter;
    int mPosition = 0;
    ListView listView;
    ShareActionProvider mShareActionProvider;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        setShareChatIntent();
        return new CursorLoader(this,
                DataProvider.CONTENT_URI_ALL_MSGS,
                new String[]{DataProvider.COL_ID, DataProvider.COL_MSG, DataProvider.COL_TIME, DataProvider.COL_CONTACT,DataProvider.COL_RECEIVED},
                DataProvider.COL_CONTACT + " = ?" ,
                new String[]{args.getString(LOCAL_CONTACT_ID)},
                DataProvider.COL_TIME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        listView.setSelection(adapter.getCount());
        if (mShareActionProvider != null) {
            setShareChatIntent();
        }
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPosition = listView.getSelectedItemPosition();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if( mPosition>0)
            listView.setSelection(mPosition);
        if (mShareActionProvider != null) {
            setShareChatIntent();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(getIntent().getStringExtra("CONTACT_NAME"));

        listView = (ListView) findViewById(R.id.msg_list);
        listView.setItemsCanFocus(true);
        final ListView listViewLocal = listView;
        listViewLocal.setDivider(null);
        adapter = new MyChatAdapter(this);

        listViewLocal.setAdapter(adapter);
        Bundle bundle = new Bundle();
        final String contact_id = getIntent().getStringExtra(ContactsActivity.CONTACT_ID);
        bundle.putString(LOCAL_CONTACT_ID, contact_id);
        getLoaderManager().initLoader(0, bundle, this);
        final ImageView button = (ImageView) findViewById(R.id.send_btn);
        final EditText editText = (EditText) findViewById(R.id.msg_edit);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewLocal.setSelection(adapter.getCount());
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = editText.getText().toString();
                if(!string.equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DataProvider.COL_MSG, string);
                    contentValues.put(DataProvider.COL_CONTACT, contact_id);

                    Intent intent = new Intent(getApplicationContext(),SendMessageService.class);
                    intent.putExtra(Constants.SEND_MESSAGE_KEY,1);
                    intent.putExtra(Constants.MY_MESSAGE,string);
                    intent.putExtra(Constants.MY_CONTACT_ID,contact_id);
                    startService(intent);
                    contentValues.put(DataProvider.COL_RECEIVED, 0);
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    long time = date.getTime();
//                    String currentTimestamp = sdf.format(new Date()); //current date
                    contentValues.put(DataProvider.COL_TIME,time);
                    getContentResolver().insert(DataProvider.CONTENT_URI_ALL_MSGS, contentValues);
//                    Intent intent = new Intent();
////                    intent.putExtra(CONTENT_RESOLVER, getContentResolver());
//                    startService(new Intent(getApplicationContext(), SendMessageService.class));
                    editText.setText("");
                    listViewLocal.setSelection(adapter.getCount());
                }
            }
        });

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
////                Toast.makeText(getApplicationContext(), "long", Toast.LENGTH_LONG).show();
////                parent.
////                showChatOptionsDialog();
//                return false;
//            }
//        });
    }

//    private void showChatOptionsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
////        builder.setTitle("Options");
//
//        ListView modeList = new ListView(this);
//        String[] stringArray = new String[] { "Share"};
//        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, R.layout.dialog_layout, R.id.text1, stringArray);
//        modeList.setAdapter(modeAdapter);
//        builder.setView(modeList);
//        final Dialog dialog = builder.create();
//        dialog.show();
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_call) {
            dialPhoneNumber(getIntent().getStringExtra(ContactsActivity.CONTACT_ID));

            return true;
        }
//        if (id == R.id.action_share) {
//           // dialPhoneNumber(getIntent().getStringExtra(ContactsActivity.CONTACT_ID));
//
//            // Get the provider and hold onto it to set/change the share intent.
////            ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();
//
////            Adapter adapter1 = listView.getAdapter();
//
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    private void setShareChatIntent(){
        StringBuilder builder = new StringBuilder();
        String other_person = getIntent().getStringExtra("CONTACT_NAME");
        for( int i=0;i<adapter.getCount();i++) {
            Cursor c = (Cursor) adapter.getItem(i);
            String msg1 = c.getString(c.getColumnIndex(DataProvider.COL_MSG));
            int received = c.getInt(c.getColumnIndex(DataProvider.COL_RECEIVED));
            if (received == 1) {
                builder.append(other_person);
                builder.append(" : ");
                builder.append( msg1);
                builder.append( "\n");
            } else {
                builder.append("Me : ");
                builder.append( msg1);
                builder.append( "\n");
            }
        }

//            Log.e("" ,builder.toString());
        mShareActionProvider.setShareIntent(createShareChatIntent(builder.toString()));
    }

    private Intent createShareChatIntent(String msg) {
        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        return intent;
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
