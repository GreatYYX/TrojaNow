<?php

define('IN_TROJANOW', true);
require_once './global.php';

/*
	Status code
*/
define('S_OK', 200);
define('S_CREATED', 201);
define('S_NO_CONTENT', 204);
define('S_BAD_REQUEST', 400);
define('S_UNAUTHORIZED', 401);
define('S_FORBIDDEN', 403);
define('S_NOT_FOUND', 404);
define('S_METHOD_NOT_ALLOWED', 405);
define('S_CONFLICT', 409);
define('S_INTERNAL_SERVER_ERROR', 500);

function is_param_complete($arr, $key = array()) {
	if(count($arr) != count($key)) {
		return false;
	}
	
	foreach ($key as $k) {
		if(!array_key_exists($k, $arr))
			return false;
	}
	return true;
}

function check_param($rest, $req, $key) {
	if(!is_param_complete($req, $key)) {
		$rest->render(S_BAD_REQUEST, array('error' => 'invalid parameter'));
	}
}

/*

RESTful definition:
	POST - create
	GET - read
	PUT - update
	DELETE - delete

*/

/*
	Debug
*/
$rest->get('/debug/info', function() {
	require_once 'debug_info.php';
	exit();
	//$rest->render(S_OK);
});

/*
	MQTT
*/
$rest->get('/push/:name', function($name) use ($noti) {
	$noti->push($name, 'From '.DOMAIN.' at '.date("r"));
	echo 'pushed!';
	exit();
});

/*
	Test

	request {"test":"true"}
	response OK {"test message":"correct :D"}
*/
$rest->post('/test', function() use ($rest) {
	$req = $rest->request->getBody();
	check_param($rest, $req, array('test'));

	if($req['test'] == 'true') {
		$rest->render(S_OK, array('test message' => 'correct :D'));
	} else {
		$rest->render(S_BAD_REQUEST, array('error' => 'error parameter in key or value :('));
	}
});

/*
	Account
*/
$rest->group('/account', function() use ($rest, $pro) {

    $rest->post('/reg', function() use ($rest, $pro) {
		$req = $rest->request->getBody();
		check_param($rest, $req, array('user', 'password', 'nickname'));

		$ret = $pro->registration($req['user'], $req['password'], $req['nickname']);

		if($ret == true) {
			$rest->render(S_CREATED);
		} else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
    });

    $rest->put('/signin', function() use ($rest, $pro) {
		$req = $rest->request->getBody();
		check_param($rest, $req, array('user', 'password', 'ip'));
		
		$ret = $pro->sign_in($req['user'], $req['password'], $req['ip']);

		if($ret == true) {
			$rest->render(S_OK, $pro->get_data());
		}else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
    });

	$rest->get('/signout', function() use ($rest, $pro) {

		$ret = $pro->sign_out();

		if($ret == true) {
			$rest->render(S_OK);
		} else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
    });

    $rest->put('/info', function() use ($rest, $pro) {
		$req = $rest->request->getBody();
		check_param($rest, $req, array('nickname'));

		$ret = $pro->update_info($req['nickname']);

		if($ret == true) {
			$rest->render(S_OK);
		} else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
    });

});

/*
	statuses
*/
$rest->group('/statuses', function() use ($rest, $pro) {

	$rest->post('/', function() use ($rest, $pro) {
		$req = $rest->request->getBody();
		check_param($rest, $req, array('content', 'anonymous', 'temperature', 'location'));

		$ret = $pro->create_status($req['content'], $req['anonymous'], $req['temperature'], $req['location']);

		if($ret == true) {
			$rest->render(S_CREATED, $pro->get_data());
		} else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
	});

	$rest->delete('/:id', function($id) use ($rest, $pro) {

		$ret = $pro->delete_status($id);

		if($ret == true) {
			$rest->render(S_OK);
		} else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
	});

	$rest->get('(/:type)', function($type = 'unconstrained') use ($rest, $pro) {

		$ret = $pro->get_statuses($type, DEFAULT_AMOUNT_STATUSES);

		if($ret == true) {
			$rest->render(S_OK, $pro->get_data());
		} else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
	});

});

/*
	follows
*/
$rest->group('/follows', function() use ($rest, $pro) {

	$rest->post('/', function() use ($rest, $pro) {
		$req = $rest->request->getBody();
		check_param($rest, $req, array('follow'));

		$ret = $pro->add_follow($req['follow']);

		if($ret == true) {
			$rest->render(S_CREATED);
		} else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
	});

	$rest->get('/', function() use ($rest, $pro) {

		$ret = $pro->get_follows();

		if($ret == true) {
			$rest->render(S_OK, $pro->get_data());
		} else {
			$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
		}
	});

	// $rest->delete('/:id', function($id) use ($rest, $pro) {

	// 	$ret = $pro->delete_follow($id);

	// 	if($ret == true) {
	// 		$rest->render(S_OK);
	// 	} else {
	// 		$rest->render(S_BAD_REQUEST, array('error' => $pro->error_msg()));
	// 	}
	// });

});

// response
$rest->run();

?>