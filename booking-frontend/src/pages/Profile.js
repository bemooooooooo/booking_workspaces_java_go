import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import Button from '../components/UI/Button';
import './css/Profile.css';

const ProfilePage = () => {
  const { user, update, logout } = useAuth();
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (user) {
      setFormData({
        username: user.username || '',
        email: user.email || '',
        password: user.password || ''
      });
    }
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleLogout = () => {
    logout();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await update(formData);
      setSuccess('Профиль успешно обновлен');
    } catch (err) {
      setError(err.message || 'Ошибка обновления профиля');
    } finally {
      setLoading(false);
    }
  };

  if (!user) return null;

  return (
    <div className="profile-page">
      <h2>Мой профиль</h2>
      
      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Ник:
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
            />
          </label>
        </div>

        <div className="form-group">
          <label>Email:
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              disabled
            />
          </label>
        </div>

        <div className="form-group">
          <label>Пароль:
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              disabled
            />
          </label>
        </div>

        <Button type="submit" disabled={loading}>
          {loading ? 'Сохранение...' : 'Сохранить изменения'}
        </Button>
        <Button
          variant='danger'
          size='medium'
          onClick={handleLogout}
        >
          Выйти из профиля
        </Button>
      </form>
    </div>
  );
};

export default ProfilePage;