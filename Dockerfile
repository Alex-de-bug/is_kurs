# Многоступенчатая сборка для Spring Boot + Angular

# Этап 1: Сборка Angular приложения
FROM node:18-alpine AS angular-build

WORKDIR /app/angular

# Копируем package.json и package-lock.json
COPY angular/package*.json ./

# Устанавливаем зависимости
RUN npm ci

# Копируем исходники Angular
COPY angular/ ./

# Собираем Angular приложение для production
RUN npm run build

# Этап 2: Сборка Spring Boot приложения
FROM maven:3.9-eclipse-temurin-17 AS backend-build

WORKDIR /app

# Копируем pom.xml для кэширования зависимостей
COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

# Загружаем зависимости Maven (будет закэшировано если pom.xml не изменился)
RUN mvn dependency:go-offline -B

# Копируем исходники бэкенда
COPY src ./src

# Копируем собранный Angular из предыдущего этапа
COPY --from=angular-build /app/angular/dist/calendar/browser ./src/main/resources/public


# Собираем Spring Boot приложение (пропускаем тесты и checkstyle)
RUN mvn clean package -DskipTests -Dcheckstyle.skip=true -B

# Этап 3: Финальный образ для запуска
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Создаем непривилегированного пользователя
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Копируем собранный jar из этапа сборки
COPY --from=backend-build /app/target/calendar-*.jar app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]