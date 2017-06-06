<?php
require "ini.php";
$userid = $_POST["userid"];
$content= $_POST["content"];
$mysql_qry = "insert into homehelper (USERID, CONTENT) 
values ('$userid', '$content')";
if($conn->query($mysql_qry) == TRUE){
	echo "insert succesfull";
}
else{
	echo "Error: ". $mysql_qry . "<br>" . $conn->error;
}
$conn-> close();
?>