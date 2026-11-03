# Containerizing and Deploying a Super Mario Application Using Docker on AWS EC2

## 1. Project Overview

In this project, I learned how to containerize a real-world JavaScript/TypeScript application using Docker and deploy it on an AWS EC2 instance.

The application used in this project is a Super Mario game application. My goal is to understand the application structure, identify its dependencies, create a Dockerfile, build a Docker image, push the image to Docker Hub and deploy it on AWS EC2.

The original demonstration used Google Cloud Platform. I am following the same approach using AWS as my cloud provider.

### Project Objectives

- Understand the application structure and dependencies.
- Understand how to build a JavaScript application.
- Create a Dockerfile using multi-stage builds.
- Build a Docker image.
- Push the Docker image to Docker Hub.
- Launch an AWS EC2 instance.
- Install Docker on the EC2 instance.
- Pull the Docker image from Docker Hub.
- Run the application inside a Docker container.
- Configure AWS Security Groups to expose the application.
- Access the application through the EC2 public IP address.

### Expected Outcome

At the end of this project, the Super Mario application should be accessible through a web browser using:

`http://<EC2-PUBLIC-IP>:8080`

---

# 2. Why Docker?

Docker has become an important technology in modern software development and DevOps.

There are two major problems that Docker helps solve.

## 2.1 Environment Consistency

One common problem in software development is:

"It works on my machine!"

Imagine a developer develops an application on a Windows machine and tests it successfully.

However, the DevOps team receives the application and deploys it on a Linux server. The application might fail because of differences in:

- Operating systems
- Software versions
- Dependencies
- Configurations
- Runtime environments

Even different Linux distributions can have differences in their configurations.

Docker solves this problem by packaging the application along with its required dependencies into a Docker image.

The same image can then be used across different environments that support the required container runtime.

This makes application deployment more consistent and reliable.

## 2.2 Better Resource Utilization

Before containerization, organizations commonly deployed applications directly on physical servers or virtual machines.

Imagine a physical server has:

- 100 CPU cores
- 100 GB RAM

If an application uses only 2 CPU cores and 2 GB RAM, most of the available resources remain unused.

Virtualization helped solve this problem by dividing physical servers into multiple virtual machines.

However, virtual machines also require their own operating systems and resources.

Docker takes resource utilization further by allowing multiple containers to run on a single virtual machine.

Each container can run a separate application or microservice.

This helps organizations utilize their infrastructure more efficiently.

### Summary

Docker helps solve two major problems:

1. Environment consistency across development and production.
2. Better resource utilization through containers.

---

# 3. Understanding the Application Before Containerization

As a DevOps or Cloud Engineer, I don't necessarily need to develop the entire application or become an expert in every programming language.

However, I should understand three important things:

1. Application structure.
2. Application build process.
3. Application deployment process.

These things help me understand how to containerize and deploy applications developed by different teams.

## 3.1 Application Structure

Different programming languages use different files to manage their dependencies.

| Programming Language | Dependency File |
|---|---|
| Java (Maven) | pom.xml |
| Java (Gradle) | build.gradle |
| Python | requirements.txt |
| JavaScript / TypeScript | package.json |
| Go | go.mod |

For our Super Mario application, we are working with JavaScript/TypeScript.

Therefore, we need to understand the `package.json` file.

## 3.2 What is package.json?

`package.json` is a configuration file used in Node.js projects.

It contains information such as:

- Project name and version.
- Application dependencies.
- Development dependencies.
- Available scripts.
- Project configuration.

