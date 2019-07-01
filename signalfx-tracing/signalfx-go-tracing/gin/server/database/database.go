package database

import (
	"github.com/gin-gonic/gin"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/models"
)

// Manager provides interface to communicate with database chosen by the user
type Manager interface {
	GetBoardByID(c *gin.Context, id string) (models.Board, error)
	InsertBoard(c *gin.Context, board models.Board) error
	UpdateBoard(c *gin.Context, board models.Board) error
	Close(c *gin.Context)
}

// Config is a type to store database connection config
type Config struct {
	Driver      string
	Host        string
	Port        int
	Name        string
	ServiceName string
}

var manager Manager

// InitManager creates an instace of gifven manager type
func InitManager(conf *Config) {
	if conf.Driver == "mgo" {
		manager = &mgoManager{
			Host:        conf.Host,
			Port:        conf.Port,
			Name:        conf.Name,
			ServiceName: conf.ServiceName,
		}
	} else if conf.Driver == "mongo" {
		manager = &mongoManager{
			Host:        conf.Host,
			Port:        conf.Port,
			Name:        conf.Name,
			ServiceName: conf.ServiceName,
		}
	}
}

// GetManager returns database manager instance
func GetManager() Manager {
	return manager
}
