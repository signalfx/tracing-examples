package database

import (
	"context"
	"fmt"

	mongotrace "github.com/signalfx/signalfx-go-tracing/contrib/mongodb/mongo-go-driver/mongo"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/models"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type mongoManager struct {
	Host        string
	Port        int
	Name        string
	ServiceName string
}

var _ Manager = (*mongoManager)(nil)

var mongoClient *mongo.Client

// GetBoardByID returns a board for a given boardID
func (m *mongoManager) GetBoardByID(c context.Context, id string) (models.Board, error) {
	collection := m.getCollection(c)

	board := models.Board{}
	err := collection.FindOne(c, bson.M{"board_id": id}).Decode(&board)
	if err != nil {
		fmt.Printf("%+v\n", err)
		return models.Board{}, err
	}
	return board, nil
}

// InsertBoard inserts a given board
func (m *mongoManager) InsertBoard(c context.Context, board models.Board) error {
	collection := m.getCollection(c)

	_, err := collection.InsertOne(c, board)
	return err
}

// UpdateBoard saves a given updated board
func (m *mongoManager) UpdateBoard(c context.Context, board models.Board) error {
	collection := m.getCollection(c)

	_, err := collection.UpdateOne(c, bson.M{"board_id": board.ID}, bson.D{{Key: "$set", Value: board}})
	return err
}

// getCollection returns board collection
func (m *mongoManager) getCollection(c context.Context) *mongo.Collection {
	if mongoClient == nil {
		opts := options.Client()
		opts.SetMonitor(mongotrace.NewMonitor(mongotrace.WithServiceName("signalfx-battleship")))

		var err error
		mongoClient, err = mongo.Connect(c, opts.ApplyURI(fmt.Sprintf("mongodb://%s:%d", m.Host, m.Port)))
		if err != nil {
			fmt.Printf("Can't connect to mongo, go error %v\n", err)
			panic(err.Error())
		}
	}

	db := mongoClient.Database(m.Name)
	collection := db.Collection(models.CollectionBoard)

	return collection
}

// Close closes open DB session
func (m *mongoManager) Close(c context.Context) {
	if mongoClient != nil {
		mongoClient.Disconnect(c)
		mongoClient = nil
	}
}