Example:

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build"
  }
}
```

In this example, we have two scripts.

**Development server:**

```bash
npm run dev
```

This starts the application in development mode.

**Production build:**

```bash
npm run build
```

This generates production-ready application files.

As a DevOps engineer, I need to identify these commands because they are required when writing the Dockerfile.

## 3.3 What is npm?

npm stands for Node Package Manager.

It is used to install, manage and execute packages and scripts in Node.js applications.

Important commands:

```bash
npm install
npm ci
npm run dev
npm run build
```

### Difference Between npm install and npm ci

| npm install | npm ci |
|---|---|
| Commonly used during development | Commonly used in CI/CD |
| Can update package-lock.json | Uses the existing package-lock.json |
| Can install individual packages | Installs dependencies from the lockfile |
| Does not require a clean installation | Performs a clean installation |

`npm ci` is useful for automated builds because it provides a more consistent dependency installation.

## 3.4 What is Vite?

Vite is a frontend build tool used for modern JavaScript and TypeScript applications.

In our project, Vite is responsible for generating production-ready files.

We use:

```bash
npm run build
```

The build output is generated inside the `dist` directory.

## 3.5 What is the dist Directory?

The `dist` directory contains the production build output generated by the build tool.

It contains the static files required to serve the frontend application, such as:

- HTML files.
- JavaScript files.
- CSS files.
- Images and other assets.

We will copy this directory into the production stage of our Dockerfile.

---

# 4. Understanding Docker Architecture

Before creating the Dockerfile, I need to understand three important Docker components.

## 4.1 Dockerfile

A Dockerfile is a text file that contains instructions to build a Docker image.

It tells Docker:

- Which base image to use.
- Where to create the working directory.
- Which dependencies to install.
- Which application files to copy.
- How to build the application.
- How to start the application.

I can consider a Dockerfile as a recipe for creating a Docker image.

## 4.2 Docker Image

A Docker image is a read-only template used to create containers.

It contains the application files and the required runtime components and dependencies.

For this project, we will create an image named:

`mario-game:v1`

## 4.3 Docker Container

A Docker container is a running instance of a Docker image.

When I execute the Mario Docker image, Docker creates a container and runs the application inside it.

### Docker Workflow

```text
             Dockerfile
                 |
                 | docker build
                 v
             Docker Image
                 |
                 | docker run
                 v
           Docker Container
                 |
                 v
          Running Application
```

### Difference Between Dockerfile, Image and Container

| Dockerfile | Docker Image | Docker Container |
|---|---|---|
| Contains instructions | Packaged template | Running instance |
| Used to build images | Used to create containers | Runs applications |
| Recipe | Package | Running application |

## 4.4 What is Docker Engine?

Docker Engine is the core technology responsible for building and running containers.

It consists of:

- Docker Daemon: Manages Docker objects such as images, containers and networks.
- Docker REST API: Allows communication with the Docker daemon.
- Docker CLI: Allows users to interact with Docker through commands.

---

# 5. Understanding Base Images

A base image provides the starting environment for creating a Docker image.

For example:

```dockerfile
FROM node:18-alpine
```

This means:

- Node: The image includes Node.js.
- 18: Node.js version 18.
- Alpine: Alpine Linux is used as the base operating system.

We don't need to install Node.js separately because it is already available in the selected base image.

## What is Alpine Linux?

Alpine Linux is a lightweight Linux distribution commonly used as a Docker base image.

It helps create smaller container images compared to many full-sized general-purpose Linux images.

Docker Hub provides many prebuilt images containing different operating systems, programming languages and tools.

Examples include:

- Node.js images.
- Python images.
- Java images.
- Go images.
- Nginx images.

Using suitable official base images makes application containerization easier.

---

# 6. Understanding Multi-Stage Docker Builds

Multi-stage builds are one of the most important concepts in this project.

## 6.1 What is a Single-Stage Build?

In a single-stage Docker build, we use one base image to install dependencies, copy source code, build the application and run it.

The final image may contain:

- Development dependencies.
- Source code.
- Build tools.
- Runtime components.
- Production files.

However, many of these files are not required after the application has been built.

This can unnecessarily increase image size and the attack surface.

## 6.2 What is a Multi-Stage Build?

Multi-stage builds allow us to use multiple stages inside a single Dockerfile.

We separate the application build process from the production deployment process.

For this project, we use two stages.

### Stage 1: Build Stage

In the build stage, we:

1. Select a Node.js Alpine base image.
2. Set the working directory.
3. Copy package.json and package-lock.json.
4. Install dependencies using npm ci.
5. Copy the application source code.
6. Build the application using npm run build.

After the build completes, production files are generated inside the `dist` directory.

### Stage 2: Production Stage

In the production stage, we:

1. Select a suitable base image.
2. Set the working directory.
3. Copy the production files from the build stage.
4. Install an HTTP server.
5. Expose port 8080 as image metadata.
6. Configure the startup command.

The final image contains only the components needed to serve the production application.

### Multi-Stage Build Architecture

```text
             DOCKERFILE
                  |
          -----------------
          |               |
          v               v
       STAGE 1          STAGE 2
     BUILD STAGE     PRODUCTION STAGE
          |               |
      Node.js Image    Base Image
          |               |
     Install Packages      |
          |               |
     Copy Source Code      |
          |               |
     npm run build         |
          |               |
      Generate dist        |
          |               |
          --------> Copy dist
                          |
                     Install HTTP
                        Server
                          |
                     Start App
                          |
                     Final Image
