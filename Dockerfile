# 빌드 스테이지: 애플리케이션 빌드
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew build -x test

# 실행 스테이지: 애플리케이션 실행
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY firebaseKey/team-prometheus-68425-firebase-adminsdk-fbsvc-2c3d30c54a.json /app/firebaseKey/team-prometheus-68425-firebase-adminsdk-fbsvc-2c3d30c54a.json
ENV FCM_KEY_PATH=/app/firebaseKey/team-prometheus-68425-firebase-adminsdk-fbsvc-2c3d30c54a.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]