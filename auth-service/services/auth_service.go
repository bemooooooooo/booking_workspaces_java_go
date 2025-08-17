package services

import (
	"auth-service/customErrors"
	"auth-service/models"
	"auth-service/repository"
	"errors"
	"log"
	"os"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"
)

type AuthService struct {
	userRepo *repository.UserRepository
}

func NewAuthService(userRepo *repository.UserRepository) *AuthService {
	return &AuthService{
		userRepo: userRepo,
	}
}

// Register регистрирует нового пользователя
func (s *AuthService) Register(req models.RegisterRequest) (*models.LoginResponse, error) {
	// Проверяем, что пользователь не существует
	existingUser, err := s.userRepo.GetUserByUsername(req.Username)
	if err != nil {
		return nil, err
	}
	if existingUser != nil {
		return nil, customErrors.NewCustomError(customErrors.UsernameExistsCode, "username already exist")
	}

	existingUser, err = s.userRepo.GetUserByEmail(req.Email)
	if err != nil {
		return nil, err
	}
	if existingUser != nil {
		return nil, customErrors.NewCustomError(customErrors.EmailExistsCode, "email already exist")
	}

	// Хешируем пароль
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, err
	}

	// Создаем пользователя
	user := &models.User{
		Username: req.Username,
		Email:    req.Email,
		Password: string(hashedPassword),
		Role:     "user", // По умолчанию обычный пользователь
	}

	err = s.userRepo.CreateUser(user)
	if err != nil {
		return nil, err
	}

	// Генерируем токены
	accessToken, refreshToken, err := s.generateTokens(user.ID, user.Role)
	if err != nil {
		return nil, err
	}

	return &models.LoginResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
		User:         *user,
	}, nil
}

// Login аутентифицирует пользователя
func (s *AuthService) Login(req models.LoginRequest) (*models.LoginResponse, error) {
	// Ищем пользователя по username или email
	user, err := s.userRepo.GetUserByUsername(req.Username)
	if err != nil {
		return nil, err
	}
	log.Print(user)
	if user == nil {
		return nil, customErrors.NewCustomError(customErrors.InvalidCredentials, "invalid credentials")
	}

	// Проверяем пароль
	err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(req.Password))
	if err != nil {
		return nil, customErrors.NewCustomError(customErrors.InvalidCredentials, "invalid credentials (password)")
	}

	// Генерируем токены
	accessToken, refreshToken, err := s.generateTokens(user.ID, user.Role)
	if err != nil {
		return nil, err
	}

	return &models.LoginResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
		User:         *user,
	}, nil
}

// RefreshToken обновляет access token
func (s *AuthService) RefreshToken(refreshToken string) (*models.LoginResponse, error) {
	// Валидируем refresh token
	claims, err := ValidateRefreshToken(refreshToken)
	if err != nil {
		return nil, errors.New("invalid refresh token")
	}

	// Получаем пользователя
	userID, ok := (*claims)["user_id"].(string)
	if !ok {
		return nil, errors.New("invalid token claims")
	}
	user, err := s.userRepo.GetUserByID(userID)
	if err != nil {
		return nil, errors.New("user not found")
	}

	// Генерируем новые токены
	accessToken, newRefreshToken, err := s.generateTokens(user.ID, user.Role)
	if err != nil {
		return nil, err
	}

	return &models.LoginResponse{
		AccessToken:  accessToken,
		RefreshToken: newRefreshToken,
		User:         *user,
	}, nil
}

// generateTokens генерирует access и refresh токены
func (s *AuthService) generateTokens(userID string, role string) (string, string, error) {
	// Access token (короткий срок жизни)
	accessToken := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"user_id": userID,
		"role":    role,
		"exp":     time.Now().Add(time.Hour * 1).Unix(), // 1 час
		"type":    "access",
	})

	// Refresh token (долгий срок жизни)
	refreshToken := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"user_id": userID,
		"exp":     time.Now().Add(time.Hour * 24 * 7).Unix(), // 7 дней
		"type":    "refresh",
	})

	// Получаем секретный ключ
	secret := os.Getenv("JWT_SECRET")
	if secret == "" {
		secret = "default-secret-key"
	}

	// Подписываем токены
	accessTokenString, err := accessToken.SignedString([]byte(secret))
	if err != nil {
		return "", "", err
	}

	refreshTokenString, err := refreshToken.SignedString([]byte(secret))
	if err != nil {
		return "", "", err
	}

	return accessTokenString, refreshTokenString, nil
}

// ValidateToken валидирует access token
func ValidateToken(tokenString string) (*jwt.MapClaims, error) {
	secret := os.Getenv("JWT_SECRET")
	if secret == "" {
		secret = "default-secret-key"
	}

	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return []byte(secret), nil
	})

	if err != nil {
		return nil, err
	}

	if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
		// Проверяем тип токена
		if tokenType, ok := claims["type"].(string); !ok || tokenType != "access" {
			return nil, errors.New("invalid token type")
		}
		return &claims, nil
	}

	return nil, errors.New("invalid token")
}

// ValidateRefreshToken валидирует refresh token
func ValidateRefreshToken(tokenString string) (*jwt.MapClaims, error) {
	secret := os.Getenv("JWT_SECRET")
	if secret == "" {
		secret = "default-secret-key"
	}

	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return []byte(secret), nil
	})

	if err != nil {
		return nil, err
	}

	if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
		// Проверяем тип токена
		if tokenType, ok := claims["type"].(string); !ok || tokenType != "refresh" {
			return nil, errors.New("invalid token type")
		}
		return &claims, nil
	}

	return nil, errors.New("invalid token")
}
