package com.ritesh.chatsup;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ritesh on 3/28/2015.
 */
public class Constants {
    public static final  String SERVER_URL_SEND = "http://ieeedtu.com/ritesh/send.php";
    public static final  String SERVER_URL_REGISTER = "http://ieeedtu.com/ritesh/register.php";
    public static final  String SEND_MESSAGE_KEY = "send_message_key";
    public static final  String MY_MESSAGE = "my_message";
    public static final  String MY_CONTACT_ID = "my_contact_id";
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yy h:mm a");

    public static final String SENDER_ID = "388944274132";

    public static String getReadableDate(long time){
        Date date1 = new Date();
        date1.setTime(time);
        return simpleDateFormat.format(date1);
    }


}
