# TrojanNow Protocol #

This protocol is based on RESTful.

References: 

- RESTful: `http://www.restapitutorial.com/`
- Twitter RESTful API: `https://dev.twitter.com/rest/public`
- JSON formatter: `http://jsonformatter.curiousconcept.com/`

# Common Request Format #
- Domain: `www.example.com:1024`
- Request: `METHOD` `Domain/[Resource URL].json`
- Content-Type: `application/json`

# Status Code #

- `200` OK
- `201` CREATED
- `204` NO_CONTENT
- `400` BAD_REQUEST
- `401` UNAUTHORIZED
- `403` FORBIDDEN
- `404` NOT_FOUND
- `405` METHOD_NOT_ALLOWED
- `409` CONFLICT
- `500` INTERNAL_SERVER_ERROR

# Server API #

All of the Resource URLs and METHOD with it are explained below, including request body & response body with correct status code.

If error occurs, status code will be set, response body will be:

	{
		"error": @string(message)
	}

If the request needs authorization, the header should include a `Authorization` field (noted as `AUTH` below). The value of this field is `user:token`.

Remember aLL of users' input should be filtered both on client and server for avoiding SQL injection or CSRF.

## POST /test

Request:

    {
    	"test": "true"
    }

Response: OK

	{
		"test message": "correct :D"
	}


## POST /account/reg

Request:

    {
    	"user": @string(0-9A-Za-z_, start with alphabet 6-12 length),
		"password": @string(0-9A-Za-z_, start with alphabet, 6-12 length),
    	"nickname": @string(2-12 length)
    }

Response: CREATED

## PUT /account/signin

Request:

    {
    	"user": @string,
		"password": @string,
		"ip": @string(it could be IPV4/6)
    }

Response: OK

	{
		"token": @string,
		"timestamp": @string (UNIX timestamp, not support yet)
	}

## GET /account/signout

Request:

	AUTH

Response: OK

## PUT /account/info

Request:

	AUTH
    {
    	"nickname": @string | null
    }

Response: OK

## POST /statuses

Request:

	AUTH
    {
		"content": @string,
		"anonymous": @bool,
		"temperature": @string(unit in centigrade) | null,
		"location": [@int(latitude), @int(longitude)] | null
    }

Response: CREATED

	{
		"id": @int
		"date": @int
	}

## DELETE /statuses/:id

Request:

	AUTH

Response: OK

## GET /statuses(/:type[normal|anonymous|unconstrained])

Request:

	AUTH

Response: OK

	{
		"next_cursor": @int(not support yet),
		"statuses": [
			{
				"id": @int,
		    	"author": @string,
		    	"author_nickname": @string,
				"content": @string,
				"date": @string(UNIX timestamp),
				"temperature": @string | null,
				"location": [@int(latitude), @int(longitude)] | null
		    },
			{...},
			{...}
		]
	}

In request, type is optional, default value is unconstrained.
Server will return at most 20 statuses once.

## GET /chat/:friend

Request:
	
	AUTH

Response: OK

	{
    	"ip": @string
	}


## POST /friends

Request:

	AUTH
    {
		"friend": @string
    }

Response: OK

## PUT /friends/:friend

Request:

	AUTH
	{
		"accept": @bool
	}

This is for accepting new friend request or not.

## GET /friends

Request:

    AUTH

Response: OK

	{
		"friends": [
			@string,
			@string,
			...,
			@string
		]
	}

## DELETE /friends/:id

Request:

    AUTH

Response: OK

## PUT /sessions

Request:

	AUTH
    {
		"location": [@int, @int],
		"temperature": @string
    }

Response: OK

# Server Pushed Notification

Server will push back notifications to client by MQTT protocol. Client should also connect to MQTT server when signing in.

## New friend request

	{
		"type": "NEW_FRIEND",
		"data": {
			"user": @string
		}
	}

## Accepted new friend request
	
	{
		"type": "NEW_FRIEND_ACCEPT",
		"data": {
			"user": @string,
			"nickname": @string
		}
	}

## Rejected new friend request
	
	{
		"type": "NEW_FRIEND_REJECT",
		"data": {
			"user": @string
		}
	}

## New status available

	{
		"type": "NEW_STATUS"
	}