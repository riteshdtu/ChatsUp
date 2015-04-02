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
import android.widget.SimpleCursorAdapter;


public class ContactsActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final Context mContext = this;
    private SimpleCursorAdapter adapter;
    public static final String CONTACT_ID = "contact_id";
    int mPosition = 0;
    ListView listView;

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                DataProvider.CONTENT_URI_USERS,
                new String[]{ DataProvider.COL_ID, DataProvider.COL_CONTACT, DataProvider.COL_NAME},
                DataProvider.COL_STATUS + " = 0 ",
                null,
                DataProvider.COL_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setTitle("Select Contact");
        adapter = new SimpleCursorAdapter(this,
                R.layout.main_list_item,
                null,
                new String[]{DataProvider.COL_NAME, DataProvider.COL_CONTACT},
                new int[]{R.id.text_msg, R.id.text2},
                0);
        listView = (ListView)findViewById(R.id.contact_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
//                int a = cursor.getInt(0);
                String s = cursor.getString(1);
                String name = cursor.getString(cursor.getColumnIndex(DataProvider.COL_NAME));
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(CONTACT_ID, s);
                intent.putExtra("CONTACT_NAME", name);
                startActivityForResult(intent, 101);
            }
        });
        getLoaderManager().initLoader(0, null, this);
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
        finish();
    }



}
