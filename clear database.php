<?php 
require "ini.php";
$mysql_qry = "TRUNCATE TABLE homehelper";
if($conn->query($mysql_qry) == TRUE){
	echo "clear succesfull";
}
else{
	echo "Error: ". $mysql_qry . "<br>" . $conn->error;
}
$conn-> close();
?>