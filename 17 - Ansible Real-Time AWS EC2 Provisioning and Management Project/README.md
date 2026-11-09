# Project 17: Ansible Real-Time AWS EC2 Provisioning and Management

## 1. Project Overview

In this project, I used Ansible to automate the provisioning and management of EC2 instances on AWS.

I created three EC2 instances using an Ansible playbook. After provisioning, I configured SSH authentication between the Ansible Control Node and the Managed Nodes.

I then used Ansible to check connectivity and gather system information from all three instances. Finally, I created a playbook that shuts down only Ubuntu instances while keeping the Amazon Linux instance running.

### Objectives

- Create three EC2 instances using Ansible.
- Configure SSH key-based authentication.
- Connect the Control Node to all Managed Nodes.
- Test connectivity using the Ansible ping module.
- Gather operating system information using Ansible Facts.
- Shut down only Ubuntu instances using conditional execution.

## 2. Project Architecture

The project uses one Ansible Control Node to manage three EC2 instances.

| Machine | Operating System | Role |
|---|---|---|
| Ansible-Control-Node | Ubuntu | Control Node |
| Manage-Node-1 | Ubuntu | Managed Node |
| Manage-Node-2 | Ubuntu | Managed Node |
| Manage-Node-3 | Amazon Linux 2023 | Managed Node |

### Architecture Flow

```text
                  AWS Cloud
                      |
             Ansible Control Node
                Ubuntu EC2
                      |
                 Ansible
                      |
          SSH Key-Based Authentication
                      |
        +-------------+-------------+
        |             |             |
        v             v             v
   Manage-Node-1  Manage-Node-2  Manage-Node-3
      Ubuntu         Ubuntu      Amazon Linux
```

## 3. AWS Environment Details

The following configuration was used during this practical.

| Configuration | Value |
|---|---|
| AWS Region | us-east-1 (N. Virginia) |
| Control Node | 172.31.17.219 |
| VPC ID | vpc-0c6cd2d587a5406dd |
| Subnet ID | subnet-08c7deca2bf1fc730 |
| Security Group ID | sg-0f0b23b1d02bdccd9 |
| Key Pair Name | ansible-key |
| Instance Type | t2.micro |

### Managed Node Details

| Instance | Private IP | Public IP | SSH User |
|---|---|---|---|
| Manage-Node-1 | 172.31.10.169 | 100.52.184.204 | ubuntu |
| Manage-Node-2 | 172.31.7.49 | 44.192.106.135 | ubuntu |
| Manage-Node-3 | 172.31.12.77 | 3.216.125.99 | ec2-user |

**Important:** These IP addresses belong to the instances created during this execution. If you recreate the instances, AWS may assign different IP addresses. Always check the EC2 console and update the inventory file.

## 4. Prerequisites

Before starting, make sure you have:

- An AWS account.
- An Ubuntu EC2 instance to use as the Control Node.
- Ansible installed on the Control Node.
- An AWS IAM identity with permission to create and manage EC2 instances.
- An EC2 key pair.
- A security group that allows the required SSH connections.
- Python and the required AWS SDK libraries.

### Step 1: Connect to the Control Node

Connect to your Ubuntu Control Node using SSH or EC2 Instance Connect.

Check the operating system:

```bash
cat /etc/os-release
```

### Step 2: Install Ansible

Update the package list:

```bash
sudo apt update
```

Install Ansible:

```bash
sudo apt install ansible -y
```

Verify the installation:

```bash
ansible --version
```

### Step 3: Install AWS Dependencies

Install Boto3 and Botocore:

```bash
sudo apt install python3-boto3 python3-botocore -y
```

Install the Amazon AWS collection:

```bash
ansible-galaxy collection install amazon.aws
```

Verify the collection:

```bash
ansible-galaxy collection list
```

The `amazon.aws` collection provides modules that allow Ansible to interact with AWS services.

## 5. AWS Preparation

### Step 1: Create an IAM Identity

Ansible needs AWS permissions to provision EC2 instances.

Create an IAM user or use an appropriate IAM role attached to the Control Node.

For the IAM user approach used in this practical:

1. Open the AWS IAM console.
2. Create an IAM user named `ansible-user`.
3. Grant the required EC2 permissions.
4. Create an access key for programmatic access.
5. Keep the Access Key ID and Secret Access Key secure.

Do not share these credentials or upload them to GitHub.

### Step 2: Create an EC2 Key Pair

Create an EC2 key pair with the following name:

```text
ansible-key
```

Download the private key file:

```text
ansible-key.pem
```

The key pair name must match the `key_name` used in the EC2 provisioning playbook.

### Step 3: Prepare the Security Group

Use a security group that allows:

