package com.ritesh.chatsup;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Ritesh on 3/28/2015.
 */
public class RecentContactsActivity  extends ActionBarActivity{

    final Context mContext = this;
    private RecentChatAdapter adapter;
    public static final String CONTACT_ID = "contact_id";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
//        sendNotification("testing");
        adapter = new RecentChatAdapter(getApplicationContext());
//        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
////                int i = cursor.getInt(0);
//                view.(CONTACT_ID, i);
//                return false;
//            }
//        });
        listView = (ListView)findViewById(R.id.contact_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
//                int a = cursor.getInt(0);
                String s = cursor.getString(1);
                startChatActivity(s);
            }
        });

        LoaderManager.LoaderCallbacks<Cursor> loader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(mContext,
                        DataProvider.CONTENT_URI_JOIN,
                        null,
                        null,
                        null,
                        DataProvider.TABLE_USERS+"."+DataProvider.COL_NAME + " ASC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//                Log.e("test", "loaderfinished");
                adapter.swapCursor(data);
                if(adapter.getCount() == 0){
                    Toast.makeText(getApplicationContext(),"No recent Chats. Select a contact from Menu to Chat with!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.swapCursor(null);
            }
        };

        getLoaderManager().initLoader(0, null, loader);
    }

    public void startChatActivity(String s) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(CONTACT_ID,s);
        startActivityForResult(intent, 101);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startActivity(new Intent(this,RecentContactsActivity.class));
        finish();
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


    //    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
////        Intent intent = new Intent(this, ChatActivity.class);
////        intent.putExtra(Common.PROFILE_ID, String.valueOf(id));
////        startActivity(intent);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_chat) {
            Intent intent = new Intent(this,ContactsActivity.class);
            startActivityForResult(intent, 102);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
