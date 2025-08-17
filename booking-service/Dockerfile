# Используем официальный образ OpenJDK 17 с Maven
FROM maven:3.9-eclipse-temurin-17 AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml
COPY pom.xml .

# Скачиваем зависимости (этот слой будет кэшироваться)
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src src

# Собираем приложение
RUN mvn clean package -DskipTests

# Создаем новый образ с JRE
FROM eclipse-temurin:17-jre

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR файл
COPY --from=0 /app/target/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Устанавливаем переменные окружения
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 