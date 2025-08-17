package customErrors

// The type CustomError defines a custom error structure with Code and Message fields.
// @property {string} Code - Code is a string field in the CustomError struct that represents the error
// code or identifier associated with the custom error.
// @property {string} Message - The `Message` property in the `CustomError` struct is typically used to
// store a human-readable description or explanation of the error that occurred. It provides additional
// context to help developers understand what went wrong when the error was triggered.
type CustomError struct {
	Code    string
	Message string
}

func (e *CustomError) Error() string {
	return e.Message
}

// The `const` block in the code snippet is defining a set of error code constants. Each constant
// represents a specific type of error that can occur in the application. Here's a breakdown of each
// constant:
const (
	EmailExistsCode    = "EMAIL_EXIST"
	UsernameExistsCode = "USERNAME_EXIST"
	InvalidEmailCode   = "INVALID_EMAIL"
	InvalidCredentials = "INVALID_CREDENTIALS"
)

func NewCustomError(code, message string) *CustomError {
	return &CustomError{Code: code, Message: message}
}
