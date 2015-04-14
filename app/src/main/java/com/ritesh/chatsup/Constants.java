package com.ritesh.chatsup;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ritesh on 3/28/2015.
 */
public class Constants {
    public static final  String SERVER_URL_SEND = "http://ieeedtu.com/ritesh/send.php";
    public static final  String SERVER_URL_REGISTER = "http://ieeedtu.com/ritesh/register.php";
    static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("M/d/yy");
    static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("h:m a");

    public static final String SENDER_ID = "388944274132";


    public static String getReadableDate(long time){
        Date date1 = new Date();
        date1.setTime(time);
        return simpleDateFormat1.format(date1)+" "+simpleDateFormat2.format(date1);
    }


}
