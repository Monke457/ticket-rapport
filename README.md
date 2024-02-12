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
Password: t-EB?{Qtz2M8D3[77bLC?FI

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

## Login credentials

You can log in to the app using the following username and password

#### Admin
```
username: admin@ticket-kauz.ch
password: Pass123!
```

#### Learner
```
username: learner@ticket-kauz.ch
password: Pass123!
```