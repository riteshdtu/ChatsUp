package com.ritesh.chatsup;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Ritesh on 3/27/2015.
 */
public class DataProvider extends ContentProvider {

    public static final String COL_ID = "_id";

    public static final String TABLE_USERS = "users";
    public static final String TABLE_ALL_MSGS = "all_msgs";
    public static final String TABLE_PENDING_MSGS = "pending_msgs";
    private static final String TABLE_JOIN = "users inner join all_msgs on users.contact = all_msgs.contact";

    public static final String COL_CONTACT = "contact";
    public static final String COL_MSG_COUNT = "msg_count";
    public static final String COL_NAME = "name";
    public static final String COL_MSG = "msg";
    public static final String COL_TIME = "time";
    public static final String COL_STATUS = "status";
    public static final String COL_RECEIVED = "received";
    public static final String COL_ALL_MSGS_ID= "all_msgs_id";

    private DbHelper dbHelper;

    public static final Uri CONTENT_URI_USERS = Uri.parse("content://com.ritesh.chatsup.provider/users");
    public static final Uri CONTENT_URI_JOIN = Uri.parse("content://com.ritesh.chatsup.provider/join");
    public static final Uri CONTENT_URI_ALL_MSGS = Uri.parse("content://com.ritesh.chatsup.provider/all_msgs");
    public static final Uri CONTENT_URI_PENDING_MSGS = Uri.parse("content://com.ritesh.chatsup.provider/pending_msgs");

    private static final int USERS_ALL = 1;
    private static final int USERS_SINGLE = 2;
    private static final int ALL_MSGS_ALL = 3;
    private static final int ALL_MSGS_SINGLE = 4;
    private static final int PENDING_MSGS_ALL = 5;
    private static final int PENDING_MSGS_SINGLE = 6;
    private static final int JOIN_MSG_USER = 7;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.ritesh.chatsup.provider", "users", USERS_ALL);
        uriMatcher.addURI("com.ritesh.chatsup.provider", "users/#", USERS_SINGLE);
        uriMatcher.addURI("com.ritesh.chatsup.provider", "all_msgs", ALL_MSGS_ALL);
        uriMatcher.addURI("com.ritesh.chatsup.provider", "all_msgs/#", ALL_MSGS_SINGLE);
        uriMatcher.addURI("com.ritesh.chatsup.provider", "pending_msgs", PENDING_MSGS_ALL);
        uriMatcher.addURI("com.ritesh.chatsup.provider", "pending_msgs/#", PENDING_MSGS_SINGLE);
        uriMatcher.addURI("com.ritesh.chatsup.provider", "join", JOIN_MSG_USER);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch(uriMatcher.match(uri)) {
            case USERS_ALL:
            case ALL_MSGS_ALL:
            case PENDING_MSGS_ALL:
                qb.setTables(getTableName(uri));
                break;

            case USERS_SINGLE:
            case ALL_MSGS_SINGLE:
            case PENDING_MSGS_SINGLE:
                qb.setTables(getTableName(uri));
                qb.appendWhere("_id = " + uri.getLastPathSegment());
                break;

            case JOIN_MSG_USER:
                qb.setTables(getTableName(uri));
                Cursor c = db.rawQuery("select * from all_msgs left outer join users on all_msgs.contact = users.contact group by all_msgs.contact order by time desc",null);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id;
        switch(uriMatcher.match(uri)) {
            case USERS_ALL:
                try {
                    id = db.insertOrThrow(TABLE_USERS, null, values);
                }catch (Exception e){
                    return null;
                }
                break;

            case ALL_MSGS_ALL:
                try {
                id = db.insertOrThrow(TABLE_ALL_MSGS, null, values);
                }catch (Exception e){
                    return null;

                }
                break;

            case PENDING_MSGS_ALL:
                try {
                id = db.insertOrThrow(TABLE_PENDING_MSGS, null, values);
                }catch (Exception e){
                    return null;
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Uri insertUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(insertUri, null);
        return insertUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //update will be singla ones
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case ALL_MSGS_ALL:
            case USERS_ALL:
            case PENDING_MSGS_ALL:
                count = db.update(getTableName(uri), values, selection, selectionArgs);
                break;

            case ALL_MSGS_SINGLE:
            case PENDING_MSGS_SINGLE:
            case USERS_SINGLE:
                count = db.update(getTableName(uri), values, "_id = ?", new String[]{uri.getLastPathSegment()});
                break;


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case ALL_MSGS_ALL:
            case USERS_ALL:
            case PENDING_MSGS_ALL:
                count = db.delete(getTableName(uri), selection, selectionArgs);
                break;

            case ALL_MSGS_SINGLE:
            case PENDING_MSGS_SINGLE:
            case USERS_SINGLE:
                count = db.delete(getTableName(uri), "_id = ?", new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private String getTableName(Uri uri) {
        switch(uriMatcher.match(uri)) {
            case USERS_ALL:
            case USERS_SINGLE:
                return TABLE_USERS;

            case ALL_MSGS_ALL:
            case ALL_MSGS_SINGLE:
                return TABLE_ALL_MSGS;

            case PENDING_MSGS_ALL:
            case PENDING_MSGS_SINGLE:
                return TABLE_PENDING_MSGS;

            case JOIN_MSG_USER:
                return TABLE_JOIN;
        }
        return null;
    }
}
