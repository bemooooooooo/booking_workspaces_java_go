package services

import (
	"auth-service/models"
	"auth-service/repository"
	"errors"

	"golang.org/x/crypto/bcrypt"
)

type UserService struct {
	userRepo *repository.UserRepository
}

func NewUserService(userRepo *repository.UserRepository) *UserService {
	return &UserService{
		userRepo: userRepo,
	}
}

// GetUserByID возвращает пользователя по ID
func (s *UserService) GetUserByID(userID string) (*models.User, error) {
	user, err := s.userRepo.GetUserByID(userID)
	if err != nil {
		return nil, err
	}
	return user, nil
}

// UpdateProfile обновляет профиль пользователя
func (s *UserService) UpdateProfile(userID string, req models.UpdateProfileRequest) (*models.User, error) {
	user, err := s.userRepo.GetUserByID(userID)
	if err != nil {
		return nil, err
	}

	if req.Username != "" {
		existingUser, err := s.userRepo.GetUserByUsername(req.Username)
		if err != nil {
			return nil, err
		}
		if existingUser != nil && existingUser.ID != userID {
			return nil, errors.New("username already exists")
		}
		user.Username = req.Username
	}

	if req.Email != "" {
		existingUser, err := s.userRepo.GetUserByEmail(req.Email)
		if err != nil {
			return nil, err
		}
		if existingUser != nil && existingUser.ID != userID {
			return nil, errors.New("email already exists")
		}
		user.Email = req.Email
	}

	if req.Password != "" {
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
		if err != nil {
			return nil, err
		}
		user.Password = string(hashedPassword)
	}

	err = s.userRepo.UpdateUser(user)
	if err != nil {
		return nil, err
	}

	return user, nil
}

// GetAllUsers возвращает всех пользователей (только для администраторов)
func (s *UserService) GetAllUsers() ([]models.User, error) {
	users, err := s.userRepo.GetAllUsers()
	if err != nil {
		return nil, err
	}
	return users, nil
}

// UpdateUser обновляет пользователя (только для администраторов)
func (s *UserService) UpdateUser(userID string, req models.UpdateUserRequest) (*models.User, error) {
	user, err := s.userRepo.GetUserByID(userID)
	if err != nil {
		return nil, err
	}

	if req.Username != "" {
		existingUser, err := s.userRepo.GetUserByUsername(req.Username)
		if err != nil {
			return nil, err
		}
		if existingUser != nil && existingUser.ID != userID {
			return nil, errors.New("username already exists")
		}
		user.Username = req.Username
	}

	if req.Email != "" {
		existingUser, err := s.userRepo.GetUserByEmail(req.Email)
		if err != nil {
			return nil, err
		}
		if existingUser != nil && existingUser.ID != userID {
			return nil, errors.New("email already exists")
		}
		user.Email = req.Email
	}

	if req.Password != "" {
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
		if err != nil {
			return nil, err
		}
		user.Password = string(hashedPassword)
	}

	if req.Role != "" {
		if req.Role != "user" && req.Role != "admin" {
			return nil, errors.New("invalid role")
		}
		user.Role = req.Role
	}

	err = s.userRepo.UpdateUser(user)
	if err != nil {
		return nil, err
	}

	return user, nil
}

// DeleteUser удаляет пользователя (только для администраторов)
func (s *UserService) DeleteUser(userID string) error {
	_, err := s.userRepo.GetUserByID(userID)
	if err != nil {
		return errors.New("user not found")
	}

	err = s.userRepo.DeleteUser(userID)
	if err != nil {
		return err
	}

	return nil
}
