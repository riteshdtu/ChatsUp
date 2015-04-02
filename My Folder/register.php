<?php
//connection file-----------------
$db_host = "******";
$db_user = "******";
$db_psd = "******";

//establishing connection-------
$con = mysql_connect($db_host, $db_user, $db_psd) or die("error to establish connection");

//selecting database-------
mysql_select_db("******") or die("error to select database");

$contact= $_POST['contact'];
$registration_id= $_POST['registration_id'];
$slashed_id = addslashes($registration_id);

$res = mysql_query("select * from users where contact = $contact");
if($row=mysql_fetch_array($res)){
	mysql_query("UPDATE `users` SET `reg_id` = '$registration_id' WHERE `contact` = '$contact';");
}
else{
	mysql_query("INSERT INTO users ( `contact`, `reg_id`) VALUES ( '$contact', '$registration_id')") or die(mysql_error());
	
	$resource = mysql_query("SELECT msg,pk,sender,time FROM `testTable` WHERE `receiver` = '$contact'");
	while($row=mysql_fetch_array($resource)){
	    $msg1 = $row['msg'];
	    $pk = $row['pk'];
	    $sender = $row['sender'];
	    $time = $row['time'];
	    mysql_query("delete from testTable where pk = $pk");
	    sendGCM($msg1,$registration_id,$sender,$time);
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
}
?>