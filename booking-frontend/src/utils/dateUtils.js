/**
 * Форматирует дату в строку для API (yyyy-MM-dd HH:mm:ss)
 * @param {Date} date - Объект Date
 * @returns {string} Отформатированная строка даты
 */
export const  formatDateTime = (date) => {
  if (!(date instanceof Date) || isNaN(date)) {
    throw new Error('Invalid Date object');
  }

  const pad = num => num.toString().padStart(2, '0');

  return [
    date.getFullYear(),
    pad(date.getMonth() + 1),
    pad(date.getDate())
  ].join('-') + ' ' + [
    pad(date.getHours()),
    pad(date.getMinutes()),
    pad(date.getSeconds())
  ].join(':');
};

/**
 * Преобразует строку из API в объект Date
 * @param {string} dateStr - Строка даты (yyyy-MM-dd HH:mm:ss)
 * @returns {Date} Объект Date
 */
export const parseApiDate = (dateStr) => {
  if (!dateStr || typeof dateStr !== 'string') {
    throw new Error('Invalid date string');
  }

  const [datePart, timePart] = dateStr.split(' ');
  const [year, month, day] = datePart.split('-').map(Number);
  const [hours, minutes, seconds] = timePart.split(':').map(Number);

  return new Date(year, month - 1, day, hours, minutes, seconds);
};

/**
 * Добавляет указанное количество часов к дате
 * @param {Date} date - Начальная дата
 * @param {number} hours - Количество часов для добавления
 * @returns {Date} Новая дата
 */
export const addHours = (date, hours) => {
  const result = new Date(date);
  result.setHours(result.getHours() + hours);
  return result;
};

/**
 * Проверяет, находится ли дата в рабочее время (9:00-19:00)
 * @param {Date} date - Дата для проверки
 * @returns {boolean}
 */
export const isWithinWorkingHours = (date) => {
  const hours = date.getHours();
  return hours >= 9 && hours < 19;
};

/**
 * Форматирует дату для отображения пользователю
 * @param {Date} date - Объект Date
 * @returns {string} Например: "15 января 2024, 14:30"
 */
export const formatForDisplay = (date) => {
  const options = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  };
  return date.toLocaleDateString('ru-RU', options);
};

/**
 * Сравнивает две даты (без учета времени)
 * @param {Date} date1 - Первая дата
 * @param {Date} date2 - Вторая дата
 * @returns {number} -1 если date1 < date2, 0 если равны, 1 если date1 > date2
 */
export const compareDates = (date1, date2) => {
  const d1 = new Date(date1.getFullYear(), date1.getMonth(), date1.getDate());
  const d2 = new Date(date2.getFullYear(), date2.getMonth(), date2.getDate());
  
  return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
};

/**
 * Генерирует массив временных слотов для бронирования
 * @param {Date} date - Дата
 * @param {number} interval - Интервал в минутах (по умолчанию 30)
 * @returns {Array} Массив строк в формате "HH:MM"
 */
export const generateTimeSlots = (date, interval = 30) => {
  const slots = [];
  const startHour = 9; // Начало рабочего дня
  const endHour = 19;  // Конец рабочего дня
  
  const current = new Date(date);
  current.setHours(startHour, 0, 0, 0);
  
  const end = new Date(date);
  end.setHours(endHour, 0, 0, 0);
  
  while (current < end) {
    slots.push(
      current.toLocaleTimeString('ru-RU', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      }).slice(0, 5)
    );
    current.setMinutes(current.getMinutes() + interval);
  }
  
  return slots;
};

/**
 * Проверяет, доступна ли дата для бронирования (не в прошлом и в рабочее время)
 * @param {Date} date - Дата для проверки
 * @returns {boolean}
 */
export const isDateAvailable = (date) => {
  const now = new Date();
  return date >= now && isWithinWorkingHours(date);
};

export default {
  formatDateTime,
  parseApiDate,
  addHours,
  isWithinWorkingHours,
  formatForDisplay,
  compareDates,
  generateTimeSlots,
  isDateAvailable
};