```

### Benefits of Multi-Stage Builds

1. Reduces the final image size.
2. Avoids unnecessary development dependencies.
3. Reduces the attack surface.
4. Separates build and production environments.
5. Keeps production images cleaner.

## 6.3 What does AS build mean?

Example:

```dockerfile
FROM node:18-alpine AS build
```

`AS build` assigns the name `build` to the first stage.

We can refer to this stage later when copying files.

## 6.4 What does COPY --from=build mean?

Example:

```dockerfile
COPY --from=build /app/dist ./dist
```

This copies the `dist` directory from the previous build stage into the current stage.

This allows us to transfer only the required production files.

---

# 7. Understanding Dockerfile Instructions

These are the main Dockerfile instructions used in this project.

## 7.1 FROM

Specifies the base image.

```dockerfile
FROM node:18-alpine
```

## 7.2 WORKDIR

Sets the working directory inside the container.

```dockerfile
WORKDIR /app
```

All subsequent relative commands execute from this directory unless otherwise specified.

## 7.3 COPY

Copies files and directories from the build context into the image.

```dockerfile
COPY package*.json ./
```

This copies package.json and package-lock.json into the working directory.

Another example:

```dockerfile
COPY . .
```

This copies the application source code.

## 7.4 RUN

Executes commands during the Docker image build process.

Example:

```dockerfile
RUN npm ci
```

This installs the application dependencies while building the image.

## 7.5 EXPOSE

Documents the port on which the application is expected to listen inside the container.

```dockerfile
EXPOSE 8080
```

Important: EXPOSE does not actually publish the port or open it to the internet.

## 7.6 CMD

Specifies the default command that runs when a container starts.

Example:

```dockerfile
CMD ["http-server", "dist", "-p", "8080"]
```

This starts the HTTP server and serves the files inside the dist directory on port 8080.

## 7.7 Difference Between RUN and CMD

| RUN | CMD |
|---|---|
| Executes during image building | Executes when the container starts |
| Used to install dependencies | Used to start the application |
| Can appear multiple times | Only the last CMD instruction takes effect |

## 7.8 Difference Between COPY and ADD

| COPY | ADD |
|---|---|
| Copies files and directories | Supports additional functionality |
| Simple and predictable | Can extract local tar archives |
| Preferred for normal file copying | Used when its extra features are needed |

We generally prefer COPY when we only need to copy files.

---

# 8. Docker Layer Caching

Docker uses layers while building images.

Each instruction that changes the image filesystem can create a new layer.

For example:

```dockerfile
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build
```

Why do we copy package files before the application source code?

Imagine I modify only one JavaScript file.

My dependencies have not changed.

If the dependency installation layer is still valid, Docker can reuse the cached layer instead of installing all dependencies again.

This reduces unnecessary build time.

### Important Points

- Docker uses caching to avoid repeating unchanged build steps.
- Dependency files should be copied before source code when possible.
- Changing package.json or package-lock.json can invalidate the dependency installation layer.
- Changing only application source files can allow dependency layers to be reused.

---

# 9. Understanding HTTP Server

After building the application, Vite generates static files inside the `dist` directory.

However, users cannot access these files through a browser unless they are served by a web server.

For this project, we use `http-server`.

## What is an HTTP Server?

An HTTP server receives HTTP requests from clients and sends HTTP responses.

It helps users access web application files through a browser.

## Why do we need http-server?

Our production application contains static files.

We need a server to serve those files over HTTP.

We install it using:

```bash
npm install -g http-server
```

We start it using:

```bash
http-server dist -p 8080
```

Here:

- `http-server` starts the HTTP server.
- `dist` specifies the directory containing the production files.
- `-p 8080` specifies the port.

Now the application can be accessed through port 8080, provided the required networking configurations are in place.

Important: This project serves a frontend application. We are not deploying a separate backend service here.

---

# 10. Building a Docker Image

After writing the Dockerfile, the next step is to build the Docker image.

We use:

```bash
docker build -t mario-game:v1 .
```

### Understanding the Command

| Component | Purpose |
|---|---|
| docker build | Builds a Docker image |
| -t | Assigns an image name and tag |
| mario-game | Image name |
| v1 | Image version |
| . | Current directory as build context |

## What is Build Context?

The build context is the collection of files Docker can access during the image build process.

In the command:

```bash
docker build -t mario-game:v1 .
```

The dot represents the current directory.

After building the image, we can verify it using:

```bash
docker images
```

This displays the Docker images available on the local machine.

---

# 11. Understanding Docker Hub

Docker Hub is a cloud-based container registry used to store, share and distribute Docker images.

Imagine I build an image on my local machine.

I need to transfer that image to an AWS EC2 instance.

Instead of manually transferring the image, I can push it to Docker Hub and pull it from EC2.

### Docker Hub Workflow

```text
       Local Machine
             |
             v
        Docker Build
             |
             v
        Docker Image
             |
             v
          Docker Hub
             |
             v
        AWS EC2 Instance
             |
             v
         Docker Pull
             |
             v
        Docker Container
