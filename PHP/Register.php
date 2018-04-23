<?php
   
	$con = mysqli_connect("localhost", "id4930388_ureaqa", "GoldenResults", "id4930388_ureaqa");
    
	$email = $_POST["email"];
    $name = $_POST["name"];
	$last_name = $_POST["last_name"];
    $username = $_POST["username"];
    $birthday	= $_POST["birthday"];
    $weight = $_POST["weight"];
	$password = $_POST["password"];
	
	$count;


	
    
	
    $response = array();
    $response["success"] = false;
	
	
	if (usernameAvailable()){
        
		$response["success"] = true;
		$response["error"] = $count;
		
		$password = password_hash($password, PASSWORD_BCRYPT);
		$statement = mysqli_prepare($con, "INSERT INTO user (email, name, last_name, username, birthday, weight, password) VALUES (?,?,?,?,?,?,?)");
		mysqli_stmt_bind_param($statement, "sssssis", $email, $name, $last_name, $username, $birthday, $weight, $password);
		$respose["success"] = mysqli_stmt_execute($statement);
			
		
          
    }	
    
    echo json_encode($response);
	
	
	
	function usernameAvailable() {
		global $con, $username, $count;
        $statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ?"); 
        mysqli_stmt_bind_param($statement, "s", $username);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
         
        if ($count < 1){
            $error = "we're not sure what happened";
			$response["error"] = $error;
			return true;
        }
		else { 
			$error = "Username Not Available";
			$response["error"] = $error;
            return false; 
        }
	}
	
		

?>
