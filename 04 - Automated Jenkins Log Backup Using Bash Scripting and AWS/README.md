<h1 align="center"> Jenkins Log Backup to Amazon S3 </h1>

<p align="center">
  <strong>Automating Jenkins Build Log Backups Using Bash Scripting and AWS S3</strong>
</p>

<p align="center">
  <code>Jenkins</code> • <code>Bash</code> • <code>AWS CLI</code> • <code>Amazon S3</code> • <code>Linux</code>
</p>

## 📌 Project Overview

In this project, I created a Bash script to back up Jenkins build log files to an Amazon S3 bucket.

Jenkins creates log files whenever a build runs. Instead of manually copying these logs to another location, I used a shell script to find the logs created on the current day and upload them to S3.

I also installed Jenkins on an Ubuntu EC2 instance and created a Jenkins job named `Jenkins-Log-Backup-to-S3`.

## 🎯 Project Objectives

- Install Jenkins on an Ubuntu EC2 instance.
- Install and configure the AWS CLI.
- Create a Bash script to find Jenkins build logs.
- Upload today's build logs to an Amazon S3 bucket.
- Check whether each upload succeeds or fails.
- Create and test a Jenkins job.

## 🛠️ Tools and Technologies

| Tool | Purpose |
|---|---|
| AWS EC2 | Runs the Ubuntu server and Jenkins |
| Ubuntu Linux | Operating system |
| Jenkins | Creates and manages build jobs |
| Bash scripting | Automates log file processing |
| AWS CLI | Communicates with AWS services |
| Amazon S3 | Stores the uploaded log files |
| Vim | Creates and edits the shell script |

## 🏗️ Project Architecture

```text
Jenkins on AWS EC2
        |
        v
Jenkins Job Build Logs
        |
        v
Bash Script (s3upload.sh)
        |
        v
Check AWS CLI Installation
        |
        v
Find Today's Build Logs
        |
        v
AWS CLI: aws s3 cp
        |
        v
Amazon S3 Bucket
(jenkins-cost-optimization21)
```

## 🚀 Step 1: Install Java and Jenkins

First, I updated the Ubuntu package list and installed Java 21 and Fontconfig.

```bash
sudo apt update

sudo apt install fontconfig openjdk-21-jre -y

java -version
```

Next, I added the Jenkins repository and installed Jenkins.

```bash
sudo mkdir -p /etc/apt/keyrings

sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key

echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

sudo apt update

sudo apt install jenkins -y
```

I enabled and started the Jenkins service.

```bash
sudo systemctl enable jenkins

sudo systemctl start jenkins

sudo systemctl status jenkins
```

To retrieve the initial Jenkins administrator password, I used:

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

I then accessed Jenkins through its web interface and completed the initial setup.

## ☁️ Step 2: Install and Configure AWS CLI

I installed AWS CLI on the EC2 instance.

```bash
sudo apt update

sudo apt install awscli -y

aws --version
```

Then I configured the AWS CLI:

```bash
aws configure
```

I tested whether the CLI could access S3:

```bash
aws s3 ls
```

This command lists the S3 buckets accessible to the configured AWS identity.

**Security note:** For an EC2-based project, an IAM role attached to the instance with appropriate S3 permissions is recommended instead of storing long-term AWS access keys on the server.

## 🪣 Step 3: Create the S3 Bucket

I used the S3 bucket named:

`jenkins-cost-optimization21`

The bucket stores the Jenkins build log files uploaded by the script.

The script uses this destination:

```bash
S3_BUCKET="s3://jenkins-cost-optimization21"
```

## 📝 Step 4: Create the Bash Script

I created a shell script using Vim.

```bash
vim s3upload.sh
```

The script uses the Jenkins home directory, S3 bucket, and current date to find and upload log files.

### Script: `s3upload.sh`

