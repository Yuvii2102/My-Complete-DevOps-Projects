# Installing Jenkins on AWS EC2 and Running My First Job

## 📌 1. Introduction

In this project, I installed Jenkins on an AWS EC2 Ubuntu instance and opened it in my web browser.

Jenkins is a tool that helps us automate tasks like building, testing, and deploying applications.

I also created my first Jenkins job and ran a few simple commands to check whether everything was working correctly.

## 🎯 2. What I Wanted to Learn

- How to install Jenkins on an AWS EC2 instance.
- How to install Java 21.
- How to open Jenkins in a web browser.
- How to create my first Jenkins job.
- How to run shell commands using Jenkins.
- How to check the build results.

## 🛠️ 3. Tools Used

- **AWS EC2** – To run my Ubuntu server.
- **Ubuntu** – The operating system on my EC2 instance.
- **Java 21** – Required to run Jenkins.
- **Jenkins** – To automate tasks.
- **Web Browser** – To access the Jenkins dashboard.
- **Shell Scripting** – To run commands in my Jenkins job.

## ☁️ 4. Connect to the EC2 Instance

First, I connected to my Ubuntu EC2 instance using SSH.

Then, I updated the package list:

```bash
sudo apt update
```

## ☕ 5. Install Java 21

Jenkins needs Java to run. So, I installed Java 21.

```bash
sudo apt install -y fontconfig openjdk-21-jre
```

I checked whether Java was installed correctly:

```bash
java -version
```

## 📦 6. Install Jenkins

First, I added the official Jenkins repository key:

```bash
sudo mkdir -p /etc/apt/keyrings

sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key
```

Then, I added the Jenkins repository:

```bash
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | \
sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
```

I updated the package list and installed Jenkins:

```bash
sudo apt update
sudo apt install -y jenkins
```

## ▶️ 7. Start Jenkins

After installation, I started the Jenkins service and enabled it to start automatically when the server boots.

```bash
sudo systemctl enable jenkins
sudo systemctl start jenkins
```

I checked its status:

```bash
sudo systemctl status jenkins
```

The service should show `active (running)` when Jenkins is running correctly.

## 🔐 8. Configure the EC2 Security Group

Jenkins uses port **8080** by default.

I opened my EC2 instance's security group and added an inbound rule:

| Setting | Value |
|---|---|
| Type | Custom TCP |
| Port | 8080 |
| Source | My IP |

This allowed me to access Jenkins from my computer.

**Security note:** I restricted access to my own IP instead of opening Jenkins to everyone.

## 🌐 9. Open Jenkins in the Browser

I copied the public IPv4 address of my EC2 instance and opened this URL:

```text
http://YOUR-EC2-PUBLIC-IP:8080
```

I replaced `YOUR-EC2-PUBLIC-IP` with my actual EC2 public IP address.

The Jenkins setup page appeared in my browser.

## 🔑 10. Unlock Jenkins

To get the initial administrator password, I ran:

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

I copied the password and pasted it into the Jenkins setup page.

Then, I installed the suggested plugins, created my administrator account, and completed the setup.

Finally, I reached the Jenkins dashboard!

## 🧰 11. Create My First Jenkins Job

I wanted to test whether Jenkins could run commands for me.

I followed these steps:

1. Clicked **New Item**.
2. Entered the job name `First-Jenkins-Job`.
3. Selected **Freestyle project**.
4. Clicked **OK**.
5. Scrolled down to **Build Steps**.
6. Selected **Add build step → Execute shell**.

I entered these commands:

```bash
echo "Hello from Jenkins!"
echo "My first CI/CD job on AWS EC2"
```

Then, I clicked **Save**.

## 🚀 12. Run the Jenkins Job

I clicked **Build Now** to run my job.

After the build finished, I opened **Build History → Console Output** to see what happened.

The output showed:

```text
Hello from Jenkins!
My first CI/CD job on AWS EC2

Finished: SUCCESS
```

## 📚 14. What I Learned

Through this project, I learned:

- How to install Java and Jenkins on Ubuntu.
- How to start and check a Linux service.
- How to configure an EC2 security group.
- How to access Jenkins through a web browser.
- How to create a Freestyle job.
- How to execute shell commands in Jenkins.
- How to check whether a build succeeded or failed.

## 🎯 15. What's Next?

Now that my first Jenkins job is working, I want to learn how to connect Jenkins with GitHub and automatically run jobs whenever I push new code.

This is my first step toward learning Jenkins pipelines and CI/CD automation.

Every project helps me learn something new. I will keep practising, building and improving my Cloud & DevOps skills! 