```

## 11.1 What is docker push?

It uploads a local Docker image to a container registry.

```bash
docker push username/mario-game:v1
```

## 11.2 What is docker pull?

It downloads a Docker image from a container registry.

```bash
docker pull username/mario-game:v1
```

## 11.3 What is an Image Tag?

An image tag identifies a particular version or variation of an image.

Example:

`mario-game:v1`

Here:

- mario-game is the image name.
- v1 is the image tag.

Image versioning helps identify releases and deploy specific versions.

It also makes rollback easier when older images are retained.

---

# 12. Running Docker Containers

Once the image is available on AWS EC2, we need to run it.

Example:

```bash
docker run -d -p 8080:8080 --name mario-game mario-game:v1
```

### Understanding the Command

| Component | Meaning |
|---|---|
| docker run | Creates and starts a container |
| -d | Runs the container in the background |
| -p | Maps host and container ports |
| 8080:8080 | Host port 8080 to container port 8080 |
| --name | Assigns a container name |
| mario-game:v1 | Image used to create the container |

## 12.1 What is Detached Mode?

The `-d` option stands for detached mode.

It allows the container to run in the background without occupying the terminal.

## 12.2 What is Interactive Mode?

The `-it` option combines:

- `-i`: Keeps standard input open.
- `-t`: Allocates a pseudo-terminal.

It is useful when we need interactive access to a container.

## 12.3 Understanding Port Mapping

Port mapping connects a port on the host machine to a port inside the container.

Example:

```bash
docker run -d -p 8080:8080 mario-game:v1
```

The first 8080 is the host port on EC2.

The second 8080 is the container port.

```text
          AWS EC2
       Host Port 8080
              |
              v
         Docker Engine
              |
              v
       Docker Container
       Container Port 8080
              |
              v
          HTTP Server
              |
              v
         Mario Game
