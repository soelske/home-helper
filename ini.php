<?php
$conn = new mysqli("johnny.heliohost.org", "soelske_Bart", "Daalstraat0907PAY", "soelske_homehelper");
if (mysqli_connect_errno()) {
printf("Connect failed: %s\n", mysqli_connect_error()); //this will print out the error while connecting to MySQL, if any
exit();
}
else{
printf("Connect succes");
}
