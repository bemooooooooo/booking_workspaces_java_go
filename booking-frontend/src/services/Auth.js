import axios from 'axios';

const API_URL = process.env.REACT_APP_AUTH_URL;

const api = axios.create({
  baseURL: '/auth-api',
  timeout: 10000,
});

// Интерцептор для добавления токена
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Интерцептор для обновления токена
api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) throw new Error('No refresh token');
        
        const { data } = await axios.post(`/auth/refresh`, { refreshToken });
        localStorage.setItem('token', data.access_token);
        if (data.refresh_token) {
          localStorage.setItem('refreshToken', data.refresh_token);
        }
        
        originalRequest.headers.Authorization = `Bearer ${data.access_token}`;
        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        // window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export const authService = {
  async login(username, password) {
    try {
      const { data } = await api.post('/auth/login', {username, password });
      localStorage.setItem('token', data.access_token);
      if (data.refresh_token) {
        localStorage.setItem('refreshToken', data.refreshToken);
      }
      return data.user;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка входа');
    }
  },

  async register(userData) {
    try {
      const { data } = await api.post('/auth/register', userData);
      localStorage.setItem('token', data.access_token);
      if (data.refresh_token) {
        localStorage.setItem('refreshToken', data.refreshToken);
      }
      return data.user;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка регистрации');
    }
  },

  async getProfile() {
    try {
      console.log(API_URL+'/users.profile')
      const { data } = await api.get('/users/profile');
      return data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка загрузки профиля');
    }
  },

  async updateProfile(userData) {
    try {
      const { data } = await api.put('/users/profile', userData);
      return data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка обновления профиля');
    }
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  },

  // Административные функции
  async getAllUsers() {
    try {
      const { data } = await api.get('/admin/users');
      return data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка загрузки пользователей');
    }
  },

  async updateUser(id, userData) {
    try {
      const { data } = await api.put(`/admin/users/${id}`, userData);
      return data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка обновления пользователя');
    }
  },

  async deleteUser(id) {
    try {
      const { data } = await api.delete(`/admin/users/${id}`);
      return data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка удаления пользователя');
    }
  },
};

export default authService;