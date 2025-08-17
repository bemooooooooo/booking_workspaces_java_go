import axios from 'axios';
import { formatDateTime } from '../utils/dateUtils';

const API_URL = process.env.REACT_APP_API_URL;

const api = axios.create({
  baseURL: '/api',
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

export const workspaceService = {
  async getAllWorkspaces() {
    try {
      const response = await api.get('/workspaces');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка загрузки рабочих мест');
    }
  },

  async getWorkspaceById(id) {
    try {
      const response = await api.get(`/workspaces/${id}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) return null;
      throw new Error(error.response?.data?.message || 'Ошибка загрузки рабочего места');
    }
  },

  async getAvailableWorkspaces(startTime, endTime) {
    try {
      const response = await api.get('/workspaces/available', {
        params: {
          startTime: formatDateTime(startTime),
          endTime: formatDateTime(endTime)
        }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка поиска доступных мест');
    }
  },

  async getAvailableWorkspacesWithCapacity(startTime, endTime, minCapacity) {
    try {
      const response = await api.get('/workspaces/available/capacity', {
        params: {
          startTime: formatDateTime(startTime),
          endTime: formatDateTime(endTime),
          minCapacity
        }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка поиска доступных мест');
    }
  },

  // Административные методы
  async createWorkspace(workspaceData) {
    try {
      const response = await api.post('/workspaces', workspaceData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка создания рабочего места');
    }
  },

  async updateWorkspace(id, workspaceData) {
    try {
      const response = await api.put(`/workspaces/${id}`, workspaceData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) return null;
      throw new Error(error.response?.data?.message || 'Ошибка обновления рабочего места');
    }
  },

  async deactivateWorkspace(id) {
    try {
      await api.delete(`/workspaces/${id}`);
      return true;
    } catch (error) {
      if (error.response?.status === 404) return false;
      throw new Error(error.response?.data?.message || 'Ошибка деактивации рабочего места');
    }
  },

  // Вспомогательный метод для получения типов рабочих мест
  // async getWorkspaceTypes() {
  //   const workspaces = await this.getAllWorkspaces();
  //   const types = [...new Set(workspaces.map(ws => ws.type))];
  //   return types.map(type => ({
  //     value: type,
  //     label: type.charAt(0).toUpperCase() + type.slice(1).toLowerCase()
  //   }));
  // }
};

export default workspaceService;