import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Button from '../components/UI/Button';
import './css/AuthPage.css';

const Register = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (formData.password !== formData.confirmPassword) {
      setError('Пароли не совпадают');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await register(formData);
      navigate('/login', { state: { registrationSuccess: true } });
    } catch (error) {
      if (error.response) {
      // Ошибка от сервера
      const { code, message } = error.response.data;
      let errorMessage;
      // Специфичные коды ошибок
      switch(code) {
        case 'USERNAME_EXIST':
          errorMessage = 'Пользователь с таким ником уже зарегистрирован';
          break;
        case 'EMAIL_EXISTS':
          errorMessage = 'Пользователь с таким email уже зарегистрирован';
          break;
        case 'INVALID_EMAIL':
          errorMessage = 'Некорректный email';
          break;
        case 'IVALID_CREDENTIALS':
          errorMessage = 'Некорректно введены данные'
          break;
        default:
          errorMessage = message || errorMessage;
      }
      setError(errorMessage)
    } else {
      // Другие ошибки (нет соединения и т.д.)
      setError('Не удалось подключиться к серверу');
    }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Регистрация</h1>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Ник:</label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              minLength="2"
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email:</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Пароль:</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              minLength="6"
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Подтвердите пароль:</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
              minLength="6"
            />
          </div>

          <Button
            type="submit" 
            disabled={loading}
          >
            {loading ? 'Регистрация...' : 'Зарегистрироваться'}
          </Button>
        </form>

        <div className="auth-footer">
          Уже есть аккаунт? <a href="/login">Войдите</a>
        </div>
      </div>
    </div>
  );
};

export default Register;