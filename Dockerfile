FROM alpine:edge
RUN apk update && \
    apk add --no-cache openjdk21-jdk gradle bash && \
    rm -rf /var/cache/apk/*

WORKDIR /app

COPY . .
RUN gradle bootJar && mv /app/build/libs/*.jar /app/build/libs/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/build/libs/app.jar"]