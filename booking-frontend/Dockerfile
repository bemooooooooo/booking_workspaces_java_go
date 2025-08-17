# Используем официальный образ Node.js
FROM node:18-alpine AS builder

# Указываем ARG для сборки
ARG REACT_APP_AUTH_URL
ARG REACT_APP_API_URL

# Устанавливаем переменные в ENV
ENV REACT_APP_AUTH_URL=$REACT_APP_AUTH_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем package.json и package-lock.json
COPY package*.json ./

# Устанавливаем зависимости
RUN npm install --omit=dev

# Копируем исходный код
COPY . .

# Запускаем линтер
# RUN npm run lint -- --max-warnings=10 || echo "Lint errors found"

# RUN find . -type d -name "node_modules" -prune -o -type f -print
# RUN grep -r "context/AuthContext" .
# Собираем приложение
RUN npm run build

# Используем nginx для раздачи статических файлов
FROM nginx:alpine

# Копируем собранное приложение
COPY --from=builder /app/build /usr/share/nginx/html
# COPY --from=builder /app/src /usr/share/nginx/html/src

# Копируем конфигурацию nginx
COPY app.conf /etc/nginx/conf.d/default.conf

# Открываем порт
EXPOSE 80

# Запускаем nginx
CMD ["nginx", "-g", "daemon off;"]