- SSH access on port 22 from the Control Node.
- Required SSH access from your own machine if you need to connect directly.

For communication between the Control Node and Managed Nodes, make sure the security group rules permit SSH traffic between them.

In this project, all three Managed Nodes were created in the same VPC, subnet and security group.

## 6. Create the Project Directory

Create a working directory:

```bash
mkdir -p ~/ansible-real-time-project/day7
```

Move into the directory:

```bash
cd ~/ansible-real-time-project/day7
```

Create the required files:

```bash
touch ec2-instance-create.yaml
touch ec2-instance-stop.yaml
touch inventory
```

The project structure:

```text
day7/
|
|-- ec2-instance-create.yaml
|-- ec2-instance-stop.yaml
|-- inventory
|-- password.yaml
|-- password.txt
|-- ansible-key.pem
```

The credential and private-key files are sensitive. They must never be committed to GitHub.

## 7. Configure AWS Credentials Using Ansible Vault

Ansible Vault helps encrypt sensitive information such as AWS credentials.

### Step 1: Create the Vault Password File

```bash
vim password.txt
```

Enter a strong password that you will use to encrypt and decrypt the credentials.

Save and exit:

```text
Esc
:wq
```

Restrict access to the file:

```bash
chmod 600 password.txt
```

### Step 2: Create the AWS Credentials File

```bash
vim password.yaml
```

Add the following structure using your own AWS credentials:

```yaml
ec2_access_key: "YOUR_AWS_ACCESS_KEY"
ec2_secret_key: "YOUR_AWS_SECRET_KEY"
```

Save and exit:

```text
Esc
:wq
```

Restrict access:

```bash
chmod 600 password.yaml
```

### Step 3: Encrypt the Credentials

Run:

```bash
ansible-vault encrypt password.yaml --vault-password-file password.txt
```

The credentials file is now encrypted.

To view the encrypted file:

```bash
cat password.yaml
```

To edit it securely:

```bash
ansible-vault edit password.yaml --vault-password-file password.txt
```

**Security note:** Never commit `password.txt`, `password.yaml` or your `.pem` file to GitHub.

If you use an IAM role instead of access keys, configure the playbook to use the role's credentials rather than storing access keys in Vault.

## 8. Practical 1: Provision Three EC2 Instances

### Objective

Create three EC2 instances using the `amazon.aws.ec2_instance` Ansible module.

### Step 1: Create the Playbook

```bash
vim ec2-instance-create.yaml
```

Add the following configuration:

```yaml
---
- name: Create three EC2 instances
  hosts: localhost
  connection: local
  gather_facts: false

  vars_files:
    - password.yaml

  tasks:
    - name: Create EC2 instances
      amazon.aws.ec2_instance:
        name: "{{ item.name }}"
        key_name: ansible-key
        instance_type: t2.micro
        image_id: "{{ item.image }}"
        region: us-east-1
        vpc_subnet_id: subnet-08c7deca2bf1fc730
        network_interfaces:
          - assign_public_ip: true
            groups:
              - sg-0f0b23b1d02bdccd9
        aws_access_key: "{{ ec2_access_key }}"
        aws_secret_key: "{{ ec2_secret_key }}"
        state: present
        wait: true
        tags:
          Name: "{{ item.name }}"

      loop:
        - name: Manage-Node-1
          image: ami-0d97d7f07b25dd693

        - name: Manage-Node-2
          image: ami-0d97d7f07b25dd693

        - name: Manage-Node-3
          image: ami-0c230065b91270bc6
```

Save the file:

```text
Esc
:wq
```

### Understanding the Playbook

| Parameter | Purpose |
|---|---|
| `hosts: localhost` | Runs the playbook on the Control Node. |
| `connection: local` | Uses a local connection. |
| `gather_facts: false` | Skips gathering facts for localhost. |
| `vars_files` | Loads the encrypted credentials file. |
| `amazon.aws.ec2_instance` | Creates and manages EC2 instances. |
| `name` | Sets the EC2 instance name. |
| `key_name` | Specifies the EC2 key pair. |
| `instance_type` | Defines the EC2 instance type. |
| `image_id` | Specifies the AMI. |
| `region` | Selects the AWS region. |
| `vpc_subnet_id` | Selects the subnet. |
| `assign_public_ip` | Assigns a public IP address. |
| `groups` | Associates the security group. |
| `state: present` | Ensures the instances exist. |
| `wait: true` | Waits for the requested instance state. |
| `loop` | Repeats the task for each instance. |

### Step 2: Execute the Playbook

```bash
ansible-playbook ec2-instance-create.yaml --vault-password-file password.txt
```

### Step 3: Verify in AWS

Open:

AWS Console → EC2 → Instances

