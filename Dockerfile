# imagem jdk
FROM openjdk:17-jdk-alpine

# diretorio de trabalho
WORKDIR /app

# copia do jar da aplicação
COPY target/testprojeto-0.0.1-SNAPSHOT.jar app.jar

# expor a porta 8080
EXPOSE 8080

# comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
