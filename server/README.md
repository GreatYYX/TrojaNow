# Introduction #

A simple RESTful API server for TrojaNow. It supports server pushback notifications (to mobile apps or client programs).

# Structure #

`/lib/`: third-party libraries.
`/lib/db`: database operation class MysqliDb.
`/lib/Slim`: RESTful framework.
`/lib/slim_json`: a middleware of Slim, for json format response (Attention: modified by me).
`/lib/mqtt`: php mqtt wrapper class (some functions need to run on Linux-based OS).

`/core/`: core files, it should be a DocumentRoot of a virtual host.
`/core/.htaccess`: apache httpd config override config.
`/core/index.php`: nothing, direct to parser.php, avoid catalog listing.
`/core/config.php`: configurations.
`/core/global.php`: global variables.
`/core/parser.php`: parser of protocols in Json, unpack request & pack response.
`/core/pool.php`: class of pools.
`/core/processor.php`: process all the requests and make responses.
`/core/notification.php`: sender of pushback notifications.
`/core/debug_info.php`: for debug only, delete it when releasing (also delete the routers in parser.php).

`/db.sql`: database sql file.


# Dependency #

- php: 5.4+
- apache httpd: with mod_rewrite on
- mysql
- memcached
- mosquitto (MQTT Broker)