```

When someone accesses EC2 port 8080, Docker forwards the traffic to container port 8080.

## 12.4 Useful Container Commands

Check running containers:

```bash
docker ps
```

Check all containers:

```bash
docker ps -a
```

View container logs:

```bash
docker logs <container-id>
```

Stop a container:

```bash
docker stop <container-id>
```

Remove a container:

```bash
docker rm <container-id>
```

---

# 13. Understanding AWS EC2

The original demonstration uses Google Compute Engine.

For my implementation, I will use Amazon EC2.

## What is Amazon EC2?

Amazon Elastic Compute Cloud (EC2) is an AWS service that provides scalable virtual servers in the cloud.

We use EC2 to host Docker and run the Super Mario application.

## What is an AMI?

AMI stands for Amazon Machine Image.

It provides the information required to launch an EC2 instance, including its operating system and configuration.

For this project, I will use an Ubuntu AMI.

## Why do we use Ubuntu?

Ubuntu is a Linux-based operating system that supports Docker and provides a suitable environment for running our application.

## Why do we need an EC2 instance?

We need a cloud virtual machine where we can:

1. Install Docker.
2. Pull the application image.
3. Run the Docker container.
4. Expose the application to users.

---

# 14. Understanding AWS Security Groups

A Security Group acts as a virtual firewall for AWS resources such as EC2 instances.

It controls incoming and outgoing network traffic.

## Why do we need a Security Group?

Even if the Docker container is running properly, users might not be able to access the application from the internet.

This can happen because the required inbound traffic is not allowed.

Our HTTP server listens on port 8080.

Therefore, we need to configure the EC2 Security Group to allow inbound TCP traffic on port 8080.

### Example Security Group Rule

| Type | Protocol | Port | Source |
|---|---|---|---|
| Custom TCP | TCP | 8080 | Your public IP |

For learning purposes, some demonstrations allow `0.0.0.0/0`, which permits access from any IPv4 address.

However, restricting the source to my public IP is safer for this practical.

## What is a Public IPv4 Address?

A Public IPv4 address allows an EC2 instance to communicate over the internet, subject to the required networking and security configurations.

We use the EC2 public IP to access the deployed application.

---

# 15. Google Cloud vs AWS

The original instructor used Google Cloud Platform, but I am implementing the same project using AWS.

The main concepts remain the same. Only the cloud-specific services and configurations change.

| Google Cloud Platform | AWS Equivalent |
|---|---|
| Compute Engine | Amazon EC2 |
| VM Instance | EC2 Instance |
| Network Tags | Security Group association |
| Firewall Rules | Security Group Inbound Rules |
| External IP | Public IPv4 Address |
| Cloud Shell / SSH | EC2 Instance Connect / SSH |
| Docker Hub | Docker Hub |

Important: AWS Security Groups and Google Cloud Network Tags are not exactly the same. AWS Security Groups are directly associated with network interfaces, while Google Cloud uses network tags to target firewall rules.

---

# 16. Complete Project Architecture

The following is the overall workflow I will implement.

```text
       GITHUB REPOSITORY
       Super Mario Source Code
                |
                v
       APPLICATION ANALYSIS
       package.json
       package-lock.json
       Source Code
                |
                v
          DOCKERFILE
                |
                v
       MULTI-STAGE BUILD
                |
          -------------
          |           |
          v           v
       BUILD       PRODUCTION
       STAGE         STAGE
          |           |
          v           |
      npm ci          |
          |           |
          v           |
    Copy Source       |
          |           |
          v           |
    npm run build     |
          |           |
          v           |
      dist Folder ----|
                      |
                      v
                FINAL IMAGE
                      |
                      v
                  DOCKER HUB
                      |
                      v
                  AWS EC2
                      |
                      v
                 DOCKER PULL
                      |
                      v
                DOCKER CONTAINER
                      |
                      v
                 HTTP SERVER
                      |
                      v
                MARIO APPLICATION
                      |
                      v
                  USER BROWSER
