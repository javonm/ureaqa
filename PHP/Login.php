<?php
    
	$con = mysqli_connect("localhost", "id4930388_ureaqa", "GoldenResults", "id4930388_ureaqa");
    
    $username = $_POST["username"];
    $thisPassword = $_POST["password"];
	
    $response = array();
	$response["success"] = false;
	$response["error"] = "Unknown Error";
	
    $statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ?");
    mysqli_stmt_bind_param($statement, "s", $username);
    
	if(mysqli_stmt_execute($statement)){
		mysqli_stmt_store_result($statement);
		mysqli_stmt_bind_result($statement, $user_id, $email, $name, $last_name, $username, $birthday, $weight, $password); //follow same order as table
		
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
			else{
				$response["success"] = false;
				$response["error"] = "Incorrect Password";
				
			}
		}
		if(is_null($user_id)){
			$response["success"] = false;
			$response["error"] = "Username Not Found"; 
		}
	}    
    else{
		$response["success"] = false;
		$response["error"] = "Login Request Not Completed"; 
	}
	
    echo json_encode($response);
?>
