# MERN Stack Application – Complete Deployment Steps

## Project Overview

In this project, I deployed a MERN stack application on an AWS EC2 instance using Docker and Docker Compose.

The application contains three main components:

1. Frontend – React.js
2. Backend – Node.js and Express.js
3. Database – MongoDB

The project has two deployment methods:

- Manual Docker Deployment
- Docker Compose Deployment

---

# Part 1: EC2 Setup

### 1. Connect to your EC2 instance

Connect to your Ubuntu EC2 instance using SSH through MobaXterm.

### 2. Check memory and storage

```bash
free -h
df -h
```

- `free -h` – Displays available memory.
- `df -h` – Displays available disk space.

### 3. Install Git

```bash
sudo apt update
sudo apt install git -y
```

Check the Git version:

```bash
git --version
```

### 4. Start Docker

After installing Docker and Docker Compose, start the Docker service.

```bash
sudo systemctl start docker
sudo systemctl enable docker
sudo systemctl status docker
sudo systemctl is-active docker
```

- `start` – Starts the Docker service.
- `enable` – Starts Docker automatically when the system boots.
- `status` – Displays the Docker service status.
- `is-active` – Checks whether Docker is active.

### 5. Configure Docker permissions

Add the Ubuntu user to the Docker group.

```bash
sudo usermod -aG docker ubuntu
```

Apply the group change:

```bash
newgrp docker
```

Now I can execute Docker commands without using `sudo` every time.

---

# Part 2: Clone the Project

### 6. Clone the GitHub repository

```bash
git clone https://github.com/iam-veeramalla/MERN-docker-compose.git
```

Go inside the project directory:

```bash
cd MERN-docker-compose
```

### 7. Check branches

```bash
git branch -a
```

Switch to the Compose branch:

```bash
git checkout compose
```

The `compose` branch contains the Dockerfiles and Docker Compose configuration.

### 8. Check the project structure

```bash
ls -R
```

This command displays all the files and folders inside the project.

---

# Part 3: Build the Frontend Image

### 9. Go inside the frontend directory

```bash
cd mern/frontend
```

### 10. Check the Dockerfile

Open the Dockerfile using Vim:

```bash
vim Dockerfile
```

Display its contents:

```bash
cat Dockerfile
```

### 11. Configure the backend API address

Before building the frontend image, configure the backend API address in the frontend source code.

Use the current EC2 public IP address.

```text
http://EC2_PUBLIC_IP:5050
```

This allows the frontend application in the browser to communicate with the backend running on EC2.

### 12. Build the frontend image

```bash
docker build -t myfrontend .
```

This command builds the frontend Docker image using the Dockerfile.

- `docker build` – Builds a Docker image.
- `-t myfrontend` – Gives the image the name `myfrontend`.
- `.` – Uses the current directory as the build context.

### 13. Verify the frontend image

```bash
docker images
```

Check whether the `myfrontend` image was created successfully.

---

# Part 4: Create the Docker Network

### 14. Create the network

```bash
docker network create mern
```

This creates a Docker network named `mern`.

**Why do we need a Docker network?**

Our application contains three separate containers:

1. Frontend
2. Backend
3. MongoDB

These containers need to communicate with each other.

The Docker network acts like a common connection between the containers.

It allows the backend to communicate with MongoDB and the frontend container to communicate with the backend container.

---

# Part 5: Run MongoDB

### 15. Start the MongoDB container

```bash
docker run -d --network mern --name mongodb -p 27017:27017 -v ~/opt/data:/data/db mongo:latest
```

**Command explanation:**

- `docker run` – Creates and starts a container.
- `-d` – Runs the container in the background.
- `--network mern` – Connects the container to the `mern` network.
- `--name mongodb` – Gives the container the name `mongodb`.
- `-p 27017:27017` – Maps port 27017.
- `-v ~/opt/data:/data/db` – Connects the EC2 directory to the MongoDB data directory inside the container.
- `mongo:latest` – Uses the MongoDB image.

### 16. Verify the MongoDB image

```bash
docker images
```

Check whether the MongoDB image is available.

### 17. Check the MongoDB connection

```bash
docker exec mongodb mongosh --eval "db.runCommand({ ping: 1 })"
```

Expected response:

```text
{ ok: 1 }
```

This confirms that MongoDB is responding.

---

# Part 6: Build the Backend Image

### 18. Go inside the backend directory

From the `mern/frontend` directory:

```bash
cd ../backend
```

### 19. Check the Dockerfile

Open the Dockerfile:

```bash
vim Dockerfile
```

Display its contents:

```bash
cat Dockerfile
```

### 20. Build the backend image

```bash
docker build -t mybackend .
```

This command builds the backend Docker image using the Dockerfile.

### 21. Verify the backend image

