# TrojanNow Protocol #

This protocol is based on RESTful.

References: 

- RESTful: `http://www.restapitutorial.com/`
- Twitter RESTful API: `https://dev.twitter.com/rest/public`
- JSON formatter: `http://jsonformatter.curiousconcept.com/`

# Common Request Format #
- Domain: `trojanow.yyx.name:6666`
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

# API #

All of the Resource URLs are explained below, including request body & response body with correct status code.

If error occurs, status code will be set, response body will be:

	{
		"error": @string(message)
	}

ALL of users' input should be filtered both on client and server for avoiding SQL injection or CSRF.

## POST auth/reg

Request:

    {
    	"user: @string(0-9A-Za-z_, start with alphabet 6-12 length),
		"password": @string(0-9A-Za-z_, start with alphabet, 6-12 length),
    	"nickname": @string(2-12 length)
    }

Response: CREATED

## GET auth/signin

Request: OK

    {
    	"user": @string,
		"password": @string
    }

Response: OK

	{
		"id": @int
		"user": @string,
		"token": @string
	}

## GET auth/signout

Request:

    {
    	"user": @string,
		"token": @string
    }

Response: OK

## POST statuses

Request:

    {
    	"user": @string,
		"token": @string,
		"content": @string,
		"date": @string(UNIX timestamp),
		"anonymous": @bool,
		"temperature": @string | null,
		"location": [@int(latitude), @int(longitude)] | null
    }

Response: GREATED

	{
		"id": @int
	}

## DELETE statuses

Request:

    {
    	"user": @string,
		"token": @string,
		"id": @int
    }

Response: OK

## GET statuses

Request:

    {
    	"user": @string,
		"token": @string,
		"anonymous": @bool(true will only return anonymous status, false will only return normal status)
    }

Response: OK

	[
		{
	    	"auther": @string,
			"content": @string,
			"date": @string(UNIX timestamp),
			"temperature": @string | null,
			"location": [@int(latitude), @int(longitude)] | null
	    },
		{...},
		{...}
	]

## GET chat

Request:

    {
    	"user": @string,
		"token": @string,
		"friend": @string(target user)
    }

Response: OK

	{
    	"ip": @string(it could be IPV4/6)
	}


## POST friends

Request:

    {
    	"user": @string,
		"token": @string,
		"friend": @string
    }

Response: OK

## GET friends

Request:

    {
    	"user": @string,
		"token": @string
    }

Response: OK

    [
		@string,
		@string,
		...,
		@string
	]

## DELETE friends

Request:

    {
    	"user": @string,
		"token": @string,
		"friend": @string
    }

Response: OK

## PUT sessions

Request:

    {
    	"user": @string,
		"token": @string,
		"location": [@int, @int],
		"temperature": @string
    }

Response: OK
