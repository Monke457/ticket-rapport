# Ticket Rapport

## Running the application for local development
Build the Jar

```
mvn -DskipTests clean package
```

Run the application
```
mvn spring-boot:run
```

MySQL administrator credentials:

Username: root
Password: 6MD?!<&uri3NG|O1MlNDY6

## Deploying using Docker

To build the Dockerized version of the project, package the jar

```
mvn -DskipTests clean package
```

Then build the image

```
docker-compose build
```

Once the Docker image is correctly built, you can test it locally using

```
docker-compose up
```
