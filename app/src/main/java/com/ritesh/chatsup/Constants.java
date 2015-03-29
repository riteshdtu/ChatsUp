package com.ritesh.chatsup;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ritesh on 3/28/2015.
 */
public class Constants {
    public static final  String SERVER_URL_SEND = "http://ieeedtu.com/ritesh/send.php";
    public static final  String SERVER_URL_REGISTER = "http://ieeedtu.com/ritesh/register.php";

    public static final String SENDER_ID = "388944274132";


    public static String getReadableDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.toLocalizedPattern();
        Date date1 = new Date();
        date1.setTime(time);
        return simpleDateFormat.format(date1);
    }

}