```

## Complete Deployment Workflow

1. Download or clone the Super Mario application source code from GitHub.
2. Understand the application structure and dependencies.
3. Identify the build and startup commands.
4. Create a multi-stage Dockerfile.
5. Build the application inside the Docker build stage.
6. Copy production files into the final stage.
7. Build the Docker image.
8. Tag and push the image to Docker Hub.
9. Launch an Ubuntu EC2 instance on AWS.
10. Install and verify Docker on EC2.
11. Pull the Docker image from Docker Hub.
12. Run the container in detached mode.
13. Configure the EC2 Security Group to allow inbound traffic on port 8080.
14. Access the application using the EC2 public IP address.

---

# 17. AWS Resources Required

| AWS Resource | Purpose |
|---|---|
| EC2 Instance | Virtual machine to run Docker |
| Ubuntu AMI | Operating system |
| Security Group | Controls network traffic |
| Public IPv4 Address | Allows external access |
| Docker Engine | Builds and runs containers |
| Docker Hub | Stores Docker images |

For this project, I don't need EKS, ECS, Kubernetes, RDS, S3 or a Load Balancer.

This is a basic Docker deployment project using a single EC2 instance.

I will also monitor AWS usage and terminate the EC2 instance when the practical is completed.

---

# 18. Interview Questions and Answers

## Section 1: Docker Fundamentals

### Q1. What is Docker?

Docker is a containerization platform that allows us to package applications along with their dependencies into containers. It helps applications run consistently across different environments.

### Q2. Why do we use Docker?

Docker solves two major problems:

1. Environment consistency across different machines.
2. Better resource utilization by running multiple containers on a single machine.

### Q3. What is the "It works on my machine" problem?

It happens when an application works on a developer's machine but fails on another machine because of differences in operating systems, dependencies, software versions or configurations.

Docker helps solve this by packaging the application and its dependencies into an image.

### Q4. What is a Dockerfile?

A Dockerfile is a text file containing instructions required to build a Docker image.

### Q5. What is a Docker image?

A Docker image is a read-only template containing the application and the required runtime components and dependencies. We use it to create containers.

### Q6. What is a Docker container?

A Docker container is a running instance of a Docker image. It provides an isolated environment to run an application.

### Q7. What is the difference between Dockerfile, Docker image and Docker container?

- Dockerfile contains instructions.
- Docker image is the packaged template.
- Docker container is the running instance of that image.

### Q8. What is Docker Engine?

Docker Engine is the core technology responsible for building and running containers. It includes the Docker daemon, REST API and Docker CLI.

### Q9. What is a base image?

A base image provides the starting environment for building a Docker image.

Example:

```dockerfile
FROM node:18-alpine
```

### Q10. What is Docker Hub?

Docker Hub is a cloud-based container registry where developers and DevOps engineers can store, share, download and distribute Docker images.

---

## Section 2: Application Understanding

### Q11. Why should a DevOps engineer understand the application structure?

To identify application dependencies, understand the build process and determine how to deploy the application.

We don't necessarily need to write the application's source code.

### Q12. What is package.json?

`package.json` is a configuration file used in Node.js projects. It contains project information, dependencies, development dependencies and application scripts.

### Q13. What is npm?

npm stands for Node Package Manager. It is used to install, manage and execute packages and scripts in Node.js applications.

### Q14. What is npm install?

It installs dependencies defined in package.json and updates package-lock.json when necessary.

### Q15. What is npm ci?

It installs dependencies based on package-lock.json. It is commonly used in CI/CD pipelines because it provides consistent dependency installation.

### Q16. What is the difference between npm install and npm ci?

- npm install can update package-lock.json.
- npm ci uses the existing lockfile.
- npm ci performs a clean installation.
- npm install is commonly used during development.
- npm ci is commonly used in automated builds.

### Q17. What is Vite?

Vite is a frontend build tool used to develop and build modern JavaScript and TypeScript applications.

### Q18. What is the difference between npm run dev and npm run build?

- npm run dev starts the development server.
- npm run build generates production-ready files.

### Q19. What is the dist directory?

The dist directory contains the production build output generated by the build tool. It contains the static files required to serve the frontend application.

---

## Section 3: Dockerfile Instructions

### Q20. What is FROM in a Dockerfile?

FROM specifies the base image used to build the Docker image.

Example:

```dockerfile
FROM node:18-alpine
```

### Q21. What is Alpine Linux?

Alpine Linux is a lightweight Linux distribution commonly used as a Docker base image to create smaller container images.

### Q22. What is WORKDIR?

WORKDIR sets the working directory inside the container.

Example:

```dockerfile
WORKDIR /app
```

### Q23. What is COPY?

COPY copies files and directories from the build context into the Docker image.

Example:

```dockerfile
COPY package*.json ./
```

### Q24. What is RUN?

RUN executes commands during the Docker image build process.

Example:

```dockerfile
RUN npm ci
```

### Q25. What is EXPOSE?

EXPOSE documents the port on which the application is expected to listen inside the container.

It does not actually publish the port.

### Q26. What is CMD?

CMD specifies the default command that runs when a container starts.

Example:

```dockerfile
CMD ["http-server", "dist", "-p", "8080"]
```

### Q27. What is the difference between RUN and CMD?

- RUN executes during image building.
- CMD executes when the container starts.
- RUN is generally used to install dependencies.
- CMD is generally used to start the application.

### Q28. What is the difference between COPY and ADD?

COPY copies files and directories into the image.

ADD supports additional features such as extracting local tar archives and handling remote URLs.

We generally prefer COPY for normal file-copying operations.

---

## Section 4: Multi-Stage Docker Builds

### Q29. What is a multi-stage Docker build?

Multi-stage Docker builds allow us to use multiple stages in one Dockerfile.

We use one stage to build the application and another stage to create the final production image.

### Q30. Why do we use multi-stage builds?

We use them to:

1. Reduce final image size.
2. Avoid unnecessary development dependencies.
3. Reduce the attack surface.
4. Keep production images cleaner.

### Q31. What is a build stage?

The build stage contains the tools and dependencies required to build the application.

In our project, we use Node.js to install dependencies and generate production files.

### Q32. What is a production stage?

The production stage creates the final image containing only the required files and runtime components to serve the application.

### Q33. What does AS build mean?

Example:

```dockerfile
FROM node:18-alpine AS build
```

It assigns the name `build` to that Docker build stage.

### Q34. What does COPY --from=build mean?

It copies files from a previous build stage into the current stage.

Example:

```dockerfile
COPY --from=build /app/dist ./dist
```

### Q35. Why do we copy package.json before copying source code?

Docker uses layer caching.

By copying dependency files first and installing dependencies separately, Docker can reuse the dependency installation layer when application source code changes but dependency files remain unchanged.

This reduces unnecessary rebuild time.

---

## Section 5: HTTP Server and Networking

### Q36. What is an HTTP server?

An HTTP server receives HTTP requests from clients and sends HTTP responses.

### Q37. Why do we need http-server in this project?

After building the application, Vite generates static files inside the dist directory.

We need an HTTP server to serve those files so users can access the application through a browser.

### Q38. What does this command do?

```bash
npm install -g http-server
```

It installs the HTTP server globally inside the current environment.

### Q39. What is port 8080?

Port 8080 is the port on which our HTTP server listens for incoming application requests.

### Q40. What is port mapping in Docker?

Port mapping connects a port on the host machine to a port inside the container.

Example:

```bash
docker run -d -p 8080:8080 mario-game:v1
```

### Q41. What is the difference between EXPOSE and -p?

- EXPOSE documents the container port.
- -p publishes the container port by mapping it to the host port.

### Q42. What happens when a user accesses our application?

The request travels through this path:

```text
User Browser
     |
     v
