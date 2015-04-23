<?php

abstract class Pool {

}

class SessionPool extends Pool {

	private $mem;

	function __construct($mem) {
		$this->mem = $mem;
	}

	function set($user, $token, $ip, $id) {
		return $this->mem->set($user, array('token' => $token, 'ip' => $ip, 'id' => $id));
	}

	function get($user) {
		return $this->mem->get($user);
	}

	function getToken($user) {
		return $this->mem->get($user)['token'];
	}

	function getIp($user) {
		return $this->mem->get($user)['ip'];
	}

	function getId($user) {
		return $this->mem->get($user)['id'];
	}

	function delete($user) {
		return $this->mem->delete($user);
	}
}

class LocationPool extends Pool {

	const KEY_INDEX = 'keyindex';
	const PREFIX = 'loc_'; //avoid conflicting with other pool
	private $mem;

	function __construct($mem) {
		$this->mem = $mem;
		$this->mem->set(self::KEY_INDEX, array());
	}

	// $loc should be an array(latitude, longitude)
	function set($user, $loc) {
		// set
		$ret = $this->mem->set(self::PREFIX.$user, $loc);
		if(!$ret) {
			return false;
		}
		// update key index
		$key = $this->mem->get(self::KEY_INDEX);
		$key[] = $user;
		$ret = $this->mem->set(self::KEY_INDEX, $key);
		return $ret;
	}

	function get($user) {
		return $this->mem->get(self::PREFIX.$user);
	}

	function delete($user) {
		// delete
		$ret = $this->men->delete(self::PREFIX.$user);
		if(!$ret) {
			return false;
		}
		// update key index
		$key = $this->mem->get(self::KEY_INDEX);
		$key = array_diff($key, array($user));
		$ret = $this->mem->set(self::KEY_INDEX, $key);
		return $ret;
	}

	function findNear($user, $range) {

		$user_loc = $this->mem->get(self::PREFIX.$user);
		if(!$user_loc) {
			return false;
		}

		$ret = array();
		$key = $this->mem->get(self::KEY_INDEX);
		foreach ($key as $k) {
			if($k == $user) {
				continue;
			}
			$loc = $this->mem->get(self::PREFIX.$k);
			$offset_lati = abs($user_loc[0] - $loc[0]);
			$offset_long = abs($user_loc[1] - $loc[1]);
			if($offset_lati < $range && $offset_long < $range) {
				$ret[] = $k;
			}
		}
		return $ret;
	}

}

?>