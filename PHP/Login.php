<?php
    
	$con = mysqli_connect("localhost", "id4930388_ureaqa", "GoldenResults", "id4930388_ureaqa");
    
    $username = $_POST["username"];
    $thisPassword = $_POST["password"];
	
     
    $statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ?");
    mysqli_stmt_bind_param($statement, "s", $username);
    mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $user_id, $email, $name, $last_name, $username, $birthday, $weight, $password); //follow same order as table
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement)){
		if (password_verify($thisPassword, $password)){
			$response["success"] = true;  
			$response["email"] = $email;
			$response["name"] = $name;
			$response["last_name"] = $last_name;
			$response["birthday"] = $birthday;
			$response["username"] = $username;
			$response["weight"] = $weight;
		}
    }
    
    echo json_encode($response);
?>
