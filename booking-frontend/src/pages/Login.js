import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Button from '../components/UI/Button';
import './css/AuthPage.css';

const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await login(username, password);
      navigate('/');
    } catch (error) {
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
    } finally {
      setLoading(false);
    }
  };
  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Вход в систему</h1>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Ник:
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </label>
          </div>
          <div className="form-group">
            <label>Пароль:
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </label>
          </div>
          <Button type="submit" disabled={loading}>
            {loading ? 'Вход...' : 'Войти'}
          </Button>
        </form>
        <div className="auth-footer">
          Нет аккаунта? <a href="/register">Зарегистрируйтесь</a>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;