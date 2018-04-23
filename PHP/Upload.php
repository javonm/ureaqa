<?php
    
	$con = mysqli_connect("localhost", "id4930388_ureaqa", "GoldenResults", "id4930388_ureaqa");
    
    $username = $_POST["username"];
	$timestamp = $_POST["timestamp"];
	$hydration_lvl = $_POST["hydration_lvl"];
	
	$response = array();
	$statement = mysqli_prepare($con, "INSERT INTO results (username, timestamp, hydration_lvl) VALUES (?,?,?)");
	$response["bind"] = mysqli_stmt_bind_param($statement, "ssi", $username, $timestamp, $hydration_lvl);
	$response["success"] = mysqli_stmt_execute($statement);
	
	
	$response["username"] = $username;
	$response["timestamp"] = $timestamp;			
    $response["hydration_lvl"] = $hydration_lvl;
    
    echo json_encode($response);
	
?>