Internet
     |
     v
AWS Security Group
     |
     v
EC2 Public IP : 8080
     |
     v
Docker Port Mapping
     |
     v
Container Port 8080
     |
     v
HTTP Server
     |
     v
Super Mario Application
```

---

## Section 6: Docker Image and Container Commands

### Q43. How do you build a Docker image?

```bash
docker build -t mario-game:v1 .
```

This builds an image named mario-game with version v1.

### Q44. How do you list Docker images?

```bash
docker images
```

### Q45. How do you run a Docker container in the background?

```bash
docker run -d -p 8080:8080 mario-game:v1
```

### Q46. What does -d mean?

It stands for detached mode. It runs the container in the background.

### Q47. What does -it mean?

- -i keeps standard input open.
- -t allocates a pseudo-terminal.

Together they allow interactive terminal access to a container.

### Q48. How do you check running containers?

```bash
docker ps
```

### Q49. How do you check all containers, including stopped containers?

```bash
docker ps -a
```

### Q50. How do you check container logs?

```bash
docker logs <container-id>
```

### Q51. How do you stop a container?

```bash
docker stop <container-id>
```

### Q52. How do you remove a container?

```bash
docker rm <container-id>
```

### Q53. How do you remove a Docker image?

```bash
docker rmi <image-id>
```

---

## Section 7: Docker Hub

### Q54. Why do we push Docker images to Docker Hub?

We push images to Docker Hub so they can be stored centrally and accessed from different machines.

In our project, we build the image, push it to Docker Hub and pull it from AWS EC2.

### Q55. What is docker push?

It uploads a local Docker image to a container registry.

```bash
docker push username/mario-game:v1
```

### Q56. What is docker pull?

It downloads a Docker image from a container registry.

```bash
docker pull username/mario-game:v1
```

### Q57. What is an image tag?

An image tag identifies a particular version or variation of an image.

Example:

`mario-game:v1`

Here, v1 is the tag.

### Q58. Why should we use image versioning?

Versioning helps us identify different application releases, deploy specific versions and roll back to earlier versions when required.

---

## Section 8: AWS Cloud Concepts

### Q59. What is Amazon EC2?

Amazon Elastic Compute Cloud (EC2) is an AWS service that provides scalable virtual servers in the cloud.

We use EC2 to install Docker and run our Super Mario application.

### Q60. Why do we use EC2 in this project?

We need a cloud virtual machine to host Docker and run our application so that it can be accessed over the internet.

### Q61. What is an AMI?

AMI stands for Amazon Machine Image.

It provides the information required to launch an EC2 instance, including its operating system and configuration.

### Q62. Why do we use Ubuntu?

Ubuntu is a Linux-based operating system that supports Docker and provides a suitable environment for running our application.

### Q63. What is a Security Group?

A Security Group is a virtual firewall associated with AWS resources such as EC2 instances.

It controls inbound and outbound traffic.

### Q64. Why do we open port 8080 in the Security Group?

Our HTTP server listens on port 8080.

We need to allow inbound TCP traffic on port 8080 so that users can access our application.

### Q65. What is a Public IPv4 address?

A Public IPv4 address allows an EC2 instance to communicate over the internet, subject to networking and security configurations.

### Q66. What happens if our Docker container is running but the application is not accessible?

We should troubleshoot:

1. Check whether the container is running.
2. Check application logs.
3. Verify that the HTTP server listens on port 8080.
4. Verify Docker port mapping.
5. Check EC2 Security Group inbound rules.
6. Verify the EC2 public IP and internet connectivity.

---

## Section 9: Google Cloud vs AWS

### Q67. The instructor used Google Cloud. Why are we using AWS?

The concepts of virtual machines, containerization, image registries and networking are common across cloud platforms.

Only the cloud-specific configurations and commands may differ.

### Q68. What is the difference between Google Cloud Network Tags and AWS Security Groups?

Google Cloud uses network tags to associate certain firewall rules with VM instances.

AWS uses Security Groups, which are directly associated with network interfaces.

Both help control network access, but their implementations differ.

### Q69. What is the AWS equivalent of Google Compute Engine?

Amazon EC2.

### Q70. What is the AWS equivalent of a Google Cloud VM instance?

An Amazon EC2 instance.

---

# 19. Explaining the Project in an Interview

### Question: Explain your Super Mario application containerization project.

**Answer:**

I worked on containerizing and deploying a JavaScript/TypeScript-based Super Mario application using Docker on AWS EC2.

First, I analyzed the application structure and identified its dependencies and build commands using the package.json file.

Then I created a multi-stage Dockerfile. In the first stage, I used a Node.js Alpine base image, installed the dependencies using npm ci, copied the application source code and generated production files using npm run build.

In the second stage, I copied only the production build files from the first stage and configured an HTTP server to serve the application on port 8080.

After that, I built the Docker image and pushed it to Docker Hub.

Next, I launched an Ubuntu EC2 instance on AWS, installed Docker and pulled the image from Docker Hub.

Finally, I ran the container in detached mode with port mapping and configured the EC2 Security Group to allow inbound traffic on port 8080.

I verified the deployment by accessing the application through the EC2 public IP address in a browser.

---

# 20. Complete Learning Checklist

I will use this checklist to track my understanding and progress throughout the project.

## Docker Fundamentals

- [ ] Understand Docker, images and containers.
- [ ] Understand Docker Engine.
- [ ] Understand Docker Hub.
- [ ] Understand base images.

## Application Understanding

- [ ] Understand package.json.
- [ ] Understand npm and npm ci.
- [ ] Understand npm run build.
- [ ] Understand Vite and the dist directory.

## Dockerfile

- [ ] Understand FROM, WORKDIR and COPY.
- [ ] Understand RUN, EXPOSE and CMD.
- [ ] Understand multi-stage builds.
- [ ] Understand Docker layer caching.

## Docker Image Deployment

- [ ] Build Docker image.
- [ ] Tag and push image to Docker Hub.
- [ ] Pull image on EC2.
- [ ] Run container in detached mode.

## AWS Deployment

- [ ] Launch Ubuntu EC2 instance.
- [ ] Install and verify Docker.
- [ ] Configure Security Group.
- [ ] Map port 8080.
- [ ] Access application using public IP.

## Interview Preparation

- [ ] Revise all 70 interview questions.
- [ ] Understand the complete project architecture.
- [ ] Practise explaining the project.
- [ ] Understand common deployment problems and troubleshooting.

---

# 21. Final Project Summary

Through this project, I am learning how to take an existing application and deploy it using Docker on AWS.

The main concepts covered are:

- Application analysis.
- Dependency management.
- Dockerfile creation.
- Multi-stage Docker builds.
- Docker layer caching.
- Docker image creation.
- Docker Hub image management.
- Container deployment.
- AWS EC2.
- Security Groups.
- Port mapping.
- Application accessibility.

My main goal is not just to execute Docker commands but to understand why each step is required and how these concepts are used in real-world DevOps environments.

**Learning approach:** Understand the theory first, perform the practical step by step, document the commands and outputs, and finally revise the interview questions.

