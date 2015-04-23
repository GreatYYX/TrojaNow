<?php

define('DEBUG', true);

define('DEFAULT_AMOUNT_STATUSES', 20);
define('DOMAIN', 'trojanow.example.com');
define('PUSH_CLIENT_ID', 'TrojaNow');

define('DB_HOST', 'localhost');
define('DB_USER', 'xxx');
define('DB_PWD', 'xxx');
define('DB_DBNAME', 'trojanow');
define('DB_PORT', 3306);

define('MEM_HOST', 'localhost');
define('MEM_PORT', 11211);
define('MQTT_HOST', 'trojanow.example.com');
define('MQTT_PORT', 1883);

$banned_keywords = array('keyindex', 'admin', 'anonymous');
date_default_timezone_set('America/Los_Angeles');

?>