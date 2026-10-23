# Project 2: Deploying a Static Website on AWS EC2 Using Apache

## 📌 1. Project Overview

In this project, I deployed a static website called **Dimension** on an AWS EC2 instance using Ubuntu Linux and Apache2.

I installed Apache, downloaded the website files from GitHub, copied them into Apache's web directory, and configured the AWS Security Group. Finally, I opened the EC2 public IP address in my browser to access the website.

## 🛠️ 2. Technologies Used

| Technology | Purpose |
|---|---|
| AWS EC2 | To create a cloud server |
| Ubuntu Linux | Operating system |
| Apache2 | To host the website |
| Git | To download website files |
| GitHub | To get the website source code |
| HTML | To structure the webpage |
| CSS | To style the website |
| JavaScript | To add interactive features |
| HTTP | To access the website |

## 🏗️ 3. Project Architecture

```text
GitHub Repository
       |
       | git clone
       v
AWS EC2 Instance
(Ubuntu Linux)
       |
       v
Apache2 Web Server
       |
       v
/var/www/html/
       |
       v
Dimension Website
       |
       v
Web Browser
Public IP Address
HTTP - Port 80
```

## ⚙️ 4. Step-by-Step Implementation

### Step 1: Launch an EC2 Instance

I created an EC2 instance with Ubuntu Linux from the AWS Management Console and connected to it using EC2 Instance Connect.

### Step 2: Update Ubuntu Packages

```bash
sudo apt update
```

This updates the information about available software packages.

### Step 3: Install Apache2

```bash
sudo apt install apache2 -y
```

I installed Apache2 to serve my website to visitors.

### Step 4: Start Apache2

```bash
sudo systemctl start apache2
```

This starts the Apache web server.

### Step 5: Enable Apache at Startup

```bash
sudo systemctl enable apache2
```

This makes Apache start automatically when Ubuntu boots.

### Step 6: Check Apache Status

```bash
sudo systemctl is-active apache2
sudo systemctl status apache2
```

Expected output:

```text
active
```

### Step 7: Test Apache Locally

```bash
curl -I http://localhost
```

This checks whether Apache is responding to HTTP requests from the EC2 instance.

### Step 8: Open the Apache Default Page

I opened the EC2 public IPv4 address in my browser:

```text
http://MY_EC2_PUBLIC_IP
```

Initially, I saw the default Apache welcome page. This confirmed that Apache was working.

### Step 9: Configure the AWS Security Group

I checked the inbound rules of my EC2 Security Group.

| Type | Port | Source |
|---|---:|---|
| HTTP | 80 | `0.0.0.0/0` |
| SSH | 22 | Appropriate trusted source |

Port 80 allows visitors to access the website using HTTP. SSH access should be restricted to trusted sources and configured according to the connection method.

### Step 10: Check Git Installation

```bash
git --version
```

If Git was not installed, I could install it using:

```bash
sudo apt install git -y
```

### Step 11: Go to the Home Directory

```bash
cd ~
```

This takes me to my Ubuntu user's home directory.

### Step 12: Clone the GitHub Repository

I used the following repository to get the Dimension website files:

**Source:** [varunbgit/AWSDemo](https://github.com/varunbgit/AWSDemo)

```bash
git clone https://github.com/varunbgit/AWSDemo.git
```

This downloads the repository onto the EC2 instance.

### Step 13: Enter the Project Directory

```bash
cd AWSDemo
ls -la
```

I checked the files and folders to find the correct website files.

### Step 14: Check Apache's Web Directory

```bash
ls -la /var/www/html/
```

Apache normally serves website files from:

```text
/var/www/html/
```

The main webpage is usually named `index.html`.

### Step 15: Copy the Website Files

From the directory containing the correct Dimension website files, I ran:

```bash
sudo cp -r . /var/www/html/
```

This copies the website files and folders into Apache's web directory.

**Note:** I must run this command from the correct website directory to avoid copying unrelated files or overwriting files unintentionally.

### Step 16: Restart Apache

```bash
sudo systemctl restart apache2
```

I checked the service again:

```bash
sudo systemctl is-active apache2
```

Expected output:

```text
active
```

### Step 17: Access the Website

I opened the EC2 public IPv4 address in my browser:

```text
http://MY_EC2_PUBLIC_IP
```

The Dimension website appeared after the correct website files were placed in Apache's web directory and HTTP access was configured.

## 🧰 5. Important Commands

| Command | Purpose |
|---|---|
| `sudo apt update` | Update package information |
| `sudo apt install apache2 -y` | Install Apache2 |
| `sudo systemctl start apache2` | Start Apache |
| `sudo systemctl enable apache2` | Enable startup at boot |
| `sudo systemctl is-active apache2` | Check Apache status |
| `curl -I http://localhost` | Test Apache locally |
| `git --version` | Check Git version |
| `sudo apt install git -y` | Install Git |
| `cd ~` | Go to the home directory |
| `git clone https://github.com/varunbgit/AWSDemo.git` | Download the repository |
| `cd AWSDemo` | Enter the project directory |
| `ls -la` | List files and folders |
| `sudo cp -r . /var/www/html/` | Copy website files |
| `sudo systemctl restart apache2` | Restart Apache |

## 🔍 6. Troubleshooting

### Problem 1: Apache Default Page Appeared

**Reason:** The Dimension website files might not have been copied to the correct directory.

**Solution:**

```bash
ls -la /var/www/html/
```

I checked whether the correct `index.html` and supporting files were present.

### Problem 2: Website Was Not Opening

**Possible reasons:**
- Apache was not running.
- The public IP address was incorrect.
- Port 80 was not allowed in the Security Group.
- There was a network connectivity issue.

**Commands to check:**

```bash
sudo systemctl is-active apache2
curl -I http://localhost
```

I also checked the EC2 public IP address and Security Group rules.

### Problem 3: Website Stopped Loading

If Apache was stopped, I started it again using:

```bash
sudo systemctl start apache2
```

This allows Apache to serve the files currently present in its web directory.

## 📚 9. Source Code Credit

The Dimension website source code was obtained from:

**[varunbgit/AWSDemo](https://github.com/varunbgit/AWSDemo)**

The repository provided the website files. My work focused on setting up EC2, installing Apache, deploying the website, configuring network access, and troubleshooting.

## 👨‍💻 About Me

**Yuvraj**  
Cloud & DevOps Learner

- GitHub: [Yuvii2102](https://github.com/Yuvii2102)
- DevOps Learning Journey: [My-Devops-Cloud-Journey](https://github.com/Yuvii2102/My-Devops-Cloud-Journey)
- Hashnode Blog: [Build, Break & Fix Diaries](https://build-break-fix-diaries-with-yuvrajj.hashnode.dev/)

---

*Learning by doing, growing with every challenge.*
