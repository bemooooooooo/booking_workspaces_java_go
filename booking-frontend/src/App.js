import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout/Layout';
import Routes from './Routes';

function AuthContent() {
  const { loading } = useAuth();

  if (loading) {
    return (
      <div className="auth-loading-overlay">
        <div className="auth-loader">Проверка авторизации...</div>
      </div>
    );
  }

  return (
    <Layout>
      <Routes />
    </Layout>
  );
}

function App() {
  return (
    <AuthProvider>
      <AuthContent />
    </AuthProvider>
  );
}

export default App;