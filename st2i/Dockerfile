
FROM maven:3.9-eclipse-temurin-17 AS builder

# Définit le répertoire de travail
WORKDIR /app

# Copie le pom.xml (pour cache Docker)
COPY pom.xml .

# Télécharge les dépendances Maven
# (cette couche est cachée si pom.xml ne change pas)
RUN mvn dependency:resolve -DskipTests

# Copie tout le code source
COPY src ./src

# Compile le projet et crée le JAR
# -DskipTests : saute les tests (tests faits en CI/CD)
# -Dpackaging=jar : format du livrable
RUN mvn clean package -DskipTests && \
    # Affiche le JAR créé
    ls -la target/*.jar

# Récupère le nom du JAR (utile si version variable)
RUN ls -1 target/*.jar | head -n 1 | xargs -I {} cp {} /app/application.jar


FROM openjdk:17-jdk-slim

# Métadonnées de l'image Docker
LABEL maintainer="ST2I Development"
LABEL version="1.0"
LABEL description="Backend Spring Boot 4.0.4 - ST2I PFE"

# Définit le répertoire de travail en runtime
WORKDIR /app

# Copie SEULEMENT le JAR compilé depuis le stage BUILD
# (ne copie pas les sources, dépendances Maven, etc.)
COPY --from=builder /app/application.jar .



# Port exposé (8080 = port par défaut Spring Boot)
EXPOSE 8080

# Variables d'environnement (pour docker-compose)
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/st2i
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV KEYCLOAK_SERVER_URL=http://keycloak:8080
ENV KEYCLOAK_REALM=st2i
ENV KEYCLOAK_CLIENT_ID=backend
ENV KEYCLOAK_CLIENT_SECRET=secret
ENV JAVA_OPTS="-Xms512m -Xmx1g"

# Health check (vérifie que l'app est vivante)
# Lance une requête toutes les 30s, 3 tentatives avant de fail
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1



# Lance le JAR avec optimisations JVM
# -XX:+UseContainerSupport : détecte les limits Docker automatiquement
# -XX:+UseG1GC : algorithme GC optimisé
ENTRYPOINT [ "sh", "-c", \
    "java -XX:+UseContainerSupport -XX:+UseG1GC -Dfile.encoding=UTF-8 $JAVA_OPTS -jar application.jar" ]

