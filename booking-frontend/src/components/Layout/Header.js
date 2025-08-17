import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Button from '../UI/Button';

const Header = () => {
  const { user, logout, loading } = useAuth();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  if (loading) {
    return null; // или <header>Загрузка...</header>
  }

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleBookings = () => {
    navigate('/bookings');
  };

  const handleProfile = () => {
    navigate('/profile');
  };

  const handleLogin = () => {
    navigate('/login');
  };

  const handleRegister = () => {
    navigate('/register');
  };

  if(user === undefined){
    return null
  }

  return (
    <header className="app-header">
      <div className="container">
        <Link to="/" className="logo">Коворкинг</Link>
        {pathname === '/' && <nav className="nav">
          {user ? (
            <>
              <Button variant="text" onClick={handleBookings}>Мои бронирования</Button>
              <Button variant="text" onClick={handleProfile}>Профиль</Button>
              <Button variant="text" onClick={handleLogout}>Выйти</Button>
            </>
          ) : (
            <>
              <Button variant="text" onClick={handleLogin}>Вход</Button>
              <Button variant="text" onClick={handleRegister}>Регистрация</Button>
            </>
          )}
        </nav>}
      </div>
    </header>
  );
};

export default Header;