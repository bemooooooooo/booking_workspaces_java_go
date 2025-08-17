package models

import (
	"time"
)

// User представляет пользователя в системе
type User struct {
	ID        string    `json:"id" db:"id"`
	Username  string    `json:"username" db:"username"`
	Email     string    `json:"email" db:"email"`
	Password  string    `json:"-" db:"password_hash"`
	Role      string    `json:"role" db:"role"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
	UpdatedAt time.Time `json:"updated_at" db:"updated_at"`
}

// RegisterRequest представляет запрос на регистрацию
type RegisterRequest struct {
	Username string `json:"username" binding:"required,min=3,max=50"`
	Email    string `json:"email" binding:"required,email"`
	Password string `json:"password" binding:"required,min=6"`
}

// LoginRequest представляет запрос на вход
type LoginRequest struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

// LoginResponse представляет ответ на вход
type LoginResponse struct {
	User         User   `json:"user"`
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}

// UpdateProfileRequest представляет запрос на обновление профиля
type UpdateProfileRequest struct {
	Username string `json:"username,omitempty" binding:"omitempty,min=3,max=50"`
	Email    string `json:"email,omitempty" binding:"omitempty,email"`
	Password string `json:"password,omitempty" binding:"omitempty,min=6"`
}

// UpdateUserRequest представляет запрос на обновление пользователя (админ)
type UpdateUserRequest struct {
	Username string `json:"username,omitempty" binding:"omitempty,min=3,max=50"`
	Email    string `json:"email,omitempty" binding:"omitempty,email"`
	Password string `json:"password,omitempty" binding:"omitempty,min=6"`
	Role     string `json:"role,omitempty" binding:"omitempty,oneof=user admin"`
}

// UserRole представляет роли пользователей
const (
	RoleUser  = "user"
	RoleAdmin = "admin"
)
