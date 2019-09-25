<html>
	<head>
		<Title>Ultimate Video Repository</Title>
		<link rel='stylesheet'  type='text/css' href='home.css'>
	</head>
	<body>
		<!-- PHP Script -->
		<?php

		/******************** Set up *********************/

		// Array to hold server and server info
		$servers = array(
			array(1, 'www.siteA.com', 'SiteA', 'Great'),
			array(2, 'www.siteB.com', 'SiteB', 'Good'), 
			array(3, 'www.siteC.com', 'SiteC', 'Ok'),
			array(4, 'www.siteD.com', 'SiteD', 'Awful'));

		// Array to holds video and video info
		$videos = array(
			array(1, 'Movie1', '50', 'mp4', 2, 'Movie2', '90', 'avi', 3, 'Movie3', '10', 'mov', 4, 'Movie4', '220', 'wmv'),
			array(5, 'Movie5', '100', 'mp4', 6, 'Movie6', '320', 'mov', 7, 'Movie7', '70', 'avi', 8, 'Movie8', '200', 'mp4'),
			array(9, 'Movie9', '250', 'wmv', 10, 'Movie10', '280', 'avi', 11, 'Movie11', '130', 'avi', 12, 'Movie12', '50', 'wmv'),
			array(13, 'Movie13', '50', 'mov', 14, 'Movie14', '80', 'avi', 15, 'Movie15', '30', 'mov', 16, 'Movie16', '140', 'avi'));

		// Enumerate formats
		$formats = array(1, 'mp4', 2, 'avi', 3, 'mov', 4, 'wmv');

		
		/******************** Server List *********************/

		// Acquire the serverList parameter from AJAX POST
		$serverList = $_POST['serverList'];

		// Respond with list of servers
		if ($serverList){
			for ($i = 0; $i < count($servers); $i++){
				$str = "'" . $servers[$i][2] . "'";
				echo '<div onclick="selectServer(' . $servers[$i][0] . ', ' . $str . ')" class="serverOption">' . $servers[$i][2] . '</div>';				
			}
		}

		/******************** Format List *********************/
		
		// Acquire the serverList parameter from AJAX POST
		$formatList = $_POST['formatList'];

		// Respond with list of servers
		if ($formatList){
			for ($i = 0; $i < count($formats); $i++){
				if($i % 2 == 1){
					$str = "'" . $formats[$i] . "'";
					echo '<div onclick="selectFormat(' . $str . ')" class="formatSelect" id="formatOption">' . $formats[$i] . ' </div>';
				}				
			}
		}


		/******************** Video List *********************/

		// Acquire the serverID and formatID parameters from AJAX POST
		$selectedServer = $_POST['selectedServer'];
		$selectedFormat = $_POST['selectedFormat'];

		// Respond with videos from correct server and format
		$serverIndex = $selectedServer - 1;
		$found = False;
		for ($i = 0; $i < count($videos[$serverIndex]); $i+=4){
			if ($videos[$serverIndex][$i+3] == $selectedFormat){
				echo '<li onclick="selectVideo(' . $videos[$serverIndex][$i] . ')" class="videoOption">' . $videos[$serverIndex][$i+1] . '</li>';
				$found = True;
			}
		}

		// Respond if no videos found
		if($selectedServer && $selectedFormat && !$found){
			echo "<div>Cannot find any videos -- please make another selection.</div>";
		}

		/******************** Video Details *********************/

		// Acquire the videoID parameters from AJAX POST
		$selectedVideo = $_POST['selectedVideo'];

		// Respond with correct movie details
		for ($i = 0; $i < count($videos); $i++) {
			for ($j = 0; $j < count($videos[$i]); $j+=4) {
				if ($videos[$i][$j] == $selectedVideo){
					echo '<div class="videoDetail" id="videoTitle">' . $videos[$i][$j+1] . '</div>';
					echo '<div class="videoDetail">' . $servers[$i][1] . '</div>';
					echo '<div class="videoDetail">' . $servers[$i][2] . '</div>';
					echo '<div class="videoDetail">' . $servers[$i][3] . '</div>';
					echo '<div class="videoDetail">' . $videos[$i][$j+2] . ' minutes</div>';
					echo '<div class="videoDetail">' . $videos[$i][$j+3] . '</div>';
				}
			}
		}

		?>
	</body>
</html>