# Containerizing a Python Django Web Application Using Docker

## 1. About the Project

In this project, I containerized my Python Django web application using Docker.

First, I created my Django application and stored the project files in my GitHub repository. Then I used an AWS EC2 instance to build a Docker image and run the application inside a container.

After testing the application in my browser, I uploaded the Docker image to Docker Hub. This allows me to store and share my image online.

## 2. Tools Used

- **AWS EC2** – To build and run the application on a cloud server.
- **Ubuntu** – The operating system used on my EC2 instance.
- **Docker** – To build images and run containers.
- **Docker Hub** – To store my Docker image online.
- **Git and GitHub** – To store and manage my project files.
- **Python and Django** – To build the web application.
- **HTML and CSS** – To customize the website design.

## 3. Project Workflow

1. Create the Django application and store its files in GitHub.
2. Open the project on an AWS EC2 instance.
3. Build a Docker image.
4. Run the application inside a Docker container.
5. Open the application in a browser and test it.
6. Log in to Docker Hub.
7. Tag the Docker image with my Docker Hub username.
8. Push the image to Docker Hub.
9. Pull the image to verify that it is available.

## 4. Steps I Followed

### Step 1: Open the Project Directory

I entered my project folder on the EC2 instance:

```bash
cd ~/My-Complete-DevOps-Projects/"05 - Containerizing a Python Django Web Application"
```

This folder contains my Dockerfile, requirements file, Django application files, and README.

### Step 2: Build the Docker Image

I used the following command to build my Docker image:

```bash
docker build -t django-app .
```

**What this command means:**

- `docker build` builds a Docker image.
- `-t` gives the image a name.
- `django-app` is the name I chose for my image.
- `.` tells Docker to use the current directory as the build context.

Docker reads the Dockerfile and follows its instructions to build the image.

### Step 3: Check the Docker Image

After building the image, I checked the available images:

```bash
docker images
```

I checked the output to confirm that the `django-app` image was created.

### Step 4: Run the Django Application

I started the application inside a Docker container:

```bash
docker run -p 8000:8000 django-app
```

**What this command means:**

- `docker run` starts a container.
- `-p 8000:8000` connects port 8000 on the EC2 instance to port 8000 inside the container.
- `django-app` is the Docker image used to create the container.

After starting the container, I kept the terminal running while testing the application.

### Step 5: Open the Application in a Browser

I opened the application using the public IP address of my EC2 instance:

```text
http://<EC2-PUBLIC-IP>:8000/demo/
```

I replaced `<EC2-PUBLIC-IP>` with my instance's public IP address.

I also made sure that TCP port 8000 was allowed in the EC2 security group's inbound rules.

The `/demo/` path is used to access the Django page.

After testing the application, I stopped the container by pressing `Ctrl+C`.

### Step 6: Log In to Docker Hub

Next, I logged in to Docker Hub from my EC2 terminal:

```bash
docker login -u yuvi2102
```

I entered my Docker Hub credentials when prompted.

This allowed me to upload my Docker image to my account.

### Step 7: Tag the Docker Image

I gave my image its Docker Hub repository name:

```bash
docker tag django-app yuvi2102/django-app:latest
```

**What this command means:**

- `docker tag` gives an existing image another name or tag.
- `django-app` is my local image name.
- `yuvi2102/django-app` is my Docker Hub repository name.
- `latest` is the image tag.

This command does not rebuild the image. It adds a name that Docker can use when uploading it to Docker Hub.

### Step 8: Push the Image to Docker Hub

I uploaded the image using:

```bash
docker push yuvi2102/django-app:latest
```

Docker uploaded the image layers to my Docker Hub repository.

After the push completed, my image was available in Docker Hub.

### Step 9: Pull the Image to Verify It

I used the following command to check that the image was available from Docker Hub:

```bash
docker pull yuvi2102/django-app:latest
```

Docker confirmed that the image was up to date.

### Step 10: View the Image on Docker Hub

I opened my Docker Hub repository to verify the uploaded image.

**Docker Hub repository:** [yuvi2102/django-app](https://hub.docker.com/r/yuvi2102/django-app)

The image name is:

```text
yuvi2102/django-app:latest
```

The `latest` tag identifies the version I uploaded.

## 5. Complete Commands Used

Here are the main commands used for this project, in order:

```bash
cd ~/My-Complete-DevOps-Projects/"05 - Containerizing a Python Django Web Application"

docker build -t django-app .

docker images

docker run -p 8000:8000 django-app

docker login -u yuvi2102

docker tag django-app yuvi2102/django-app:latest

docker push yuvi2102/django-app:latest

docker pull yuvi2102/django-app:latest
```

**Browser URL:**

```text
http://<EC2-PUBLIC-IP>:8000/demo/
```

## 6. What I Learned

Through this project, I learned:

- How to build a Docker image from a Dockerfile.
- How to check Docker images on an EC2 instance.
- How to run a Django application inside a container.
- How Docker port mapping works.
- How to access a web application using an EC2 public IP address.
- How to log in to Docker Hub.
- How to tag an image with my Docker Hub username.
- How to push and pull Docker images.
- How to verify that my image is available online.

## 7. Conclusion

This project helped me understand how to containerize a Python Django web application using Docker.

I built the Docker image on AWS EC2, ran the application inside a container, tested it in my browser, and uploaded the image to Docker Hub.

Now, I have my project code in GitHub and my Docker image in Docker Hub. This project gave me useful hands-on experience with Docker, AWS EC2, Git and GitHub.

