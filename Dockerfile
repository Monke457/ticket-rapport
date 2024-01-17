FROM openjdk:19-jdk-alpine
EXPOSE 8080
ADD target/ticket.jar ticket.jar
ENTRYPOINT ["java","-jar","/ticket.jar"]