package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
)

type retrieveGameID struct {
	GameID string `json:"gameId"`
}

type retrieveBoardStatus struct {
	Board  [][]int `json:"board"`
	Status struct {
		TurnsPlayed int `json:"turnsPlayed"`
		ShipsHit    int `json:"shipsHit"`
		ShipsLeft   int `json:"shipsLeft"`
	} `json:"status"`
	Finished bool `json:"finished"`
}

const (
	// Hostname of the game service
	Hostname = "localhost"
	// Port of the game service
	Port = 3030
	// Width of the board
	Width = 15
	// Height of the board
	Height = 15
)

var client *http.Client

func main() {
	client = &http.Client{}

	gameID := createGame()
	playMoves(gameID)
}

func createGame() string {
	response, err := client.Post(fmt.Sprintf("http://%s:%d/game", Hostname, Port), "application/json", bytes.NewBufferString(fmt.Sprintf(`{"width": %d, "height": %d}`, Width, Height)))
	if err != nil {
		fmt.Printf("Can't start new game, go error %v\n", err)
		panic(err.Error())
	}

	body, _ := ioutil.ReadAll(response.Body)
	var respGameID retrieveGameID
	json.Unmarshal(body, &respGameID)

	return respGameID.GameID
}

func playMoves(gameID string) {
	moveX := 0
	moveY := 0

	for finished := false; !finished; {
		response, _ := client.Post(fmt.Sprintf("http://%s:%d/game/%s/move", Hostname, Port, gameID), "application/json", bytes.NewBufferString(fmt.Sprintf(`{"x": %d, "y": %d}`, moveX, moveY)))

		body, _ := ioutil.ReadAll(response.Body)

		var boardStatus retrieveBoardStatus
		json.Unmarshal(body, &boardStatus)

		printBoardStatus(boardStatus)

		moveX++
		if moveX >= Width {
			moveX = 0
			moveY++
			if moveY >= Height {
				break
			}
		}

		finished = boardStatus.Finished
	}
}

func printBoardStatus(boardStatus retrieveBoardStatus) {
	fmt.Printf("Turn %d\n", boardStatus.Status.TurnsPlayed)
	fmt.Printf("Hit/Left %d/%d\n", boardStatus.Status.ShipsHit, boardStatus.Status.ShipsLeft)
	for j := 0; j < Height; j++ {
		for i := 0; i < Width; i++ {
			fmt.Printf("%d ", boardStatus.Board[i][j])
		}
		fmt.Printf("\n")
	}
	fmt.Printf("\n")
}
