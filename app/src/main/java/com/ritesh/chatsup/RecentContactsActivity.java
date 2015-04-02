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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ritesh on 3/28/2015.
 */
public class RecentContactsActivity  extends ActionBarActivity{

    final Context mContext = this;
    private RecentChatAdapter adapter;
    public static final String CONTACT_ID = "contact_id";
    ListView listView;
    int mPosition = 0;

    @Override
    protected void onPause() {
        super.onPause();
        mPosition = listView.getSelectedItemPosition();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if( mPosition>0)
            listView.setSelection(mPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setTitle("ChatsUp");
        adapter = new RecentChatAdapter(getApplicationContext());
        listView = (ListView)findViewById(R.id.contact_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                String s = cursor.getString(1);
                String name = ((TextView)view.findViewById(R.id.text_msg)).getText().toString();
                startChatActivity(s,name);
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

    public void startChatActivity(String s,String name) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(CONTACT_ID,s);
        intent.putExtra("CONTACT_NAME", name);
        startActivityForResult(intent, 101);
    }

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
