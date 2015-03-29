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
public class MyChatAdapter extends CursorAdapter {

//    private static final int VIEW_TYPE_COUNT = 2;
//    private static final int VIEW_TYPE_SENT_MSG = 0;
//    private static final int VIEW_TYPE_RECEIVED_MSG = 1;

    Context mContext;

    MyChatAdapter(Context context){
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
        String msg = cursor.getString(cursor.getColumnIndex(DataProvider.COL_MSG));
        long time = cursor.getLong(cursor.getColumnIndex(DataProvider.COL_TIME));

        holder.text1.setText(msg);
        holder.text2.setText(Constants.getReadableDate(time));
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
        View itemLayout = null;
        switch(getItemViewType(cursor)){
            case 0:
                itemLayout = LayoutInflater.from(context).inflate(R.layout.chat_list_item_msg_sent, parent, false);
                break;
            case 1:
                itemLayout = LayoutInflater.from(context).inflate(R.layout.chat_list_item_msg_received, parent, false);
                break;
        }
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
        return 2;
    }

    @Override
    public int getItemViewType(int _position) {
        Cursor cursor = (Cursor) getItem(_position);
        return getItemViewType(cursor);
    }

    private int getItemViewType(Cursor _cursor) {
        int typeIdx = _cursor.getColumnIndex(DataProvider.COL_RECEIVED);
        return _cursor.getInt(typeIdx);
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
