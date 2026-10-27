<h1 align="center"> Project 06 : Reducing Docker Image Size Using Multi-Stage Builds</h1>

## 📌 About the Project

In this project, I worked with a simple calculator application written in Go (Golang).

First, I ran the application directly on my EC2 instance. Then, I created a Docker image using Ubuntu and checked its size.

The image was around 252 MB, which was quite large for a small calculator application.

After that, I used a multi-stage Docker build with `scratch` as the final base image. This reduced the image size to around 1.47 MB.

Through this project, I learned how Docker multi-stage builds can help reduce image size.

## 🛠️ Tools and Technologies

- AWS EC2
- Ubuntu Linux
- Go (Golang)
- Docker
- Dockerfile
- Multi-Stage Docker Builds
- Scratch Base Image
- Git and GitHub

## 📂 Project Structure

```text
06 - Reducing Docker Image Size Using Multi-Stage Builds and Distroless Containers/
│
├── dockerfile-without-multistage/
│   ├── Dockerfile
│   └── calculator.go
│
├── dockerfile-with-multistage/
│   └── Dockerfile
│
├── calculator.go
└── README.md
```

## 🚀 Step 1: Clone the Repository

I cloned my Docker learning repository onto my EC2 instance.

```bash
git clone https://github.com/Yuvii2102/Docker-Zero-to-Hero.git

cd Docker-Zero-to-Hero/examples/golang-multi-stage-docker-build
```

## 🧮 Step 2: Run the Calculator Application

First, I installed Go and ran the calculator without Docker.

```bash
sudo apt update
sudo apt install golang-go -y

go run calculator.go
```

The application started successfully.

**Example output:**

```text
Hi Yuvraj, I am a calculator app ....

Enter any calculation: 10 / 2
Result: 5
```

The calculator accepts mathematical expressions as input and displays the result.

## 🐳 Step 3: Build the Docker Image Without Multi-Stage Builds

I used Ubuntu as the base image and installed Go inside the Docker image.

### Dockerfile

```dockerfile
FROM ubuntu AS build

RUN apt-get update && apt-get install -y golang-go

ENV GO111MODULE=off

COPY . .

RUN CGO_ENABLED=0 go build -o /app .

ENTRYPOINT ["/app"]
```

I installed Docker on my EC2 instance.

```bash
sudo apt update
sudo apt install -y docker.io

sudo systemctl enable --now docker
sudo docker --version
sudo docker run hello-world
```

Then I moved into the directory containing the standard Dockerfile and built the image.

```bash
cd dockerfile-without-multistage

sudo docker build -t simplecalculator .
```

I checked the image size using:

```bash
sudo docker images
```

The image size was approximately **252 MB**.

The image included Ubuntu, Go and other packages used during the build. This made the image much larger than the calculator application itself.

## ⚡ Step 4: Build the Multi-Stage Docker Image

Next, I worked with a multi-stage Docker build to reduce the image size.

The idea is to compile the application in a build stage and copy only the compiled binary into the final image.

For this Go application, I used `scratch` as the final base image. It is an empty base image that does not include additional operating-system packages.

From the directory containing the multi-stage Dockerfile, I built the image:

```bash
docker build -t simplecalculator-multistage .
```

I checked the resulting image:

```bash
docker images
```

The image size was approximately **1.47 MB**.

This was much smaller than the standard Docker image.

## 📊 Step 5: Compare the Image Sizes

| Build method | Image size |
|---|---:|
| Without multi-stage build | 252 MB |
| Multi-stage build using `scratch` | 1.47 MB |

The image size was reduced by approximately **99.42%**, based on the sizes reported during my practical.

This showed me how choosing a suitable base image and using multi-stage builds can make Docker images much smaller.

## 🔐 Step 6: Configure Docker Permissions

I also learned how to run Docker commands without using `sudo` every time.

```bash
sudo usermod -aG docker "$USER"
newgrp docker

groups
docker run hello-world
```

The `docker` group provides powerful privileges, so it should only be given to trusted users.

## 📚 What I Learned

- How to run a Go application on an EC2 instance.
- How to write a Dockerfile and build a Docker image.
- How to run and inspect Docker containers.
- How to compare Docker image sizes.
- The difference between a standard Docker build and a multi-stage build.
- How the `scratch` base image can help reduce image size.
- Why smaller Docker images are useful for distributing and deploying applications.

## 🎯 Final Result

I successfully ran and containerized a Go calculator application.

By using a multi-stage Docker build with `scratch`, I reduced the reported image size from approximately 252 MB to 1.47 MB.

This hands-on project helped me understand Docker image optimization and the importance of choosing the right base image.

## 🔗 References

- **Original learning repository:** https://github.com/iam-veeramalla/Docker-Zero-to-Hero