```bash
docker images
```

Check whether the `mybackend` image was created successfully.

---

# Part 7: Run the Backend Container

### 22. Start the backend container

```bash
docker run -d --network mern --name backend -p 5050:5050 mybackend
```

**Command explanation:**

- `docker run` – Creates and starts a container.
- `-d` – Runs the container in the background.
- `--network mern` – Connects the container to the `mern` network.
- `--name backend` – Gives the container the name `backend`.
- `-p 5050:5050` – Maps port 5050.
- `mybackend` – Uses the backend image.

### Backend MongoDB connection

The backend connection string should be:

```javascript
const URI = "mongodb://mongodb:27017";
```

The hostname `mongodb` matches the MongoDB container name.

Since both containers are connected to the same `mern` network, the backend can communicate with MongoDB using this name.

---

# Part 8: Run the Frontend Container

### 23. Start the frontend container

```bash
docker run -d --network mern --name frontend -p 5173:5173 myfrontend
```

**Command explanation:**

- `docker run` – Creates and starts a container.
- `-d` – Runs the container in the background.
- `--network mern` – Connects the container to the `mern` network.
- `--name frontend` – Gives the container the name `frontend`.
- `-p 5173:5173` – Maps port 5173.
- `myfrontend` – Uses the frontend image.

---

# Part 9: Verify the Manual Deployment

### 24. Check running containers

```bash
docker ps
```

You should see these three containers:

| Container | Port |
|---|---:|
| frontend | 5173 |
| backend | 5050 |
| mongodb | 27017 |

### 25. Check backend logs

```bash
docker logs backend
```

Expected output includes:

```text
Pinged your deployment. You successfully connected to MongoDB!
Server listening on port 5050
```

This confirms that the backend connected to MongoDB successfully.

### 26. Open the application

Open this address in your browser:

```text
http://EC2_PUBLIC_IP:5173
```

Make sure the EC2 security group allows access to the frontend and backend ports.

Test the following operations:

1. Create an employee.
2. View employee records.
3. Edit an employee.
4. Delete an employee.

<text weight="semibold" color="success">Manual Docker deployment completed successfully.</text>

---

# Part 10: Docker Compose Deployment

After successfully completing the manual deployment, I deployed the application using Docker Compose.

### 27. Go back to the project root

If I am inside the backend directory:

```bash
cd ../..
```

I should now be inside:

```text
MERN-docker-compose
```

### 28. Check the project files

```bash
ls -R
```

### 29. Open the Docker Compose file

```bash
vim docker-compose.yaml
```

Use the corrected Compose configuration from the documentation.

Important configurations:

- Backend uses the MongoDB service name `mongodb`.
- Frontend uses the EC2 public IP API address configured in its source code.
- All three services use the Compose network `mern_network`.
- MongoDB uses the named volume `mongo-data`.

### 30. Start the application

```bash
docker compose up -d
```

This command builds the required images if needed and starts all three services in the background.

### 31. Check running containers

```bash
docker ps
```

Check whether the frontend, backend and MongoDB containers are running.

### 32. Check Compose services

```bash
docker compose ps
```

This displays the services managed by Docker Compose.

### 33. Check application logs

```bash
docker compose logs
```

This displays the logs from all the services.

### 34. Open the application

Open the frontend using the EC2 public IP address:

```text
http://EC2_PUBLIC_IP:5173
```

Test the CRUD operations again:

1. Create an employee.
2. View employee records.
3. Edit an employee.
4. Delete an employee.

### 35. Stop the Compose application

```bash
docker compose down
```

This stops and removes the containers created by Docker Compose.

The named database volume remains unless it is separately removed.

---

# Final Deployment Overview

## Manual Docker Deployment

1. Clone the GitHub repository.
2. Build the frontend image.
3. Create the Docker network `mern`.
4. Run the MongoDB container.
5. Build the backend image.
6. Run the backend container.
7. Run the frontend container.
8. Verify and test the application.

## Docker Compose Deployment

1. Configure `docker-compose.yaml`.
2. Run `docker compose up -d`.
3. Check containers and logs.
4. Test the application.
5. Run `docker compose down` when finished.

---

# Important Points to Remember

1. Make sure the EC2 security group allows ports `5173` and `5050`. Do not open MongoDB port `27017` to the public internet.
2. If the EC2 public IP changes, update the frontend API address and rebuild the frontend image.
3. Manual Docker uses the `mern` network. Docker Compose creates its own `mern_network`.
4. The manual MongoDB bind mount and Compose named volume store data separately.
5. Before starting Compose, make sure the manually created containers are no longer occupying ports `5173`, `5050` and `27017`.
6. The backend MongoDB hostname must match the MongoDB container name in manual deployment and the MongoDB service name in Compose deployment.
