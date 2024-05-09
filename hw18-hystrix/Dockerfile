FROM bellsoft/liberica-openjdk-alpine
COPY /target/hw18-hystrix-0.1.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]