package com.ritesh.chatsup;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Ritesh on 3/29/2015.
 */

//SimpleCursorAdapter(this,
//        R.layout.main_list_item,
//        null,
//        new String[]{DataProvider.COL_NAME, DataProvider.COL_CONTACT},
//        new int[]{R.id.text_msg, R.id.text2},
//        0);
public class RecentChatAdapter extends CursorAdapter {


    Context mContext;

    RecentChatAdapter(Context context){
        super(context, null, 0);
        mContext = context;
    }

    @Override public int getCount() {
        return getCursor() == null ? 0 : super.getCount();
    }
    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String name = cursor.getString(cursor.getColumnIndex( DataProvider.COL_NAME));
        String contact = cursor.getString(1); //contact
        long time = cursor.getLong(cursor.getColumnIndex( DataProvider.COL_TIME));
//        Log.e("test", name + " " + contact+" "+cursor.getColumnName(4)+" "+cursor.getColumnName(5)+" "+cursor.getColumnName(6)+" "+cursor.getColumnName(7));
        if(name == null){
            holder.text1.setText(contact);
        }else if (name.isEmpty()) {
            holder.text1.setText(contact);
        }else {
            holder.text1.setText(name);
        }
        holder.text2.setText("Last Conversation At: " + Constants.getReadableDate(time));
    }


    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View itemLayout = LayoutInflater.from(context).inflate(R.layout.main_list_item, parent, false);
        itemLayout.setTag(holder);
        holder.text1 = (TextView) itemLayout.findViewById(R.id.text_msg);
        holder.text2 = (TextView) itemLayout.findViewById(R.id.text2);
        return itemLayout;
    }
    private static class ViewHolder {
        TextView text1;
        TextView text2;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
    //    @Override
//    public int getItemViewType(int position) {
//        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return VIEW_TYPE_COUNT;
//    }
}
