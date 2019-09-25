<html>
	<head>
		<title>The Bank</title>
	</head>

	<body>
		<!-- Welcome Message -->
		<h1>Welcome to the Bank!</h1>

		<!-- PHP Script -->
		<?php 

		// Connect to DB
		$dbServer = "127.0.0.1";
		$username = "constan7";
		$password = "7686561";
		$dbName = "constan7";

		$conn = mysql_connect($dbServer, $username, $password);
		mysql_select_db($dbName);

		// Check connection
		if (!$conn) {
		    die("Connection failed: " . mysqli_connect_error());
		}

		// Acquire command
		$command = $_POST["command"];
		
		// Perform action based on command
		switch($command){

			case "Create Account":

				// Acquire parameters
				$acctNum = $_POST["create_acct_num"];

				// Check if account exists
				$sql = "SELECT * FROM constan7_BANKACCOUNTS where accountNum = " . $acctNum;
				$result_set = mysql_query($sql, $conn);
				$exists = TRUE;
				if(mysql_num_rows($result_set) > 0) {
				    while($row = mysql_fetch_array($result_set, MYSQL_ASSOC)) {
				        echo "Account number ". $acctNum . " already exists with a balance of $" . $row["balance"] . ".<br>";
				    }
				} else {
					$exists = FALSE;
				}

				// Insert into DB using posted account number and balance = 0
				if(!$exists){
					$sql = "INSERT INTO constan7_BANKACCOUNTS VALUES (" . $acctNum . ", 0);";
					$result_set = mysql_query($sql, $conn);
					if ($result_set === TRUE) {
					    echo "Account number " . $acctNum . " successfully created.<br>";
					} else {
					    echo "Error: " . $sql . "<br>" . $conn->error;
					}
				}

				echo "<br>Please go back to make another selection.<br>";

			   	mysql_close($conn);

				break;

			case "Retrieve Balance":

				// Acquire parameters
				$acctNum = $_POST["retrieve_acct_num"];

				// Retrieve balance from given account number
				$sql = "SELECT balance FROM constan7_BANKACCOUNTS where accountNum = " . $acctNum;
				$result_set = mysql_query($sql, $conn);
				if(mysql_num_rows($result_set) > 0) {
				    while($row = mysql_fetch_array($result_set, MYSQL_ASSOC)) {
				        echo "The balance in account number ". $acctNum . " is $" . $row["balance"] . ".<br>";
				    }
				} else {
					echo "No account information found for account number " . $acctNum . ".<br>";
				}

				echo "<br>Please go back to make another selection.<br>";

			   	mysql_close($conn);

				break;

			case "Deposit Amount":
				
				// Acquire parameters
				$acctNum = $_POST["deposit_acct_num"];
				$amount = $_POST["deposit_amount"];

				// Retrieve balance from given account number
				$sql = "SELECT balance FROM constan7_BANKACCOUNTS where accountNum = " . $acctNum;
				$result_set = mysql_query($sql, $conn);
				$exists = TRUE;
				if(mysql_num_rows($result_set) > 0) {
				    while($row = mysql_fetch_array($result_set, MYSQL_ASSOC)) {
				        $balance = $row["balance"];
				    }
				} else {
					echo "No account information found for account number " . $acctNum . ".<br>";
					$exists = FALSE;
				}
			    
			    // Check if new balance and account number are sane
			    if ($exists){

				    // Set new balance
				    $newBalance = $balance + $amount;

				    if ($amount > 0){
						// Update DB with new balance
						$sql = "UPDATE constan7_BANKACCOUNTS SET balance = " . $newBalance . "  WHERE accountNum = " . $acctNum;
						$result_set = mysql_query($sql, $conn);
						if ($result_set === TRUE) {
						    echo "Account number " . $acctNum . " went from balance of $" . $balance . " to a new balance of $" . $newBalance . ".<br>";
						} else {
						    echo "Error: " . $sql . "<br>" . $conn->error;
						}
					} else {
						echo "Please enter a positive amount to deposit.<br>";
					}
				}

				echo "<br>Please go back to make another selection.<br>";

			   	mysql_close($conn);

				break;

			case "Withdraw Amount":

				// Acquire parameters
				$acctNum = $_POST["withdraw_acct_num"];
				$amount = $_POST["withdraw_amount"];

				// Retrieve balance from given account number
				$sql = "SELECT balance FROM constan7_BANKACCOUNTS where accountNum = " . $acctNum;
				$result_set = mysql_query($sql, $conn);
				$exists = TRUE;
				if(mysql_num_rows($result_set) > 0) {
				    while($row = mysql_fetch_array($result_set, MYSQL_ASSOC)) {
				        $balance = $row["balance"];
				    }
				} else {
					echo "No account information found for account number " . $acctNum . ".<br>";
					$exists = FALSE;
				}

			    // Check if new balance and account number are sane
			    if ($exists){
			    
				    // Set new balance
				    $newBalance = $balance - $amount;

				    if ($amount > 0){
					    if ($newBalance >= 0){
							// Update DB with new balance
							$sql = "UPDATE constan7_BANKACCOUNTS SET balance = " . $newBalance . "  WHERE accountNum = " . $acctNum;
							$result_set = mysql_query($sql, $conn);
							if ($result_set === TRUE) {
							    echo "Account number " . $acctNum . " went from balance of $" . $balance . " to a new balance of $" . $newBalance . ".<br>";
							} else {
							    echo "Error: " . $sql . "<br>" . $conn->error;
							}
					    } else {
					    	echo "Account number " . $acctNum . " only has a balance of $" . $balance . " and cannot withdraw $" . $amount . ".<br>";
						}
					} else {
						echo "Please enter a positive amount to withdraw.<br>";
					}
				}

				echo "<br>Please go back to make another selection.<br>";

			   	mysql_close($conn);

				break;

			case "Show Accounts":

				// Retrieve all acounts and print
				$sql = "SELECT * FROM constan7_BANKACCOUNTS";
				$result_set = mysql_query($sql, $conn);
			    while($row = mysql_fetch_array($result_set, MYSQL_ASSOC)) {
		    		echo "Account Number: " . $row["accountNum"] . "	Balance: $" . $row["balance"] . "<br>";
			    }

				echo "<br>Please go back to make another selection.<br>";

			   	mysql_close($conn);
			
				break;
		}

		?>
	</body>
</html>