<?php

// define('IN_TROJANOW', true);
// require_once './core/global.php';

global $mem;
global $db;

?>

<html>
<head><title>TrojaNow Debug Information</title></head>
<body>

<h1>TrojaNow Debug Information</h1>

<hr>

<h2>Database</h2>

<p><b>Users</b></p>
	<table border="1">
		<th>id</th>
		<th>user</th>
		<th>password</th>
		<th>nickname</th>
	<?php
		$users = $db->get('users');
		foreach ($users as $k => $v) {
	?>
	<tr>
		<td><?php echo $v['id']; ?></td>
		<td><?php echo $v['user']; ?></td>
		<td><?php echo $v['password']; ?></td>
		<td><?php echo $v['nickname']; ?></td>
	</tr>
	<?php
		}
	?>
	</table>

<p><b>Statuses</b></p>
	<table border="1">
		<th>id</th>
		<th>user_id</th>
		<th>content</th>
		<th>date</th>
		<th>anonymous</th>
		<th>temperature</th>
		<th>location</th>
	<?php
		$users = $db->get('statuses');
		foreach ($users as $k => $v) {
	?>
	<tr>
		<td><?php echo $v['id']; ?></td>
		<td><?php echo $v['user_id']; ?></td>
		<td><?php echo $v['content']; ?></td>
		<td><?php echo $v['date']; ?></td>
		<td><?php echo $v['anonymous']; ?></td>
		<td><?php echo $v['temperature']; ?></td>
		<td><?php echo $v['location']; ?></td>
	</tr>
	<?php
		}
	?>
	</table>

	<p><b>Follows</b></p>
	<table border="1">
		<th>user_id</th>
		<th>follow_id</th>
	<?php
		$users = $db->get('follows');
		foreach ($users as $k => $v) {
	?>
	<tr>
		<td><?php echo $v['user_id']; ?></td>
		<td><?php echo $v['follow_id']; ?></td>
	</tr>
	<?php
		}
	?>
	</table>

<hr>

<h2>Memcached</h2>

<table border="1">
<?php
	foreach ($mem->getStats() as $k => $v) {
?>
<tr>
	<td><?php echo $k; ?></td>
	<td><?php echo $v; ?></td>
</tr>
<?php
	}
?>
</table>

</body>
</html>