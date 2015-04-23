<?php

class Processor {

	private $db;
	private $sess;
	private $loc;
	private $noti;
	private $error_msg;
	private $data;

	private $user;

	const DELIMITER = '|';
	const AUTH_DELIMITER = ':';
	private static $PUSH_BACK = array(
		'NEW_STATUS' => '
		{
			"type":"NEW_STATUS"
		}',
		'NEW_FOLLOW' => '
		{
			"type":"NEW_FOLLOW",
			"user": "%s"
		}'
	);

	private function validation() {
		return false;
	}

	private function pwd_hash($str) {
		// return sha1($str);
		return $str;
	}

	private function gen_token($user) {
		// return md5($user.mt_rand(1, 9).time());
		return '123456';
	}

	function __construct($db, $sess, $loc, $noti){
		$this->db = $db;
		$this->sess = $sess;
		$this->loc = $loc;
		$this->noti = $noti;

		$this->error_msg = '';
		$this->data = '';

		$this->user = '';
	}

	function error_msg() {
		return $this->error_msg;
	}

	function get_data() {
		return $this->data;
	}

	function registration($user, $pwd, $nickname) {

		// check if the user already exists
		$this->db->where('user', $user);
		if(count($this->db->get('users')) > 0) {
			$this->error_msg = 'user exists';
			return false;
		}

		// insert user
		$id = $this->db->insert('users', array(
			'user' => $user,
			'password' => $this->pwd_hash($pwd),
			'nickname' => $nickname
		));
		if($id) {
			return true;
		} else {
			$this->error_msg = $this->db->getLastError();
			return false;
		}
	}

	function sign_in($user, $pwd, $ip) {

		// // already signed in
		// $this->sess->get($user);
		// if($this->sess->get($user) != false) {
		// 	$this->error_msg = 'already signed in';
		// 	return false;
		// }

		// sign in
		$this->db->where('user', $user);
		$this->db->where('password', $pwd);
		$users = $this->db->get('users');
		if($this->db->count > 0) {
			$token = $this->gen_token($user);
			$this->sess->set($user, $token, $ip, $users[0]['id']);
			$this->data = array('token' => $token);
			return true;
		} else {
			$this->error_msg = 'wrong username or passowrd';
			return false;
		}
	}

	function is_valid_token() {

		$headers = apache_request_headers();
		if(!isset($headers['Authorization'])) {
			$this->error_msg = 'token is missing';
			return false;
		}

        $auth = explode(self::AUTH_DELIMITER, $headers['Authorization']);
        $user = $auth[0];
        $token = $auth[1];

		// not signed in
		if($this->sess->get($user) == false) {
			$this->error_msg = 'not signed in';
			return false;
		}

		// check token
		if($this->sess->getToken($user) == $token) {
			$this->user = $user;
			return true;
		}
		$this->error_msg = 'unauthorized';
		return false;
	}

	function sign_out() {

		if(!$this->is_valid_token())
			return false;
		
		$this->sess->delete($this->user);
		return true;
	}

	function update_info($nickname) {

		if(!$this->is_valid_token())
			return false;

		$this->db->where('user', $this->user);
		$id = $this->db->update('users', array('nickname' => $nickname));
		return true;
	}

	private function array_to_dbitem($arr) {
		return implode(self::DELIMITER, $arr);
	}

	private function dbitem_to_array($item) {
		return explode(self::DELIMITER, $item);
	}

	function create_status($content, $anonymous, $temperature, $location) {

		if(!$this->is_valid_token())
			return false;

		$temperature = ($temperature == null) ? '' : $temperature;
		// location should be an array or null
		if(is_array($location)) {
			$location = $this->array_to_dbitem($location);
		} else if($location == null) {
			$location = '';
		} else {
			$this->error_msg = 'invalid location';
			return false;
		}

		$date = time();
		$id = $this->db->insert('statuses', array(
			'user_id' => $this->sess->getId($this->user),
			'content' => $content,
			'date' => $date,
			'anonymous' => $anonymous,
			'temperature' => $temperature,
			'location' => $location
		));
		if($id) {
			$this->data = array('id' => $id, 'date' => $date);

			// notify subscribe
			$follows_user = $this->get_followed_by_user($this->user);
			foreach ($follows_user as $f) {
				$this->noti->push($f, self::$PUSH_BACK['NEW_STATUS']);
			}

			return true;
		} else {
			$this->error_msg = $this->db->getLastError();
			return false;
		}
	}

	function delete_status($id) {

		if(!$this->is_valid_token())
			return false;

		$this->db->where('id', $id);
		$this->db->where('user_id', $this->sess->getId($this->user));
		$ret = $this->db->delete('statuses');

		if($ret) {
			return true;
		} else {
			$this->error_msg = $this->db->getLastError(); // normally, not this user's status
			return false;
		}
	}

