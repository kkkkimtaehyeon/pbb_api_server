## 1. 사용할 베이스 이미지 (Temurin)
#FROM eclipse-temurin:21-jre
## 2. 작업 디렉토리 설정
#WORKDIR /app
## 3. JAR 파일 복사
#COPY target/book-0.0.1-SNAPSHOT.jar /app/book.jar
## 4. 쇼핑몰 서버는 8090, 8091 포트를 사용함.
#EXPOSE 8080
## 5. 컨테이너 시작 시 실행할 명령어
#CMD ["java", "-jar", "book.jar"]

# Stage 1 (Build Stage) - Maven
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 빌드 파일 복사
COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .

# 소스 코드 복사
COPY src/ src/

# 실행 권한 부여
RUN chmod +x ./mvnw

# Maven으로 빌드
RUN ./mvnw package -DskipTests

# ---
# Stage 2 (Runtime Stage) - (Gradle과 동일하지만, JAR 경로만 다름)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Maven은 target/ 폴더에 JAR을 생성합니다.
COPY --from=build /app/target/shop-api.jar shop-api.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "shop-api.jar"]