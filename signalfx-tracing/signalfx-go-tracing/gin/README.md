# Gin Instrumentation Example
This is an example of producing distributed traces using the [SignalFx Tracing Library for Go](https://github.com/signalfx/signalfx-go-tracing). This example provides an instrumented API server that serves Battleship game to show some basic patterns in accessing the instrumentations of a Gin application and MongoDB drivers. You can either play by calling exposed API endpoints directly or run a simulated player.

## Prerequisite
This example requires a running MongoDB instance that the server can access. You can start a docker container by running this command.
```sh
$ docker run -d --name mongo -p 27017:27017 mongo

```
## Configuration
A default set of configuration is included in [server/server.go](./server/server.go). Modify as needed before running. Note that you can choose MongoDB driver to use by changing `MongoDriver` config.
```go
// server.go

const (
	// ServiceName contains name of this service. This will show up on traces
	ServiceName = "signalfx-battleship"
	// TracingEndpoint contains a url to send traces
	TracingEndpoint = "http://localhost:9080/v1/trace"
	// Port that the app runs
	Port = 3030
	// MongoDriver is mongodb driver type. Selections are "mgo" | "mongo"
	MongoDriver = "mongo"
	// MongoHost is database hostname
	MongoHost = "localhost"
	// MongoPort is database port number
	MongoPort = 27017
	// MongoDbName is database name
	MongoDbName = "bships"
)

```

## Start Service
To run this example locally and send traces to your available Smart Agent or Gateway, please clone this repository and from this directory do the following:
```sh
$ cd server
$ go get
$ go run server.go

```


## How to Play
You can start a new game and get the `gameId` by calling `POST /game` endpoint.
Once you get a `gameId`, call `POST /game/[gameId]` endpoint with a JSON body as shown below.
```json
{
  "x": 1,
  "y": 0
}
```
Then, the server will return a response with the game status.
```json
{
    "board": [
        [
            0,
            0,
            0
        ],
        [
            1,
            0,
            0
        ],
        [
            0,
            0,
            0
        ]
    ],
    "finished": false,
    "status": {
        "turnsPlayed": 1,
        "shipsHit": 0,
        "shipsLeft": 2
    }
}
```
`finished` flag will turn on when all the ship spots are found.

## Using the simulated player
You can also automate the sequence of API calls of finishing one game by running [player/player.go](./player/player.go)