```bash
#!/bin/bash

#
# Author: Yuvraj.R
# Description: Shell script to upload the Jenkins log files to S3 Bucket
#

JENKINS_HOME="/var/lib/jenkins"
S3_BUCKET="s3://jenkins-cost-optimization21"
DATE=$(date +%Y-%m-%d)

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "AWS CLI is not installed. Please install it to proceed."
    exit 1
fi

# Iterate through all job directories
for job_dir in "$JENKINS_HOME/jobs/"*/; do
    job_name=$(basename "$job_dir")

    # Iterate through build directories for the job
    for build_dir in "$job_dir/builds/"*/; do
        # Get build number and log file path
        build_number=$(basename "$build_dir")
        log_file="$build_dir/log"

        # Check if log file exists and was created today
        if [ -f "$log_file" ] && [ "$(date -r "$log_file" +%Y-%m-%d)" == "$DATE" ]; then

            # Upload log file to S3
            aws s3 cp "$log_file" "$S3_BUCKET/$job_name-$build_number.log" --only-show-errors

            if [ $? -eq 0 ]; then
                echo "Uploaded: $job_name/$build_number to $S3_BUCKET/$job_name-$build_number.log"
            else
                echo "Failed to upload: $job_name/$build_number"
            fi
        fi
    done
done
```

## 🔍 Step 5: Understand the Script

### 1. Define the variables

```bash
JENKINS_HOME="/var/lib/jenkins"
S3_BUCKET="s3://jenkins-cost-optimization21"
DATE=$(date +%Y-%m-%d)
```

- `JENKINS_HOME` points to the Jenkins home directory.
- `S3_BUCKET` specifies where the logs will be uploaded.
- `DATE` stores today's date in `YYYY-MM-DD` format.

### 2. Check AWS CLI

```bash
if ! command -v aws &> /dev/null; then
    echo "AWS CLI is not installed. Please install it to proceed."
    exit 1
fi
```

This checks whether AWS CLI is installed. If it is missing, the script displays a message and exits.

### 3. Find Jenkins jobs

```bash
for job_dir in "$JENKINS_HOME/jobs/"*/; do
    job_name=$(basename "$job_dir")
```

The first `for` loop goes through the Jenkins job directories and gets each job's name.

### 4. Find build directories

```bash
for build_dir in "$job_dir/builds/"*/; do
    build_number=$(basename "$build_dir")
    log_file="$build_dir/log"
```

The second `for` loop goes through each job's build directories and identifies the build number and its log file.

### 5. Check the date

```bash
if [ -f "$log_file" ] && [ "$(date -r "$log_file" +%Y-%m-%d)" == "$DATE" ]; then
```

This checks whether the log file exists and whether its last modification date matches today's date.

### 6. Upload the log to S3

```bash
aws s3 cp "$log_file" "$S3_BUCKET/$job_name-$build_number.log" --only-show-errors
```

The AWS CLI copies the log file to S3. The destination filename includes the Jenkins job name and build number.

For example:

`Jenkins-Log-Backup-to-S3-1.log`

### 7. Check the upload result

```bash
if [ $? -eq 0 ]; then
    echo "Uploaded: $job_name/$build_number to $S3_BUCKET/$job_name-$build_number.log"
else
    echo "Failed to upload: $job_name/$build_number"
fi
```

`$?` contains the exit status of the previous command. A value of `0` means the AWS CLI command succeeded; a nonzero value indicates failure.

## ▶️ Step 6: Run the Script

I ran the following commands during the project:

```bash
chmod 777 s3upload.sh

./s3upload.sh
```

I edited the script with Vim and ran it again:

```bash
vim s3upload.sh

./s3upload.sh
```

The script checks Jenkins build logs and uploads matching logs to the S3 bucket.

**Note:** `chmod 777` grants everyone read, write, and execute permissions. For a production setup, use more restrictive permissions, such as `chmod 750 s3upload.sh`, and ensure the intended user can access the required files.

## 🖥️ Step 7: Create the Jenkins Job

I created a Jenkins job named:

`Jenkins-Log-Backup-to-S3`

I used the Jenkins dashboard to create a Freestyle project and ran builds using **Build Now**.