Check that all three instances are created and running.

Record their private IP addresses. You will need these IP addresses in the inventory file.

**Important:** The AMI IDs in this playbook are specific to the original AWS environment and region. Verify that they are available and appropriate for your account before recreating the project.

## 9. Practical 2: Configure SSH Key-Based Authentication

### Objective

Allow the Ansible Control Node to connect to all three Managed Nodes using SSH private-key authentication.

### Step 1: Upload the Private Key

Upload the downloaded `ansible-key.pem` file from your local machine to the Control Node.

Place it inside the SSH directory:

```bash
mkdir -p ~/.ssh
```

Move the uploaded key into the directory if necessary:

```bash
mv ~/ansible-key.pem ~/.ssh/
```

Set the correct permissions:

```bash
chmod 400 ~/.ssh/ansible-key.pem
```

SSH requires the private key to have restricted permissions.

### Step 2: Test SSH Connections

Connect to the first Ubuntu instance:

```bash
ssh -i ~/.ssh/ansible-key.pem ubuntu@172.31.10.169
```

Connect to the second Ubuntu instance:

```bash
ssh -i ~/.ssh/ansible-key.pem ubuntu@172.31.7.49
```

Connect to the Amazon Linux instance:

```bash
ssh -i ~/.ssh/ansible-key.pem ec2-user@172.31.12.77
```

After confirming each connection, exit the remote machine:

```bash
exit
```

Make sure all three SSH connections work before moving forward.

**Note:** Ubuntu instances normally use the `ubuntu` SSH user. Amazon Linux 2023 normally uses `ec2-user`.

## 10. Configure the Ansible Inventory

### Objective

Create an inventory file containing the Managed Nodes and SSH configuration.

Open the inventory file:

```bash
vim inventory
```

Add:

```ini
[all]
ubuntu@172.31.10.169
ubuntu@172.31.7.49
ec2-user@172.31.12.77

[all:vars]
ansible_ssh_private_key_file=~/.ssh/ansible-key.pem
```

Save and exit:

```text
Esc
:wq
```

### Understanding the Inventory

- `[all]` defines the group containing all Managed Nodes.
- Each line contains the SSH username and private IP address of a Managed Node.
- `[all:vars]` defines variables applicable to every host in the group.
- `ansible_ssh_private_key_file` specifies the private key used for SSH authentication.

If the instances receive new private IP addresses, update this file.

## 11. Test Connectivity Using the Ansible Ping Module

Run:

```bash
ansible all -i inventory -m ping
```

Expected output:

```text
172.31.10.169 | SUCCESS => {
    "changed": false,
    "ping": "pong"
}

172.31.7.49 | SUCCESS => {
    "changed": false,
    "ping": "pong"
}

172.31.12.77 | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
```

The `pong` response confirms that Ansible can connect to the Managed Nodes and execute the required module.

**Note:** Ansible's ping module is not the same as the ICMP network ping command. It checks whether Ansible can connect to the host and execute a Python-based module.

## 12. Practical 3: Shut Down Only Ubuntu Instances

### Objective

Use Ansible Facts and conditional execution to shut down only Ubuntu instances while keeping the Amazon Linux instance running.

### Step 1: Create the Shutdown Playbook

```bash
vim ec2-instance-stop.yaml
```

Add:

```yaml
---
- name: Shutdown Ubuntu instances only
  hosts: all
  become: true

  tasks:
    - name: Print gathered facts
      ansible.builtin.debug:
        var: ansible_facts

    - name: Shutdown Ubuntu instances
      ansible.builtin.command: /sbin/shutdown -h now
      when: ansible_facts['os_family'] == "Debian"
```

Save and exit:

```text
Esc
:wq
```

### Step 2: Understand Ansible Facts

Ansible Facts are system information collected automatically from Managed Nodes.

They include information such as:

- Operating system.
- Operating system family.
- Hostname.
- IP addresses.
- Memory.
- Processor information.

In this playbook, we use:

```yaml
ansible_facts['os_family']
```

The operating system families identified during our practical were:

| Managed Node | Operating System Family |
|---|---|
| Manage-Node-1 | Debian |
| Manage-Node-2 | Debian |
| Manage-Node-3 | RedHat |

Ubuntu belongs to the Debian family, while Amazon Linux belongs to the RedHat family.

### Step 3: Understand Conditional Execution

The condition used in the playbook is:

```yaml
when: ansible_facts['os_family'] == "Debian"
```

This means:

- If the operating system family is Debian, execute the shutdown command.
- If the operating system family is not Debian, skip the task.

### Step 4: Understand Privilege Escalation

```yaml
become: true
```

This allows Ansible to execute the task with elevated privileges.