	function get_statuses($type, $amount) {
		
		if(!$this->is_valid_token())
			return false;

		$follows_id = $this->get_follows_id($this->user);

		switch($type) {
			case 'normal':
				if(count($follows_id) > 0) {
					$this->db->where('user_id', $follows_id, 'IN');
					$this->db->where('anonymous', false);
				}
				break;
			case 'anonymous':
				$this->db->where('anonymous', true);
				break;
			default:// unconstrained (to anonymous)
				if(count($follows_id) > 0) {
					$this->db->where('user_id', $follows_id, 'IN');
				}
				$this->db->orwhere('anonymous', true);
		}

		// include statuses of himself
		$this->db->orwhere('user_id', $this->sess->getId($this->user));
		// query
		$this->db->join("users u", "s.user_id=u.id", "LEFT");
		$this->db->orderBy('date', 'desc');
		$statuses = $this->db->get('statuses s', $amount, 's.id, u.user, u.nickname, s.content, s.date, s.anonymous, s.temperature, s.location');

		if(!$statuses && count($statuses) != 0) {
			$this->error_msg = $this->db->getLastError();
			return false;
		}
		
		$ret_arr = array();
		foreach ($statuses as $s) {
			$loc = $s['location'];
			if($loc != '') { 
				$loc = $this->dbitem_to_array($loc);
				$loc = array(floatval($loc[0]), floatval($loc[1]));
			} else {
				$loc = null;
			}

			$temp = $s['temperature'];
			if($temp == '') {
				$temp = null;
			}

			$ret_arr[] = array(
				'id' => $s['id'],
				'author' => $s['user'],
	            'author_nickname' => $s['nickname'],
	            'content' => $s['content'],
	            'date' => intval($s['date']),
	            'anonymous' => $s['anonymous'],
	            'temperature' => $temp,
	            'location' => $loc
			);
		}
		$this->data = array('statuses' => $ret_arr);
		return true;
	}

	function add_follow($follow) {

		if(!$this->is_valid_token())
			return false;

		// follow can not be himself
		if($follow == $this->user) {
			$this->error_msg = 'you can not follow yourself';
			return false;
		}

		// get follow user id
		$this->db->where('user', $follow);
		$follow_id = $this->db->get('users');
		if(count($follow_id) <= 0) {
			$this->error_msg = 'follow user not exists';
			return false;
		}
		$follow_id = $follow_id[0]['id'];

		// insert follow
		$ret = $this->db->insert('follows', array(
			'user_id' => $this->sess->getId($this->user),
			'follow_id' => $follow_id
		));

		if($ret) {
			// notify subscribe
			$this->noti->push($follow, sprintf(self::$PUSH_BACK['NEW_FOLLOW'], $this->user));

			return true;
		} else {
			$this->error_msg = 'follow already exists';
			// $this->error_msg = $this->db->getLastError();
			return false;
		}
	}

	/*return user follows who*/
	private function get_follows_array() {
		$this->db->join("users u", "f.follow_id=u.id", "LEFT");
		$this->db->where('f.user_id', $this->sess->getId($this->user));
		$this->db->orderBy('u.user', 'asc');
		$follows = $this->db->get('follows f', null, 'f.follow_id, u.user, u.nickname');
		return $follows;
	}

	private function get_follows_id() {
		$follows = $this->get_follows_array();
		$follows_id = array();
		foreach ($follows as $f) {
			$follows_id[] = $f['follow_id'];
		}
		return $follows_id;
	}

	private function get_follows_user() {
		$follows = $this->get_follows_array();
		$follows_user = array();
		foreach ($follows as $f) {
			$follows_user[] = $f['user'];
		}
		return $follows_user;
	}

	private function get_follows_user_nickname() {
		$follows = $this->get_follows_array();
		$follows_user_nick = array();
		foreach ($follows as $f) {
			$follows_user_nick[] = array(
				'user' => $f['user'],
				'nickname' => $f['nickname']
			);
		}
		return $follows_user_nick;
	}

	/*return who follows user*/
	private function get_followed_by_array() {
		$this->db->join("users u", "f.user_id=u.id", "LEFT");
		$this->db->where('f.follow_id', $this->sess->getId($this->user));
		$follows = $this->db->get('follows f', null, 'f.follow_id, u.user');
		return $follows;
	}

	private function get_followed_by_user() {
		$follows = $this->get_followed_by_array();
		$follows_user = array();
		foreach ($follows as $f) {
			$follows_user[] = $f['user'];
		}
		return $follows_user;
	}

	function get_follows() {

		if(!$this->is_valid_token())
			return false;

		$this->data = array('follows' => $this->get_follows_user_nickname());
		return true;
	}
}

?>