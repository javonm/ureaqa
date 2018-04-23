<?php
    
	$con = mysqli_connect("localhost", "id4930388_ureaqa", "GoldenResults", "id4930388_ureaqa");
    
    $username = $_POST["username"];
	$send_receive = $_POST["send_receive"];
	//$timestamp = $_POST["timestamp"];
	//$hydration_lvl = $_POST["hydration_lvl"];
	
	
	//$statement = mysqli_prepare($con, "INSERT INTO results (username, timestamp, hydration_lvl) VALUES (?,?,?)");
	//mysqli_stmt_bind_param($statement, "ssi", $username, $timestamp, $hydration_lvl);
	//mysqli_stmt_execute($statement);
	
	
	//$sql_string = "SELECT * FROM ( SELECT username, timestamp, hydration_lvl FROM results WHERE username = '" . $username . "' ORDER BY timestamp DESC ) as t1 GROUP BY hydration_lvl LIMIT 1";
	$sql_string = "SELECT username, timestamp, hydration_lvl FROM results WHERE username = '" . $username . "' ORDER BY timestamp DESC LIMIT 1";
	$statement = mysqli_prepare($con, $sql_string);
	//mysqli_stmt_store_result($statement);
	
	mysqli_stmt_execute($statement);

    /* bind variables to prepared statement */
    mysqli_stmt_bind_result($statement, $username, $timestamp, $hydration_lvl); //follow same order as table
    

    /* fetch values */
    mysqli_stmt_fetch($statement);

   
        
    $response = array();
	$response["string"] = $sql_string;
	
	if (is_null($hydration_lvl)){
		$response["success"] = false; 
	}
	elseif (is_null($timestamp)){
		$response["success"] = false;
	}
	else {
		$response["success"] = true;
		$response["hydration_lvl"] = $hydration_lvl;
		$response["timestamp"] = $timestamp;
		$response["username"] = $username;
	}	   
		
     /* close statement */
    mysqli_stmt_close($statement);
	/* close connection */
	mysqli_close($con);
    
    echo json_encode($response);
	
?>