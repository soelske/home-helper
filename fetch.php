<?php
$conn = mysql_connect("johnny.heliohost.org", "soelske_Bart", "Daalstraat0907PAY", "soelske_homehelper");
mysql_select_db("soelske_homehelper", $conn);
$mysql_qry = "select CONTENT from homehelper where 1";
$r = mysql_query($mysql_qry);
while($row = mysql_fetch_array($r))
{
$out[] = $row;
}
 
print(json_encode($out));
mysql_close($conn);
?>