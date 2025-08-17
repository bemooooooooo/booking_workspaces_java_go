package repository

import (
	"auth-service/models"
	"database/sql"
	"fmt"
	"time"
)

// UserRepository представляет репозиторий для работы с пользователями
type UserRepository struct {
	db *sql.DB
}

// NewUserRepository создает новый экземпляр UserRepository
func NewUserRepository(db *sql.DB) *UserRepository {
	return &UserRepository{db: db}
}

// CreateUser создает нового пользователя
func (r *UserRepository) CreateUser(user *models.User) error {
	query := `
		INSERT INTO auth.users (username, email, password_hash, role, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6)
		RETURNING id`

	now := time.Now()
	err := r.db.QueryRow(
		query,
		user.Username,
		user.Email,
		user.Password,
		user.Role,
		now,
		now,
	).Scan(&user.ID)

	if err != nil {
		return fmt.Errorf("failed to create user: %v", err)
	}

	user.CreatedAt = now
	user.UpdatedAt = now
	return nil
}

// GetUserByUsername получает пользователя по имени пользователя
func (r *UserRepository) GetUserByUsername(username string) (*models.User, error) {
	query := `
		SELECT id, username, email, password_hash, role, created_at, updated_at
		FROM auth.users
		WHERE username = $1`

	user := &models.User{}
	err := r.db.QueryRow(query, username).Scan(
		&user.ID,
		&user.Username,
		&user.Email,
		&user.Password,
		&user.Role,
		&user.CreatedAt,
		&user.UpdatedAt,
	)

	if err != nil {
		if err == sql.ErrNoRows {
			return nil, nil
		}
		return nil, fmt.Errorf("failed to get user by username: %v", err)
	}

	return user, nil
}

// GetUserByEmail получает пользователя по email
func (r *UserRepository) GetUserByEmail(email string) (*models.User, error) {
	query := `
		SELECT id, username, email, password_hash, role, created_at, updated_at
		FROM auth.users
		WHERE email = $1`

	user := &models.User{}
	err := r.db.QueryRow(query, email).Scan(
		&user.ID,
		&user.Username,
		&user.Email,
		&user.Password,
		&user.Role,
		&user.CreatedAt,
		&user.UpdatedAt,
	)

	if err != nil {
		if err == sql.ErrNoRows {
			return nil, nil
		}
		return nil, fmt.Errorf("failed to get user by email: %v", err)
	}

	return user, nil
}

// GetUserByID получает пользователя по ID
func (r *UserRepository) GetUserByID(id string) (*models.User, error) {
	query := `
		SELECT id, username, email, password_hash, role, created_at, updated_at
		FROM auth.users
		WHERE id = $1`

	user := &models.User{}
	err := r.db.QueryRow(query, id).Scan(
		&user.ID,
		&user.Username,
		&user.Email,
		&user.Password,
		&user.Role,
		&user.CreatedAt,
		&user.UpdatedAt,
	)

	if err != nil {
		if err == sql.ErrNoRows {
			return nil, nil
		}
		return nil, fmt.Errorf("failed to get user by ID: %v", err)
	}

	return user, nil
}

// UpdateUser обновляет пользователя
func (r *UserRepository) UpdateUser(user *models.User) error {
	query := `
		UPDATE auth.users
		SET username = $1, email = $2, password_hash = $3, role = $4, updated_at = $5
		WHERE id = $6`

	user.UpdatedAt = time.Now()
	result, err := r.db.Exec(
		query,
		user.Username,
		user.Email,
		user.Password,
		user.Role,
		user.UpdatedAt,
		user.ID,
	)

	if err != nil {
		return fmt.Errorf("failed to update user: %v", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to get rows affected: %v", err)
	}

	if rowsAffected == 0 {
		return fmt.Errorf("user not found")
	}

	return nil
}

// GetAllUsers получает всех пользователей
func (r *UserRepository) GetAllUsers() ([]models.User, error) {
	query := `
		SELECT id, username, email, password_hash, role, created_at, updated_at
		FROM auth.users
		ORDER BY created_at DESC`

	rows, err := r.db.Query(query)
	if err != nil {
		return nil, fmt.Errorf("failed to get all users: %v", err)
	}
	defer rows.Close()

	var users []models.User
	for rows.Next() {
		user := models.User{}
		err := rows.Scan(
			&user.ID,
			&user.Username,
			&user.Email,
			&user.Password,
			&user.Role,
			&user.CreatedAt,
			&user.UpdatedAt,
		)
		if err != nil {
			return nil, fmt.Errorf("failed to scan user: %v", err)
		}
		users = append(users, user)
	}

	if err = rows.Err(); err != nil {
		return nil, fmt.Errorf("error iterating over users: %v", err)
	}

	return users, nil
}

// DeleteUser удаляет пользователя
func (r *UserRepository) DeleteUser(id string) error {
	query := `DELETE FROM auth.users WHERE id = $1`

	result, err := r.db.Exec(query, id)
	if err != nil {
		return fmt.Errorf("failed to delete user: %v", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to get rows affected: %v", err)
	}

	if rowsAffected == 0 {
		return fmt.Errorf("user not found")
	}

	return nil
} 