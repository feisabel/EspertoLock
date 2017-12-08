<?php
    $con = mysqli_connect("localhost", "id3773278_admin", "112358", "id3773278_petcare");
    
    $username = $_POST["username"];
    $key_number = $_POST["key"];
    $time = $_POST["time"];

    $statement = mysqli_prepare($con, "INSERT INTO history (username, key_number, time) VALUES (?, ?, ?)");
    mysqli_stmt_bind_param($statement, "sss", $username, $key_number, $time);
    mysqli_stmt_execute($statement);
    
    $response = array();
    $response["success"] = true;  
    
    echo json_encode($response);
?>