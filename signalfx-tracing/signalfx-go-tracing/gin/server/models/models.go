package models

const (
	// CollectionBoard is name of collection that stores boards
	CollectionBoard = "boards"
)

// Board model
type Board struct {
	ID        string   `json:"board_id" bson:"board_id"`
	Width     int      `json:"width" bson:"width"`
	Height    int      `json:"height" bson:"height"`
	Ships     [][]bool `json:"ships" bson:"ships"`
	Torpedoes [][]bool `json:"torpedoes" bson:"torpedoes"`
}

// BoardInput model
type BoardInput struct {
	Width  int `json:"width" bson:"width"`
	Height int `json:"height" bson:"height"`
}

// BoardStatus model
type BoardStatus struct {
	TurnsPlayed int `json:"turnsPlayed" bson:"turnsPlayed"`
	ShipsHit    int `json:"shipsHit" bson:"shipsHit"`
	ShipsLeft   int `json:"shipsLeft" bson:"shipsLeft"`
}

// Move model
type Move struct {
	X int `json:"x" bson:"x"`
	Y int `json:"y" bson:"y"`
}
