# ---------- Etapa 1: build da aplicacao com Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia primeiro o pom.xml para aproveitar o cache das dependencias
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copia o codigo-fonte e gera o .jar (os testes rodam no pipeline)
COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Etapa 2: imagem final, apenas com o JRE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Executa a aplicacao com um usuario sem privilegios de root
RUN useradd --system --create-home appuser
USER appuser

COPY --from=build /app/target/biblioteca-cloudtech.jar app.jar

# A porta pode ser definida pela plataforma de hospedagem na variavel PORT
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
