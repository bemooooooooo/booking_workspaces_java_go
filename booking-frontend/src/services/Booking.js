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

export const bookingService = {
  async createReservation(request) {
    try {
      const response = await api.post('/reservations', request);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка создания бронирования');
    }
  },

  async getReservationById(id) {
    try {
      const response = await api.get(`/reservations/${id}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) return null;
      throw new Error(error.response?.data?.message || 'Ошибка получения бронирования');
    }
  },

  async getUserReservations() {
    try {
      const response = await api.get(`/reservations/user`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка загрузки бронирований');
    }
  },

  async getUserActiveReservations() {
    try {
      const response = await api.get(`/reservations/user/active`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка загрузки активных бронирований');
    }
  },

  async cancelReservation(id) {
    try {
      await api.delete(`/reservations/${id}`);
      return true;
    } catch (error) {
      if (error.response?.status === 404) return false;
      throw new Error(error.response?.data?.message || 'Ошибка отмены бронирования');
    }
  },

  async updateReservationTime(id, newStartTime, newEndTime) {
    try {
      const response = await api.put(`/reservations/${id}/time`, null, {
        params: {
          newStartTime: formatDateTime(newStartTime),
          newEndTime: formatDateTime(newEndTime)
        }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) return null;
      throw new Error(error.response?.data?.message || 'Ошибка обновления времени бронирования');
    }
  },

  async getWorkspaceReservations(workspaceId) {
    try {
      const response = await api.get(`/reservations/workspace/${workspaceId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка загрузки бронирований рабочего места');
    }
  },

  async getReservationsInTimeRange(startTime, endTime) {
    try {
      const response = await api.get('/reservations/range', {
        params: {
          startTime: formatDateTime(startTime),
          endTime: formatDateTime(endTime)
        }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Ошибка загрузки бронирований');
    }
  },
};

export default bookingService;