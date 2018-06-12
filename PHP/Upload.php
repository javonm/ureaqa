<?php
    
	$con = mysqli_connect("localhost", "id4930388_ureaqa", "GoldenResults", "id4930388_ureaqa");
    
    $username = $_POST["username"];
	$timestamp = $_POST["timestamp"];
	$hydration_lvl = $_POST["hydration_lvl"];
	$hydration_feel = $_POST["hydration_feel"];
	
	$response = array();
	$statement = mysqli_prepare($con, "INSERT INTO results (username, timestamp, hydration_lvl, hydration_feel) VALUES (?,?,?,?)");
	$response["bind"] = mysqli_stmt_bind_param($statement, "ssii", $username, $timestamp, $hydration_lvl, $hydration_feel);
	$response["success"] = mysqli_stmt_execute($statement);
	
	
	$response["username"] = $username;
	$response["timestamp"] = $timestamp;			
    $response["hydration_lvl"] = $hydration_lvl;
    $response["hydration_feel"] = $hydration_feel;
	
    echo json_encode($response);
	
?>