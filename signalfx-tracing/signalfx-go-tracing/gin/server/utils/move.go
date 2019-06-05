package utils

import (
	"errors"

	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/models"
)

// ValidateMove validates a move
func ValidateMove(board models.Board, move models.Move) error {
	if move.X < 0 || move.Y < 0 || move.X >= board.Width || move.Y >= board.Height {
		return errors.New("Out of bounds")
	}
	if board.Torpedoes[move.X][move.Y] {
		return errors.New("Do not waste your torpedoes")
	}

	return nil
}

// ApplyMove applies a move to a board
func ApplyMove(board models.Board, move models.Move) models.Board {
	board.Torpedoes[move.X][move.Y] = true
	return board
}
