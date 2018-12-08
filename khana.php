<?php 
//echo "assdd";

require("db_connect.php");
//echo "assdd";
if ( isset($_POST['khana']))
{       
        //echo $_GET['khana'];
        $data= mysqli_query($con,$_POST['khana']) or die("failed");
      while( $row = mysqli_fetch_assoc($data)){
         foreach($row as $key => $value)
	    {
		echo $value.",";
	    }
        echo "&";
        }
}
?>

