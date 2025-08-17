import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './css/Home.css';

const Home = () => {
  const { user } = useAuth();

  return (
    <div className="home-page">
      <section className="hero">
        <h1>Добро пожаловать в коворкинг пространство</h1>
        <p>Удобные рабочие места для продуктивной работы</p>
        
        {user ? (
          <Link to="/bookings" className="cta-button">
            Забронировать место
          </Link>
        ) : (
          <div className="auth-actions">
            <Link to="/login" className="cta-button">
              Войти
            </Link>
            <Link to="/register" className="cta-button secondary">
              Регистрация
            </Link>
          </div>
        )}
      </section>

      <section className="features">
        <div className="feature">
          <h3>Комфортные рабочие места</h3>
          <p>Эргономичные столы и удобные кресла</p>
        </div>
        <div className="feature">
          <h3>Высокоскоростной интернет</h3>
          <p>Стабильное подключение без ограничений</p>
        </div>
        <div className="feature">
          <h3>Гибкое бронирование</h3>
          <p>От 1 часа до целого дня</p>
        </div>
      </section>
    </div>
  );
};

export default Home;