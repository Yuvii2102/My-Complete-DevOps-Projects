# Practical: Containerizing a Python Application Using Docker Init

## 1. Objective

To use Docker Init to generate Docker configuration files, build a Docker image and run a Python Flask application inside a Docker container.

## 2. Tools Required

- AWS EC2 – Linux environment
- Git and GitHub – Version control
- Python – Application
- Flask – Python web framework
- Docker Desktop – Container platform
- Docker Init – Docker configuration generation
- Docker Compose – Container management
- Windows PowerShell – Execute Docker commands

## 3. Application Architecture

```text
             GitHub Repository
                    |
                    v
                AWS EC2
                    |
          Create Python Application
                    |
                    v
              Push to GitHub
                    |
                    v
             Windows Machine
                    |
               Git Pull
                    |
                    v
               Docker Init
                    |
                    v
          Generate Docker Files
                    |
                    v
              Dockerfile
              compose.yaml
              .dockerignore
                    |
                    v
             Docker Compose
                    |
                    v
               Docker Image
                    |
                    v
             Docker Container
                    |
                    v
              Port Mapping
                8000:8000
                    |
                    v
               Web Browser
```

# PART 1: CREATE THE APPLICATION ON AWS EC2

## 4. Step 1: Connect to AWS EC2

Connect to your Ubuntu EC2 instance using SSH or MobaXterm.

Move to the home directory:

```bash
cd ~
```

Clone your GitHub repository:

```bash
git clone https://github.com/Yuvii2102/My-Complete-DevOps-Projects.git
```

Enter the repository:

```bash
cd My-Complete-DevOps-Projects
```

Enter the project directory:

```bash
cd "Docker Init Demonstration"
```

Verify the location:

```bash
pwd
```

Check existing files:

```bash
ls -la
```

## 5. Step 2: Create the Python Application

Create app.py:

```bash
vim app.py
```

Add the following code:

```python
from flask import Flask

app = Flask(__name__)

@app.route('/')
def hello():
    return "Hello from my Docker Init project!"

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)
```

Save and exit:

- Press ESC.
- Type `:wq`.
- Press Enter.

## 6. Step 3: Create requirements.txt

Create the dependency file:

```bash
vim requirements.txt
```

Add:

```text
Flask==2.1.0
Werkzeug==2.2.2
```

Save and exit:

- Press ESC.
- Type `:wq`.
- Press Enter.

## 7. Step 4: Verify Application Files

Check the files:

```bash
ls -la
```

Expected structure:

```text
Docker Init Demonstration/
├── README.md
├── app.py
└── requirements.txt
```

Verify application code:

```bash
cat app.py
```

Verify dependencies:

```bash
cat requirements.txt
```

## 8. Step 5: Push Application Files to GitHub

Check Git status:

```bash
git status
```

Add files:

```bash
git add app.py requirements.txt
```

Commit:

```bash
git commit -m "Added Python Flask application"
```

Push:

```bash
git push origin main
```

Verify the files on GitHub.

# PART 2: EXECUTE DOCKER INIT ON WINDOWS

## 9. Step 6: Prepare Windows Environment

Make sure Docker Desktop and Git for Windows are installed.

Open Docker Desktop and wait until the Docker Engine is running.

Open Windows PowerShell.

Check Docker:

```powershell
docker --version
```

Check Git:

```powershell
git --version
```

Check Docker Init:

```powershell
docker init --help
```

## 10. Step 7: Clone the Repository on Windows

Move to the home directory:

```powershell
cd $HOME
```

Clone the repository:

```powershell
git clone https://github.com/Yuvii2102/My-Complete-DevOps-Projects.git
```

Enter the repository:

```powershell
cd My-Complete-DevOps-Projects
```

Enter the project directory:

```powershell
cd "Docker Init Demonstration"
```

Verify the files:

```powershell
ls
```

Expected files:

```text
app.py
README.md
requirements.txt
```

**If the repository is already cloned**, enter the existing directory and execute:

```powershell
git pull origin main
```

## 11. Step 8: Execute Docker Init

Make sure you are inside:

```text
Docker Init Demonstration
```

Execute:

```powershell
docker init
```

Docker Init starts an interactive setup process.

### Answer the following questions:

**Question 1: What application platform does your project use?**

Select:

```text
Python
```

**Question 2: What version of Python do you want to use?**

Enter:

```text
3.10.4
```

**Question 3: What port do you want your app to listen on?**

Enter:

```text
8000
```

**Question 4: What is the command you use to run your app?**

Enter:

```text
python app.py
```

After answering all questions, Docker Init generates the following files:

```text
.dockerignore
Dockerfile
compose.yaml
README.Docker.md
```

## 12. Step 9: Verify Generated Files

Execute:

```powershell
ls -Force
```

Expected project structure:

```text
Docker Init Demonstration/
│
├── .dockerignore
├── Dockerfile
├── compose.yaml
├── README.Docker.md
├── README.md
├── app.py
└── requirements.txt
```

View the Dockerfile:

```powershell
Get-Content Dockerfile
```

View the Compose file:

```powershell
Get-Content compose.yaml
```

View the Docker ignore file:

