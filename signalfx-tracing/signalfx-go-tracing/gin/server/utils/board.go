package utils

import (
	"math/rand"
	"time"

	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/models"
)

// GetBoardStatus returns a set of numbers to show board status
func GetBoardStatus(board models.Board) models.BoardStatus {
	turnsPlayed := 0
	shipsHit := 0
	shipsLeft := 0

	for i := 0; i < board.Width; i++ {
		for j := 0; j < board.Height; j++ {
			if board.Ships[i][j] {
				if board.Torpedoes[i][j] {
					shipsHit++
				} else {
					shipsLeft++
				}
			}
			if board.Torpedoes[i][j] {
				turnsPlayed++
			}
		}
	}

	return models.BoardStatus{
		TurnsPlayed: turnsPlayed,
		ShipsHit:    shipsHit,
		ShipsLeft:   shipsLeft,
	}
}

// GetEmptyBoard returns an empty board with given size
func GetEmptyBoard(width int, height int) [][]bool {
	board := make([][]bool, width)
	for i := range board {
		board[i] = make([]bool, height)
	}
	return board
}

// GetRandomBoard returns a random board with given size
func GetRandomBoard(width int, height int) [][]bool {
	const density = 0.3
	const averageSize = 2.5

	board := GetEmptyBoard(width, height)
	for i := 0; i < int(float64(width*height)*density/averageSize); i++ {
		rx := getRandomNumber(width)
		ry := getRandomNumber(height)
		for board[rx][ry] {
			rx = getRandomNumber(width)
			ry = getRandomNumber(height)
		}
		board[rx][ry] = true

		// Make the ship size 2 or 3, if possible
		tailDirection := getRandomNumber(4)
		makeThree := getRandomNumber(2)
		if tailDirection == 0 && rx > 0 {
			board[rx-1][ry] = true
			if makeThree > 0 && rx-1 > 0 {
				board[rx-2][ry] = true
			}
		} else if tailDirection == 1 && ry > 0 {
			board[rx][ry-1] = true
			if makeThree > 0 && ry-1 > 0 {
				board[rx][ry-2] = true
			}
		} else if tailDirection == 2 && rx < width-1 {
			board[rx+1][ry] = true
			if makeThree > 0 && rx+1 < width-1 {
				board[rx+2][ry] = true
			}
		} else if tailDirection == 3 && ry < height-1 {
			board[rx][ry+1] = true
			if makeThree > 0 && ry+1 < height-1 {
				board[rx][ry+2] = true
			}
		}
	}

	return board
}

// getRamdomNumber returns a random integer between 0~max
func getRandomNumber(max int) int {
	seed := rand.NewSource(time.Now().UnixNano())
	rnd := rand.New(seed)
	return rnd.Intn(max)
}

// GetPlayerView returns what the board should look like for the player
// 0 : Torpedo never landed
// 1 : Torpedo landed and nothing happened
// 2 : Torpedo landed and hit!
func GetPlayerView(board models.Board) [][]int {
	playerView := make([][]int, board.Width)
	for i := range playerView {
		playerView[i] = make([]int, board.Height)
		for j := 0; j < board.Height; j++ {
			if board.Torpedoes[i][j] {
				if board.Ships[i][j] {
					playerView[i][j] = 2
				} else {
					playerView[i][j] = 1
				}
			} else {
				playerView[i][j] = 0
			}
		}
	}

	return playerView
}
