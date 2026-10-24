# ☁️ AWS Resource Tracker Using Shell Scripting and Cron

## 1. About This Project

In this project, I created a shell script named `rt.sh` on an Ubuntu EC2 instance to get information about AWS resources.

I used AWS CLI commands to list S3 buckets, EC2 instances, and IAM users. Instead of running each command separately, I put them into one shell script.

I also learned how to use cron to explore task scheduling and how to save script output in a log file.

**With cron, the script can run on a schedule automatically. This is an example of task scheduling and infrastructure automation.**

## 2. Tools Used

| Tool | Purpose |
|---|---|
| AWS EC2 | Provides the virtual server |
| Ubuntu Linux | Operating system |
| AWS CLI | Gets information about AWS resources |
| Bash | Runs commands through a shell script |
| Vim | Creates and edits the script |
| Cron | Schedules commands |
| Log file | Stores script output and errors |

## 3. Step 1 — Update Ubuntu

```bash
sudo apt update
```

This updates Ubuntu's package information.

## 4. Step 2 — Install AWS CLI

```bash
sudo apt install awscli -y
```

I checked whether AWS CLI was installed:

```bash
aws --version
```

This displays the installed AWS CLI version.

## 5. Step 3 — Configure AWS CLI

```bash
aws configure
```

I entered the requested AWS CLI configuration details:

- AWS Access Key ID
- AWS Secret Access Key
- Default region: `us-east-1`
- Default output format

AWS CLI needs valid credentials and the correct permissions to access AWS resources.

**Security note:** Never upload AWS credentials to GitHub. An IAM role attached to EC2 is generally safer than storing long-term access keys on the server.

## 6. Step 4 — Practise AWS CLI Commands

### List S3 buckets

```bash
aws s3 ls
```

This lists the S3 buckets I can access.

### Get EC2 instance information

```bash
aws ec2 describe-instances
```

To display the information in table format:

```bash
aws ec2 describe-instances --output table
```

To display only EC2 instance IDs:

```bash
aws ec2 describe-instances --query 'Reservations[].Instances[].InstanceId' --output table
```

### List IAM users

```bash
aws iam list-users --output table
```

These commands helped me practise retrieving AWS resource information from the terminal.

## 7. Step 5 — Create the Shell Script

I created a file named `rt.sh` using Vim:

```bash
vim rt.sh
```

I added the following commands to the script:

```bash
#!/bin/bash

###########
# Author: Yuvraj
# Date: Oct-24
# This script will report AWS resource usage

set -x

# AWS S3
# AWS EC2
# AWS Lambda
# AWS IAM

# If you don't know the AWS CLI commands, you can use the
# AWS CLI Command Reference to find the required command.

# List S3 buckets
echo "Print list of s3 buckets"
aws s3 ls

# List EC2 Instances
echo "Print list of EC2 Instances"
aws ec2 describe-instances

# List IAM users
echo "Print list of IAM users"
aws iam list-users

# With cron, the script runs on a schedule automatically.
# This is an example of task scheduling and infrastructure automation.
```

### Understanding the script

- `#!/bin/bash` tells Linux to run the script using Bash.
- `echo` prints a message.
- `aws s3 ls` lists S3 buckets.
- `aws ec2 describe-instances` retrieves EC2 instance information.
- `aws iam list-users` lists IAM users.
- `set -x` displays commands as Bash executes them.
- Lines beginning with `#` are comments.

The cron-related comments explain the purpose of automation. They do not schedule the script by themselves.

## 8. Step 6 — Give Execute Permission

```bash
chmod +x rt.sh
```

This gives the script permission to execute.

## 9. Step 7 — Run the Script

I ran the script using:

```bash
./rt.sh
```

This executes the AWS CLI commands one after another.

I also used:

```bash
./rt.sh | more
```

This lets me read the output page by page.

## 10. Step 8 — Check EC2 Instance IDs

```bash
aws ec2 describe-instances --query 'Reservations[].Instances[].InstanceId' --output table
```

This displays EC2 instance IDs in table format.

## 11. Step 9 — Display IAM Users

```bash
aws iam list-users --output table
```

This displays IAM users in a readable table, provided I have the required permissions.

## 12. Step 10 — Explore Cron Scheduling

I opened the crontab editor using:

```bash
crontab -e
```

Cron allows Linux commands to run automatically at scheduled times.

To check the configured cron jobs, I used:

```bash
crontab -l
```

This displays the cron jobs configured for my user.

## 13. Step 11 — Understand the Cron Schedule

A cron entry has five time fields followed by the command:

```text
minute hour day-of-month month day-of-week command
```

For example, the following entry runs a script every day at 9 AM:

```bash
0 9 * * * /home/ubuntu/rt.sh
```

This is an example of cron syntax. The actual schedule depends on the entry saved in the crontab.

## 14. Step 12 — Save Output in a Log File

I used the following command to append the script output and errors to a log file:

```bash
/home/ubuntu/rt.sh >> /home/ubuntu/rt.log 2>&1
```

- `>>` appends normal output to the log file.
- `2>&1` sends error messages to the same destination.
- `/home/ubuntu/rt.log` is the log file.

## 15. Step 13 — View the Log File

To view the saved output:

```bash
cat /home/ubuntu/rt.log
```

To watch new output as it appears:

```bash
tail -f /home/ubuntu/rt.log
```

Press `Ctrl + C` to stop watching the log.

## 16. Step 14 — Check Cron Activity

To check whether the cron service is running:

```bash
sudo systemctl status cron
```

To look for cron activity in the system log:

```bash
grep CRON /var/log/syslog
```

The system log command works when cron activity is recorded in that file.

## 17. How the Project Works

```text
Ubuntu EC2 Instance
        |
        v
Install and Configure AWS CLI
        |
        v
Practise AWS CLI Commands
        |
        v
Create rt.sh Using Vim
        |
        v
Give Execute Permission
        |
        v
Run the Shell Script
        |
        v
Explore Cron Scheduling
        |
        v
Save Output and Errors
        |
        v
      rt.log

```

## 🏢 21. Real-World Use in Organizations

In companies, Cloud and DevOps engineers need to check their AWS resources regularly. Checking everything manually takes time, especially when there are many resources.

In this project, I learned how to use a shell script to make this work easier.

- **Less manual work:** Instead of running each AWS command separately, we can run one script.
- **Saves time:** The script helps us get information about S3 buckets, EC2 instances, and IAM users.
- **Runs automatically:** Using cron, we can schedule the script to run at a fixed time without running it manually every time.
- **Saves information:** We can save the output in a log file and check it later.
- **Makes work easier:** Engineers can spend less time doing the same tasks again and again.

### 🌍 Real-World Example

Imagine a company has many AWS resources. Every morning, an engineer needs to check them.

Instead of running the commands manually every day, the engineer can use a shell script and schedule it with cron. The script runs at the scheduled time and saves the output in a log file.

This makes the work easier and saves time.

**The main thing I learned:** Automation helps reduce manual work, save time, and make daily tasks easier for Cloud and DevOps engineers.

```

## 20. Final Summary

This project helped me understand how Linux, Bash scripting, AWS CLI, and cron work together.

I installed and configured AWS CLI, practised AWS commands, created a shell script, and explored how to schedule commands and save output in a log file.

The main thing I learned is that simple scripts can help automate repetitive tasks and make cloud operations easier.

Project Name: AWS Resource Tracker Using Shell Scripting and Cron

Environment: Ubuntu on Amazon EC2

Technologies: AWS CLI, Bash, Linux, Cron, Amazon EC2, Amazon S3, AWS IAM

Part of: My Cloud & DevOps Learning Journey