I checked the build history and console output to verify the job's result. Builds #1 and #2 completed successfully.

The script was also run manually from the EC2 terminal. To make the Jenkins job perform the S3 backup itself, its build step must execute the script, for example:

```bash
/home/ubuntu/s3upload.sh
```

The script must be readable and executable by the Jenkins service user, and the required AWS permissions must be available to that user. A successful Jenkins build alone does not prove that the upload happened.

## 📂 Step 8: Explore the Jenkins Home Directory

The Jenkins home directory is:

```text
/var/lib/jenkins
```

I used these commands to explore it:

```bash
sudo ls -la /var/lib/jenkins
```

To list Jenkins jobs:

```bash
sudo ls -la /var/lib/jenkins/jobs
```

To inspect my project job:

```bash
sudo ls -la /var/lib/jenkins/jobs/Jenkins-Log-Backup-to-S3
```

To inspect Build #1:

```bash
sudo ls -la /var/lib/jenkins/jobs/Jenkins-Log-Backup-to-S3/builds/1
```

To view its console log:

```bash
sudo cat /var/lib/jenkins/jobs/Jenkins-Log-Backup-to-S3/builds/1/log
```

## ✅ Step 9: Verify the S3 Upload

I opened the S3 bucket in the AWS Console and confirmed that it contained these objects:

- `Jenkins-Log-Backup-to-S3-1.log`
- `Jenkins-Log-Backup-to-S3-2.log`

Both log objects were visible in the bucket.

This confirmed that the log files had been uploaded to Amazon S3.

## 🌍 Real-World Use in an Organisation

### The Problem

In real-world organisations, developers use Jenkins to build and deploy applications. Every build generates logs that help teams understand what happened during the build.

Organisations may collect logs from different sources, such as:
- Jenkins build logs
- Application logs
- Kubernetes logs
- Infrastructure logs

Tools like the ELK Stack (Elasticsearch, Logstash, and Kibana) can help teams search, analyse, and troubleshoot these logs. However, storing large amounts of logs in ELK can increase computing and storage costs, especially when older logs are rarely accessed.

### 💡 The Solution

This project demonstrates a simple way to reduce the cost of storing Jenkins build logs.

1. Jenkins generates logs whenever a build runs.
2. A Bash script checks the Jenkins job directories and finds build logs created on the current day.
3. The script uploads those logs to an Amazon S3 bucket using AWS CLI.
4. The organisation can configure S3 Lifecycle Management to move older logs to lower-cost storage classes, such as S3 Glacier Flexible Retrieval or S3 Glacier Deep Archive, when appropriate.

### 💰 How This Helps an Organisation

- **Cost optimisation:** Organisations can reduce their dependence on expensive log storage for logs that only need to be retained for future reference.
- **Reliable backups:** Build logs are stored separately in S3, helping teams retain a copy if the original Jenkins environment becomes unavailable.
- **Easy investigation:** Teams can retrieve historical build logs when investigating failed builds or troubleshooting incidents.
- **Less manual work:** A script can automate log collection and uploads instead of requiring developers to copy files manually.
- **Storage lifecycle management:** Older logs can be transitioned to lower-cost storage classes based on the organisation's retention and access requirements.

### 🏢 Example Scenario

Imagine an organisation runs hundreds of Jenkins builds every day. Over time, the build logs consume more storage, increasing the cost of retaining them in its central logging system.

The organisation can use a script like this project to back up Jenkins build logs to Amazon S3. It can then apply lifecycle rules to transition older logs to lower-cost storage classes.

The team can keep frequently accessed logs in its main logging system for troubleshooting while retaining older logs in S3 for backup and future reference.

**The main idea is simple: keep logs available for investigation while managing long-term storage costs.**

This project demonstrates the log-backup process using Bash scripting, Jenkins, AWS CLI, and Amazon S3. S3 Lifecycle Management, automated scheduling, and integration with a central logging platform are additional configurations that can be implemented in a production environment.


## 👨‍💻 Author

**Yuvraj R.**

Cloud & DevOps Learning Journey

