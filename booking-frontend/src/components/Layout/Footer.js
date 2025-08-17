import Button from '../UI/Button';
const Footer = () => {
  return (
    <footer className="app-footer">
      <div className="container">
        <p>© {new Date().getFullYear()} Коворкинг пространство. Все права защищены.</p>
        <div className="footer-links">
          <Button variant='text' href="/about">О нас</Button>
          <Button variant='text' href="/contacts">Контакты</Button>
          <Button variant='text' href="/terms">Условия использования</Button>
        </div>
      </div>
    </footer>
  );
};

export default Footer;