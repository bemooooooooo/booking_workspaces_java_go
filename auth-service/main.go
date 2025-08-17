package main

import (
	"auth-service/handlers"
	"auth-service/middleware"
	"auth-service/repository"
	"auth-service/services"
	"log"
	"os"

	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

// @title Auth Service API
// @version 1.0
// @description Сервис авторизации и аутентификации для системы бронирования
// @host localhost:8081
// @BasePath /api
func main() {
	// Инициализация базы данных
	db, err := repository.InitDB()
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}
	defer db.Close()

	// Инициализация репозиториев
	userRepo := repository.NewUserRepository(db)

	// Инициализация сервисов
	authService := services.NewAuthService(userRepo)
	userService := services.NewUserService(userRepo)

	// Инициализация обработчиков
	authHandler := handlers.NewAuthHandler(authService)
	userHandler := handlers.NewUserHandler(userService)

	gin.SetMode(gin.ReleaseMode)
	r := gin.Default()

	// Middleware
	r.Use(middleware.CORS())
	r.Use(middleware.Logger())

	// API маршруты
	api := r.Group("/api")
	{
		// Авторизация
		auth := api.Group("/auth")
		{
			auth.POST("/register", authHandler.Register)
			auth.POST("/login", authHandler.Login)
			auth.POST("/refresh", authHandler.RefreshToken)
		}

		// Пользователи (требуют аутентификации)
		users := api.Group("/users")
		users.Use(middleware.AuthMiddleware())
		{
			users.GET("/profile", userHandler.GetProfile)
			users.PUT("/profile", userHandler.UpdateProfile)
			users.GET("/:id", userHandler.GetUserByID)
		}

		// Административные функции
		admin := api.Group("/admin")
		admin.Use(middleware.AuthMiddleware())
		admin.Use(middleware.AdminMiddleware())
		{
			admin.GET("/users", userHandler.GetAllUsers)
			admin.PUT("/users/:id", userHandler.UpdateUser)
			admin.DELETE("/users/:id", userHandler.DeleteUser)
		}
	}

	// Swagger документация
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	port := os.Getenv("SERVER_PORT")
	if port == "" {
		port = "8081"
	}
	log.Printf("Auth service starting on port %s", port)
	if err := r.Run(":" + port); err != nil {
		log.Fatal("Failed to start server:", err)
	}
}
