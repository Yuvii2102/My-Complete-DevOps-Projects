<h1 align="center">Project 07: Java Docker Image Optimization Using Multi-Stage Builds</h1>

## 📌 Project Overview

In this project, I containerized a Java application using Docker and compared two approaches:

- **Without Multi-Stage Build:** Uses Ubuntu and installs Java inside the image.
- **With Multi-Stage Build:** Uses a separate build stage to compile the Java application and a lightweight Distroless image to run it.

The main goal is to reduce Docker image size while keeping the application working correctly.

## 🎯 Project Objectives

- Understand how to containerize a Java application using Docker.
- Build Docker images with and without multi-stage builds.
- Reduce image size using multi-stage builds.
- Understand the benefits of lightweight Distroless images.
- Compare Docker image sizes and learn how to optimize containers.

## 🛠️ Technologies Used

- Java 21
- Docker
- Ubuntu
- Eclipse Temurin JDK
- Distroless Java Runtime
- Linux
- Git and GitHub
- AWS EC2

## 📁 Project Structure

```text
07 - Java Docker Image Optimization/
│
├── README.md
│
├── without-multistage/
│   ├── Dockerfile
│   └── Main.java
│
└── with-multistage/
    ├── Dockerfile
    └── Main.java
```

## 🐳 1. Docker Image Without Multi-Stage Build

In the first approach, I used Ubuntu as the base image and installed the Java Development Kit (JDK).

The application was compiled and executed inside the same Docker image.

### Dockerfile

```dockerfile
FROM ubuntu

RUN apt-get update && apt-get install -y openjdk-21-jdk

WORKDIR /app

COPY Main.java .

RUN javac Main.java

ENTRYPOINT ["java", "Main"]
```

### Build the Docker Image

```bash
docker build -t without-multistage ./without-multistage
```

### Run the Container

```bash
docker run -it --rm without-multistage
```

**Result:** The Docker image size was approximately **394 MB**.

## 🚀 2. Docker Image With Multi-Stage Build

In the second approach, I used a multi-stage Docker build.

The first stage compiles the Java application using the JDK. The second stage uses a lightweight Distroless Java runtime image and copies only the compiled class files required to run the application.

This avoids including the full JDK and unnecessary build tools in the final image.

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY Main.java .

RUN javac Main.java

FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app

COPY --from=build /app/Main.class .
COPY --from=build /app/Food.class .

ENTRYPOINT ["java", "-cp", "/app", "Main"]
```

### Build the Docker Image

```bash
docker build -t with-multistage ./with-multistage
```

### Run the Container

```bash
docker run -it --rm with-multistage
```

**Result:** The Docker image size was approximately **63.2 MB**.

## 📊 Image Size Comparison

| Metric | Without Multi-Stage | With Multi-Stage |
|---|---:|---:|
| Base image | Ubuntu | Distroless Java |
| Build approach | Single stage | Multi-stage |
| Image size | 394 MB | 63.2 MB |
| JDK in final image | Yes | No |
| Final image optimization | Basic | Optimized |

### Results

- **Original image size:** 394 MB
- **Optimized image size:** 63.2 MB
- **Space saved:** 330.8 MB
- **Image size reduction:** Approximately 84%

The multi-stage approach significantly reduced the final Docker image size.

## 🧠 Key Learnings

Through this project, I learned:

- How to package a Java application inside a Docker container.
- How to write Dockerfiles for Java applications.
- How multi-stage builds separate compilation from application execution.
- How Distroless images help create smaller runtime images.
- How to compare Docker image sizes and measure optimization.
- Why smaller images can reduce storage usage and image transfer time.

## 🧹 Cleanup

To remove the containers and images created during testing, use:

```bash
docker ps -a
docker images
```

Remove the images after confirming that you no longer need them:

```bash
docker rmi without-multistage with-multistage
```

## ✅ Conclusion

In this project, I compared a standard Docker build with a multi-stage Docker build for a Java application.

By using a multi-stage build and a Distroless Java runtime, I reduced the image size from **394 MB to 63.2 MB**, saving approximately **84%** of the original image size.

This project helped me understand how Docker image optimization improves container efficiency and why choosing the right base image matters in DevOps.

**Project completed as part of my DevOps and Cloud learning journey.** 

