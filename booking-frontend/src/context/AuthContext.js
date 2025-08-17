import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/Auth';

export const AuthContext = createContext();

export const AuthProvider = ({children})=>{
  const [user, setUser] = useState(undefined);
  const [loading, setLoading] = useState(true);

  useEffect(()=>{
    const loadUser = async () =>{
      try {
        const userData = await authService.getProfile();
        setUser(userData);
      } catch(error){
        authService.logout();
      } finally{
        setLoading(false);
      }
    };
    loadUser();
  }, []);

  const login = async (username, password) => {
    const userData = await authService.login(username, password);
    setUser(userData);
    return userData;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const register = async (userData)=>{
    return await authService.register(userData);
  };

  const update = async (userData) => {
    return await authService.updateProfile(userData);
  }

  return (
    <AuthContext.Provider value={{ user, loading, isAuthenticated: !!user, login, logout, register, update }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};