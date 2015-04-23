<?php

if(!IN_TROJANOW) {
	die();
}

require_once 'config.php';

if(!DEBUG) {
	error_reporting(0);
}

define('CURR_PATH', 		substr(dirname(__FILE__), -4, 4).'/'); // core
define('ROOT_PATH', 		substr(dirname(__FILE__), 0, -5).'/'); // length of core + 1
define('LIB_PATH', 			ROOT_PATH.'lib/');
define('MAGIC_QUOTES_GPC', 	get_magic_quotes_gpc());

// initialize Slim
require_once LIB_PATH.'Slim/Slim.php';
\Slim\Slim::registerAutoloader();
$rest = new \Slim\Slim(array(
    'debug' => DEBUG
));

// parse json to array in request
require_once LIB_PATH.'Slim/Middleware/ContentTypes.php';
$rest->add(new \Slim\Middleware\ContentTypes());

// encode array to json in response
require_once LIB_PATH.'slim_json/JsonApiView.php';
require_once LIB_PATH.'slim_json/JsonApiMiddleware.php';
$rest->view(new \JsonApiView());
$rest->add(new \JsonApiMiddleware());


// initialize database
require_once LIB_PATH.'db/MysqliDb.php';
$db = new MysqliDb(array(
	'host' => DB_HOST,
	'username' => DB_USER, 
	'password' => DB_PWD,
	'db'=> DB_DBNAME,
	'port' => DB_PORT,
	'charset' => 'utf8'
));
$db->setPrefix('trojanow_');

// initialize mqtt
require_once LIB_PATH.'mqtt/phpMQTT.php';
@$mqtt = new phpMQTT(MQTT_HOST, MQTT_PORT, 'TrojaNow Server') or die('Server Error: mqtt');
require_once 'notification.php';
$noti = new Notification($mqtt);

// initialize share memory
$mem = new Memcache;
@$mem->connect(MEM_HOST, MEM_PORT) or die ('Server Error: share memory');

// initialize pools
require_once 'pool.php';
$pool_sess = new SessionPool($mem);
$pool_loc = new LocationPool($mem);

// initialize processor
require_once 'processor.php';
$pro = new Processor($db, $pool_sess, $pool_loc, $noti);


?>