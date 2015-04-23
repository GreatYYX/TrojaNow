# TrojanNow Protocol #

This protocol is based on RESTful.

References: 

- RESTful: `http://www.restapitutorial.com/`
- Twitter RESTful API: `https://dev.twitter.com/rest/public`
- JSON formatter: `http://jsonformatter.curiousconcept.com/`

# Common Request Format #
- Domain: `www.example.com:port`
- Request: `METHOD` `Domain/[Resource URL]`
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

If error occurs, `status code` will be set, response body will be:

	{
		"error": @string(message)
	}

If the request needs authorization, the header should include a `Authorization` field (noted as `AUTH` below). The value of this field is `user:token`.

Remember all of users' input should be filtered both on client and server for avoiding SQL injection or CSRF.

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
    	"user": @string(0-9A-Za-z_, start with alphabet, 6-10 length),
		"password": @string(0-9A-Za-z_, start with alphabet, 6-12 length),
    	"nickname": @string(2-18 length)
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
		"location": [@number(latitude), @number(longitude)] | null
    }

Response: CREATED

	{
		"id": @number
		"date": @number(UNIX timestamp)
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
		"next_cursor": @number(not support yet),
		"statuses": [
			{
				"id": @number,
		    	"author": @string,
		    	"author_nickname": @string,
				"content": @string,
				"anonymous": @bool,
				"date": @number(UNIX timestamp),
				"temperature": @string | null,
				"location": [@number(latitude), @number(longitude)] | null
		    },
			{...},
			{...}
		]
	}

In request, type is optional, default value is unconstrained (to anonymous).
Server will return at most 20 statuses once.
If no statues, the value of field "statuses" will be an empty array.

## POST /follows

Request:

	AUTH
    {
		"follow": @string
    }

Response: CREATED

## GET /follows

Request:

    AUTH

Response: OK

	{
		"follows": [
			{
				"user": @string,
				"nickname": @string
			},
			{...},
			{...}
		]
	}

If no follow, the value of field "follows" will be an empty array.

## DELETE /follows/:id

Request:

    AUTH

Response: OK

# Server Pushed Notification

Server will push back notifications to client by MQTT protocol. Client should also connect to MQTT server when signing in.

## New follow
	
	{
		"type": "NEW_FOLLOW",
		"user": @string
	}

## New status available

	{
		"type": "NEW_STATUS"
	}