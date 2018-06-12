<?php
    //mysqli_report(MYSQLI_REPORT_ALL);
	$con = mysqli_connect("localhost", "id4930388_ureaqa", "GoldenResults", "id4930388_ureaqa");
    
    $username = $_POST["username"];
    $thisPassword = $_POST["password"];
	$new_value = $_POST["new_value"];
	$bool_pw = (bool) $_POST["bool_pw"]; //true means change pw, false means change weight
	
	
	$response = array();
    $response["success"] = false;  
	$response["error"] = "Unexpected error";
	$response["bool"] = $bool_pw;
	
	//$statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ?");
    //mysqli_stmt_bind_param($statement, "s", $username);
    //mysqli_stmt_execute($statement);
    
    //mysqli_stmt_store_result($statement);
    //mysqli_stmt_bind_result($statement, $user_id, $email, $name, $last_name, $username, $birthday, $weight, $password);
	//$response["id"] = $user_id;
	
	if ($bool_pw==true){
		if (password_verify($thisPassword, $password)){
			$new_value = password_hash($new_value, PASSWORD_BCRYPT);
			$string = "UPDATE 'user' SET 'password' = `".$new_value."` WHERE 'user'.'user_id' = ".$user_id;
			$statement = mysqli_prepare($con, $string);
			//mysqli_stmt_bind_param($statement, "ss", $new_value, $username);
			$respose["success"] = mysqli_stmt_execute($statement);

		}
		else{
			$response ["error"] = "Password Incorrect";
			$response ["success"] = false;
		}
	} 
	else{
		$string = "UPDATE user SET weight = ? WHERE 'user'.'user_id' = ?";
		$stmt = mysqli_prepare($con, "UPDATE user SET weight = ? WHERE username = ?");
		$response["stmt"] = $stmt;
		mysqli_stmt_bind_param($stmt, "ss", $new_value, $username);
		$respose["success"] = mysqli_stmt_execute($stmt);
		 if ($response["success"]==false){
			 $response["error"] = "Couldn't update weight";
			 
			 $response["weight"] = $new_value;
			  
		 }
			 
	}
    
    
    echo json_encode($response);
?>