```powershell
Get-Content .dockerignore
```

View the generated README:

```powershell
Get-Content README.Docker.md
```

## 13. Step 10: Verify Dockerfile Configuration

Check that the generated Dockerfile contains the following important configurations:

**Base image:**

```dockerfile
FROM python:3.10.4-slim
```

**Working directory:**

```dockerfile
WORKDIR /app
```

**Install dependencies:**

```dockerfile
RUN --mount=type=cache,target=/root/.cache/pip \
    --mount=type=bind,source=requirements.txt,target=requirements.txt \
    python -m pip install -r requirements.txt
```

**Non-root user:**

```dockerfile
USER appuser
```

**Copy application files:**

```dockerfile
COPY . .
```

**Application port:**

```dockerfile
EXPOSE 8000
```

**Start application:**

```dockerfile
CMD python app.py
```

Keep the complete Docker Init-generated Dockerfile. These are important configurations to verify, not instructions to replace the generated file with a shortened version.

## 14. Step 11: Verify Docker Compose Configuration

Execute:

```powershell
Get-Content compose.yaml
```

The main configuration should look like:

```yaml
services:
  server:
    build:
      context: .
    ports:
      - 8000:8000
```

This configuration:

- Defines a service named server.
- Builds the image using the Dockerfile.
- Uses the current directory as the build context.
- Maps host port 8000 to container port 8000.

## 15. Step 12: Build the Docker Image and Run the Container

Execute:

```powershell
docker compose up --build -d
```

Docker Compose will:

1. Read compose.yaml.
2. Read the Dockerfile.
3. Download the Python base image if required.
4. Create the non-root user.
5. Install Python dependencies.
6. Copy application files.
7. Build the Docker image.
8. Create the container.
9. Start the Flask application.

Wait until the build and container startup are complete.

# PART 3: VERIFY THE APPLICATION

## 16. Step 13: Verify Docker Image

Execute:

```powershell
docker images
```

Find the generated image:

```text
dockerinitdemonstration-server
```

## 17. Step 14: Verify Running Containers

Execute:

```powershell
docker ps
```

To display all containers:

```powershell
docker ps -a
```

## 18. Step 15: Check Application Logs

Execute:

```powershell
docker compose logs
```

To continuously monitor logs:

```powershell
docker compose logs -f
```

Press CTRL + C to stop following the logs.

## 19. Step 16: Verify Port Mapping

Execute:

```powershell
docker compose ps
```

Check the port mapping:

```powershell
docker port dockerinitdemonstration-server-1
```

Expected output:

```text
8000/tcp -> 0.0.0.0:8000
```

The host port 8000 is mapped to container port 8000.

## 20. Step 17: Access Application Through Browser

Open your web browser.

Enter:

```text
http://localhost:8000
```

Expected output:

```text
Hello from my Docker Init project!
```

The application is now successfully running inside a Docker container.

# PART 4: CLEANUP AND GITHUB

## 21. Step 18: Stop and Remove Containers

Execute:

```powershell
docker compose down
```

Verify:

```powershell
docker ps -a
```

The container should no longer be running or listed as an existing Compose container.

The Docker image remains available.

## 22. Step 19: Push Docker Configuration Files to GitHub

Check Git status:

```powershell
git status
```

Add all project files:

```powershell
git add .
```

Commit:

```powershell
git commit -m "Added Docker Init configuration for Python application"
```

Push:

```powershell
git push origin main
```

Refresh your GitHub repository and verify all files.

## 23. Final Project Structure

```text
Docker Init Demonstration/
│
├── .dockerignore
├── Dockerfile
├── compose.yaml
├── README.Docker.md
├── README.md
├── app.py
└── requirements.txt
```

## 24. Important Commands Reference

| Task | Command |
|---|---|
| Clone repository | `git clone <repository-url>` |
| Pull latest changes | `git pull origin main` |
| Enter project folder | `cd "Docker Init Demonstration"` |
| Check files | `ls -Force` |
| Check Docker version | `docker --version` |
| Execute Docker Init | `docker init` |
| View Dockerfile | `Get-Content Dockerfile` |
| View Compose file | `Get-Content compose.yaml` |
| Build and run | `docker compose up --build -d` |
| List images | `docker images` |
| List containers | `docker ps` |
| View logs | `docker compose logs` |
| Follow logs | `docker compose logs -f` |
| Check port mapping | `docker compose ps` |
| Stop containers | `docker compose down` |
| Check Git status | `git status` |
| Add files | `git add .` |
| Commit | `git commit -m "message"` |
| Push to GitHub | `git push origin main` |

## 25. Final Outcome

Successfully used Docker Init to generate Docker configuration files for a Python Flask application.

Built a Docker image, created and ran a container using Docker Compose, configured port mapping and verified the application through a web browser.

Finally, pushed the application and generated Docker configuration files to GitHub.

**GitHub Repository:** [My-Complete-DevOps-Projects](https://github.com/Yuvii2102/My-Complete-DevOps-Projects)

**Project Folder:** [Docker Init Demonstration](https://github.com/Yuvii2102/My-Complete-DevOps-Projects/tree/main/Docker%20Init%20Demonstration)
