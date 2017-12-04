<?php
    $con = mysqli_connect("localhost", "id3773278_admin", "112358", "id3773278_petcare");
    
    $username = $_POST["username"];
    
    $statement = "SELECT key_number FROM authorized_keys WHERE username ='".$username."'";
    $res = mysqli_query($con,$statement);
    
    $result = array();

    while ($row = mysqli_fetch_array($res)){
        array_push($result,array('key'=>$row['0']));
    }
    echo json_encode(($result));

    mysqli_close($con);
?>