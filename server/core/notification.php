<?php

class Notification {
	private $mqtt;

	function __construct($mqtt) {
		$this->mqtt = $mqtt;
	}

	function push($user, $msg) {
		$topic = PUSH_CLIENT_ID.'/'.$user;
		if($this->mqtt->connect()) {
			$this->mqtt->publish($topic, $msg, 0);
			$this->mqtt->close();
		}
	}
}

?>