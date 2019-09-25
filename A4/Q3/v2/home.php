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

		// Respond with server ID and name
		if($_POST["serverList"]){
			$servInfo = array();
			$servInfo = [];
			for($i = 0; $i < count($servers); $i++){

				$servInfo[] = $servers[$i][0];
				$servInfo[] = $servers[$i][2];
			}

			$myJSON = json_encode($servInfo);
			echo $myJSON;
		}

		/******************** Format List *********************/

		// Respond with encoding formats
		$formatInfo = array();
		$formatInfo = [];
		if($_POST["formatList"]){
			for($i = 0; $i < count($formats)/2; $i++){
				$formatInfo[] = $formats[$i*2+1];
			}

			$myJSON = json_encode($formatInfo);
			echo $myJSON;
		}


		/******************** Video List *********************/

		// Acquire the serverID and formatID parameters from AJAX POST
		$servID = $_POST["selectedServer"];
		$format = $_POST["selectedFormat"];

		// Respond with video list according to selected server and format
		$serverIndex = $servID-1;
		$found = False;
		$videoList = array();
		$videoList = [];
		if($servID && $format){
			for($i = 0; $i < count($videos[$serverIndex]); $i+=4) {
				if ($format == $videos[$serverIndex][$i+3]){
					$videoList[] = $videos[$serverIndex][$i];
					$videoList[] = $videos[$serverIndex][$i+1];
					$found = True;
				}
			}

			if ($found) {
				$myJSON = json_encode($videoList);
				echo $myJSON;
			} else {
				$myJSON = json_encode(null);
				echo $myJSON;
			}
		}

		/******************** Video Details *********************/

		// Acquire the videoID parameters from AJAX POST
		$selectedVideo = $_POST['selectedVideo'];

		// Respond with selected video details
		$videoDetails = array();
		$videoDetails = [];
		if($selectedVideo){
			for($i = 0; $i < count($videos); $i++){
				for($j = 0; $j < count($videos[$i]); $j+=4){
					if ($videos[$i][$j] == $selectedVideo){
						$videoDetails[] = $videos[$i][$j+1];
						$videoDetails[] = $servers[$i][1];
						$videoDetails[] = $servers[$i][2];
						$videoDetails[] = $servers[$i][3];
						$videoDetails[] = $videos[$i][$j+2];
						$videoDetails[] = $videos[$i][$j+3];
					}
				}
			}

			$myJSON = json_encode($videoDetails);
			echo $myJSON;
		}

?>