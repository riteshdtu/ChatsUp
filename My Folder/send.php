<?php
//connection file-----------------
$db_host = "******";
$db_user = "******";
$db_psd = "******";

//establishing connection-------
$con = mysql_connect($db_host, $db_user, $db_psd) or die("error to establish connection");

//selecting database-------
mysql_select_db("******") or die("error to select database");

$msg = $_POST['msg'];
$receiver_contact = $_POST['contact'];
$sender= $_POST['sender'];
$time1  = new DateTime("now",new DateTimeZone('Asia/Kolkata'));
$time = $time1->format('Y-m-d h-i-s');
$user_res = mysql_query("select reg_id from users where contact = $receiver_contact ");
if($row_get = mysql_fetch_array($user_res)){
	$id = $row_get['reg_id'];
	sendGCM($msg,$id,$sender,$time);
}
else{
	$slashed_msg = addslashes($msg);
	mysql_query("insert into testTable(msg,receiver,sender,time) values ('$slashed_msg ','$receiver_contact','$sender','$time')") or die(mysql_error());
}


function sendGCM($msg,$id,$sender,$time){
$data = array( 'message' => $msg,
                'sender' => $sender,
                 'time' => $time );
$ids = array( $id );
sendGoogleCloudMessage( $data, $ids );
}

function sendGoogleCloudMessage( $data, $ids )
{
$apiKey = '******';

    $url = 'https://android.googleapis.com/gcm/send';

    $post = array(
                    'registration_ids'  => $ids,
                    'data'              => $data,
                    );

    $headers = array( 
                        'Authorization: key=' . $apiKey,
                        'Content-Type: application/json'
                    );

    $ch = curl_init();
    curl_setopt( $ch, CURLOPT_URL, $url );
    curl_setopt( $ch, CURLOPT_POST, true );
    curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );
    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
    curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $post ) );
    $result = curl_exec( $ch );
    if ( curl_errno( $ch ) )
    {
        echo 'GCM error: ' . curl_error( $ch );
    }
    curl_close( $ch );
    echo $result;
}
?>