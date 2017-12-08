<?php
    $con = mysqli_connect("localhost", "id3773278_admin", "112358", "id3773278_petcare");
    
    $username = $_POST["username"];
    $key_number = $_POST["key"];

    $statement = mysqli_prepare($con, "INSERT INTO authorized_keys (username, key_number) VALUES (?, ?)");
    mysqli_stmt_bind_param($statement, "ss", $username, $key_number);
    mysqli_stmt_execute($statement);
    
    $response = array();
    $response["success"] = true;  
    
    echo json_encode($response);
?>