Shutting down a system requires administrative permissions.

### Step 5: Execute the Playbook

**Warning:** This command shuts down the Ubuntu Managed Nodes immediately. Run it only when you are ready to stop those instances.

```bash
ansible-playbook -i inventory ec2-instance-stop.yaml
```

### Step 6: Understand the Output

During execution:

- Manage-Node-1: Shutdown task executed.
- Manage-Node-2: Shutdown task executed.
- Manage-Node-3: Shutdown task skipped.

The Ubuntu nodes may show an unreachable SSH connection or connection closure after the shutdown command runs.

This is expected because the machines shut down before Ansible can complete its normal SSH communication.

### Step 7: Verify in AWS Console

Open:

AWS Console → EC2 → Instances

Verify the final state:

| Instance | Expected State |
|---|---|
| Ansible-Control-Node | Running |
| Manage-Node-1 | Stopped |
| Manage-Node-2 | Stopped |
| Manage-Node-3 | Running |

This confirms that the conditional execution worked correctly.

## 13. Final Project Structure

The GitHub project folder contains:

```text
17 - Ansible Real-Time AWS EC2 Provisioning and Management Project/
|
|-- README.md
|-- .gitignore
|-- ec2-instance-create.yaml
|-- ec2-instance-stop.yaml
|-- inventory
```

The following sensitive files remain on the Control Node and are not uploaded:

```text
password.txt
password.yaml
ansible-key.pem
```

### .gitignore

The `.gitignore` file contains:

```gitignore
password.txt
password.yaml
*.pem
```

This prevents these sensitive files from being tracked when they are inside the repository.

## 14. How to Execute This Project Again

Use this checklist when repeating the practical after a few weeks or months.

1. Launch or connect to an Ubuntu EC2 Control Node.
2. Install Ansible and the required AWS dependencies.
3. Install the `amazon.aws` collection.
4. Configure AWS permissions using an IAM role or securely stored IAM credentials.
5. Create or identify the EC2 key pair.
6. Prepare the security group and subnet.
7. Create the project directory.
8. Create the Vault password file and encrypted AWS credentials file if using access keys.
9. Run `ec2-instance-create.yaml`.
10. Verify all three instances in the AWS Console.
11. Upload the private key to the Control Node.
12. Set private key permissions using `chmod 400`.
13. Test SSH connections to all three Managed Nodes.
14. Update the inventory file with the current private IP addresses.
15. Run the Ansible ping module.
16. Run the shutdown playbook.
17. Verify the final instance states in AWS.

## 15. Important Commands Reference

| Command | Purpose |
|---|---|
| `ansible --version` | Check Ansible version. |
| `ansible-galaxy collection list` | List installed Ansible collections. |
| `chmod 400 ~/.ssh/ansible-key.pem` | Set private key permissions. |
| `ssh -i ~/.ssh/ansible-key.pem user@IP` | Connect to a Managed Node. |
| `ansible all -i inventory -m ping` | Test Managed Node connectivity. |
| `ansible-playbook ec2-instance-create.yaml --vault-password-file password.txt` | Provision EC2 instances. |
| `ansible-playbook -i inventory ec2-instance-stop.yaml` | Shut down matching Ubuntu instances. |
| `ansible-vault encrypt password.yaml --vault-password-file password.txt` | Encrypt credentials. |
| `ansible-vault edit password.yaml --vault-password-file password.txt` | Edit encrypted credentials. |

## 16. Key Learnings

Through this project, I learned:

- How to provision AWS EC2 instances using Ansible.
- How to use the `amazon.aws.ec2_instance` module.
- How to use loops to create multiple EC2 instances.
- How to configure SSH key-based authentication.
- How to create and manage Ansible inventories.
- How to test connectivity using the Ansible ping module.
- How to gather system information using Ansible Facts.
- How to use conditional execution with the `when` statement.
- How to use privilege escalation with `become`.
- How to protect AWS credentials using Ansible Vault.

## 17. Project Completion

Successfully completed the Ansible Real-Time AWS EC2 Provisioning and Management Project.

Three EC2 instances were provisioned using Ansible. SSH authentication was configured between the Control Node and Managed Nodes. Ansible Facts were used to identify operating system families and selectively shut down Ubuntu instances while keeping the Amazon Linux instance running.

**Project Repository:**  
https://github.com/Yuvii2102/My-Complete-DevOps-Projects/tree/main/17%20-%20Ansible%20Real-Time%20AWS%20EC2%20Provisioning%20and%20Management%20Project

---

**Important reminder:** The instance IDs, IP addresses, AMI IDs, subnet ID and security group ID in this documentation belong to the original execution. Verify or replace them when running the project in a new AWS environment.
