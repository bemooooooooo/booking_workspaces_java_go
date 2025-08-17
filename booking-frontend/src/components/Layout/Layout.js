import Header from './Header';
import Footer from './Footer';
import { useLocation } from 'react-router-dom';
import './Layout.css';

const Layout = ({ children }) => {
  const { pathname } = useLocation();
  return (
    <div className="layout">
      <Header />
      <main className="layout-content">
        {children}
      </main>
      <Footer />
    </div>
  );
};

export default Layout;