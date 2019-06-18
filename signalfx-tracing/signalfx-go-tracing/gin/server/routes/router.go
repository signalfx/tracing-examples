package routes

import (
	"net/http"

	"github.com/gin-gonic/gin"
	gintrace "github.com/signalfx/signalfx-go-tracing/contrib/gin-gonic/gin"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/database"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/handlers"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/models"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/utils"
)

// GetRouter returns a router with all API endpoints
func GetRouter(serviceName string) *gin.Engine {
	router := gin.Default()
	router = setMiddlewares(router, serviceName)
	router = setGameRoutes(router)

	return router
}

func setMiddlewares(router *gin.Engine, serviceName string) *gin.Engine {
	router.Use(gintrace.Middleware(serviceName))
	router.Use(func(c *gin.Context) {
		c.Next()
		database.GetManager().Close(c)
	})

	return router
}

func setGameRoutes(router *gin.Engine) *gin.Engine {
	gameRoute := router.Group("/game")
	{
		// Create a new board
		gameRoute.POST("/", func(c *gin.Context) {
			boardInput := models.BoardInput{}
			err := c.Bind(&boardInput)
			board, err := handlers.CreateBoard(c.Request.Context(), boardInput)

			if err != nil {
				c.AbortWithError(http.StatusBadRequest, err)
				return
			}

			c.JSON(http.StatusCreated, gin.H{
				"gameId": board.ID,
			})
		})

		// Get a board with a given ID
		gameRoute.GET("/:_id", func(c *gin.Context) {
			board, err := handlers.GetBoardByID(c.Request.Context(), c.Param("_id"))

			if err != nil {
				c.AbortWithError(http.StatusBadRequest, err)
				return
			}

			c.JSON(http.StatusOK, gin.H{
				"status": utils.GetBoardStatus(board),
				"board":  utils.GetPlayerView(board),
			})
		})

		// Make a move
		gameRoute.POST("/:_id/move", func(c *gin.Context) {
			move := models.Move{}
			err := c.Bind(&move)
			board, err := handlers.MakeMove(c.Request.Context(), c.Param("_id"), move)

			if err != nil {
				c.AbortWithError(http.StatusBadRequest, err)
				return
			}

			status := utils.GetBoardStatus(board)

			c.JSON(http.StatusCreated, gin.H{
				"finished": (status.ShipsLeft == 0),
				"status":   status,
				"board":    utils.GetPlayerView(board),
			})
		})
	}

	return router
}
