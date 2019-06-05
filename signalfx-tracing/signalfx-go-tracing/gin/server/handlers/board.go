package handlers

import (
	"context"

	"github.com/google/uuid"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/database"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/models"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/utils"
)

// CreateBoard creates a new board
func CreateBoard(c context.Context, input models.BoardInput) (models.Board, error) {
	board := models.Board{
		ID:        uuid.New().String()[:8],
		Width:     input.Width,
		Height:    input.Height,
		Ships:     utils.GetRandomBoard(input.Width, input.Height),
		Torpedoes: utils.GetEmptyBoard(input.Width, input.Height),
	}

	err := database.GetManager().InsertBoard(c, board)
	if err != nil {
		return models.Board{}, err
	}

	return board, nil
}

// GetBoardByID gets a board with a requested ID
func GetBoardByID(c context.Context, boardID string) (models.Board, error) {
	board, err := database.GetManager().GetBoardByID(c, boardID)
	if err != nil {
		return models.Board{}, err
	}

	return board, nil
}

// MakeMove makes a move to a board with a requested ID
func MakeMove(c context.Context, boardID string, move models.Move) (models.Board, error) {
	board, err := database.GetManager().GetBoardByID(c, boardID)
	if err != nil {
		return models.Board{}, err
	}

	err = utils.ValidateMove(board, move)
	if err != nil {
		return models.Board{}, err
	}
	board = utils.ApplyMove(board, move)

	err = database.GetManager().UpdateBoard(c, board)
	if err != nil {
		return models.Board{}, err
	}

	return board, nil
}
