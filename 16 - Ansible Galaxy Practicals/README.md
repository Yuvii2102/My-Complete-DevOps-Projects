# Project: Installing Docker on an EC2 Managed Node Using Ansible Galaxy

## 1. Introduction

In this practical, I used Ansible Galaxy to install Docker on a remote AWS EC2 instance.

I created two Ubuntu EC2 instances:

- **Ansible Control Node:** Runs Ansible and manages the remote server.
- **Ansible Managed Node:** Receives instructions from the Control Node. Docker will be installed on this server.

Instead of writing all Docker installation tasks manually, I used an existing Ansible Galaxy role called `geerlingguy.docker`.

The role was downloaded and installed on the Control Node and then executed through an Ansible Playbook to configure the Managed Node.

## 2. What is Ansible Galaxy?

Ansible Galaxy is an online platform where we can discover, download and share reusable Ansible roles and collections.

Ansible roles help us reuse automation tasks instead of writing everything from scratch.

Official website:

https://galaxy.ansible.com/

Official Ansible Galaxy documentation:

https://docs.ansible.com/projects/ansible/latest/galaxy.html

## 3. Finding and Selecting the Docker Role

For this practical, I needed an existing Ansible role that could automate Docker installation on a Linux server.

I followed these steps:

1. Opened the Ansible Galaxy website.
2. Searched for Docker-related roles.
3. Found the role named `geerlingguy.docker`.
4. Opened the role page and reviewed its description and installation instructions.
5. Checked its GitHub repository to understand its tasks, variables and requirements.

### Role Details

| Detail | Information |
|---|---|
| Role Name | geerlingguy.docker |
| Author / Maintainer | Jeff Geerling |
| Description | Docker for Linux |
| Version Used | 8.0.0 |
| Source | Ansible Galaxy |
| Repository | GitHub |

### Official Role Links

**1. Ansible Galaxy Role Page**

https://galaxy.ansible.com/geerlingguy/docker

This is where I discovered the role and found its installation instructions.

**2. GitHub Repository**

https://github.com/geerlingguy/ansible-role-docker

This repository contains the role's source code, README, default variables, handlers and task files.

I used the README to understand how the role works and inspected its task files after installing it.

## 4. Architecture

```text
              Windows Machine
                     |
                     | SSH
                     |
                     v
          +----------------------+
          |   Ansible Control    |
          |        Node          |
          |                      |
          |  Ubuntu EC2          |
          |  Ansible Installed   |
          |  Galaxy Role         |
          +----------------------+
                     |
                     | SSH
                     | Ansible Playbook
                     |
                     v
          +----------------------+
          |   Ansible Managed    |
          |        Node          |
          |                      |
          |  Ubuntu EC2          |
          |  Docker Installed    |
          |  Docker Compose      |
          +----------------------+
```

## 5. AWS EC2 Instance Configuration

I created two new EC2 instances using Ubuntu Server 26.04.1 LTS.

| Configuration | Control Node | Managed Node |
|---|---|---|
| Instance Name | Ansible-Control-Node | Ansible-Managed-Node |
| Operating System | Ubuntu | Ubuntu |
| Instance Type | t3.small | t3.small |
| Storage | 20 GiB | 20 GiB |
| Key Pair | ansible-key | ansible-key |
| Private IP | 172.31.17.219 | 172.31.24.152 |

### Security Group Configuration

**Control Node Security Group**

- Allowed inbound SSH traffic on port 22 from my IP address.

**Managed Node Security Group**

- Allowed inbound SSH traffic on port 22 from the Control Node's security group.
- Kept the default outbound rules.

Both instances used the same EC2 key pair named `ansible-key`.

## 6. Connect to the Control Node

From Windows PowerShell, I connected to the Control Node using SSH.

```bash
ssh -i "C:\Users\Yuvraj\Desktop\ansible-key.pem" ubuntu@CONTROL_NODE_PUBLIC_IP
```

Before connecting, I configured the private key file permissions on Windows.

```powershell
icacls "C:\Users\Yuvraj\Desktop\ansible-key.pem" /inheritance:r
```

```powershell
icacls "C:\Users\Yuvraj\Desktop\ansible-key.pem" /grant:r "$($env:USERNAME):(R)"
```

These commands removed inherited permissions and gave my Windows account read access to the private key.

## 7. Install Ansible on the Control Node

After connecting to the Control Node, I updated the package list.

```bash
sudo apt update
```

Then I installed Ansible.

```bash
sudo apt install ansible -y
```

To verify the installation:

```bash
ansible --version
```

## 8. Connect the Control Node to the Managed Node

To allow the Control Node to connect to the Managed Node, I copied the private key from my Windows machine to the Control Node.

I opened another Windows PowerShell window and executed:

```powershell
scp -i "C:\Users\Yuvraj\Desktop\ansible-key.pem" "C:\Users\Yuvraj\Desktop\ansible-key.pem" ubuntu@CONTROL_NODE_PUBLIC_IP:/home/ubuntu/ansible-key.pem
```

This copied the private key to the Control Node.

Back on the Control Node, I restricted access to the copied key:

```bash
chmod 400 ~/ansible-key.pem
```

Now I tested the SSH connection to the Managed Node:

```bash
ssh -i ~/ansible-key.pem ubuntu@172.31.24.152
```

The connection was successful.

After verifying the connection, I returned to the Control Node:

```bash
exit
```

## 9. Create the Ansible Project Directory

On the Control Node, I created a directory for this practical.

```bash
mkdir ansible-galaxy-practical
cd ansible-galaxy-practical
```

## 10. Install the Docker Role from Ansible Galaxy

After selecting the role from the Ansible Galaxy website, I installed it on the Control Node.

```bash
ansible-galaxy role install geerlingguy.docker
```

The role was installed successfully.

**Installation details:**

- Role Name: `geerlingguy.docker`
- Version: `8.0.0`
- Installation Path: `/home/ubuntu/.ansible/roles/geerlingguy.docker`

The `ansible-galaxy role install` command downloads the role from Ansible Galaxy and stores it locally on the Control Node.

## 11. Explore the Installed Role

After installing the role, I explored its directory.

```bash
ls ~/.ansible/roles/geerlingguy.docker
```

The directory contained the following files and folders:

```text
LICENSE
README.md
defaults
handlers
meta
molecule
tasks
vars
```

I also inspected the main task file:

```bash
cat ~/.ansible/roles/geerlingguy.docker/tasks/main.yml
```

The main task file contains instructions for:

- Loading OS-specific variables.
- Installing Docker packages.
- Installing the Docker Compose plugin.
- Configuring Docker daemon options.
- Starting and enabling the Docker service.
- Managing Docker users.

The role automatically selects the appropriate installation tasks based on the operating system.

Since my Managed Node uses Ubuntu, the role executes the Debian-specific setup tasks.

## 12. Configure the Ansible Inventory

An inventory file tells Ansible which servers it needs to manage.

I created an inventory file using Vim:

```bash
vim inventory.ini
```

I added the following configuration:

```ini
[docker_servers]
172.31.24.152 ansible_user=ubuntu ansible_ssh_private_key_file=/home/ubuntu/ansible-key.pem
```

### Explanation

- `[docker_servers]` defines an inventory group.
- `172.31.24.152` is the Managed Node's private IP address.
- `ansible_user=ubuntu` specifies the SSH user.
- `ansible_ssh_private_key_file` specifies the private key used for SSH authentication.

## 13. Test Ansible Connectivity

Before running the playbook, I tested whether Ansible could communicate with the Managed Node.

```bash
ansible docker_servers -i inventory.ini -m ping
```

The connection was successful.

Expected output:

```text
172.31.24.152 | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
```

This confirmed that Ansible could connect to the Managed Node and execute a basic module.

## 14. Create the Ansible Playbook

Next, I created a playbook to install Docker using the Galaxy role.

```bash
vim docker-playbook.yaml
```

I added the following YAML configuration:

```yaml
---
- name: Install Docker Using Ansible Galaxy Role
  hosts: docker_servers
  become: true

  roles:
    - geerlingguy.docker
```

### Explanation

| Keyword | Description |
|---|---|
| `name` | Describes the purpose of the playbook. |
| `hosts` | Specifies the inventory group to target. |
| `become: true` | Executes tasks with elevated privileges. |
| `roles` | Specifies the Ansible Galaxy role to execute. |
| `geerlingguy.docker` | The role responsible for installing Docker. |

The playbook uses the role installed on the Control Node to configure the Managed Node.

## 15. Execute the Playbook

I executed the playbook from the Control Node:

```bash
ansible-playbook -i inventory.ini docker-playbook.yaml
```

Ansible connected to the Managed Node and executed the tasks provided by the `geerlingguy.docker` role.

The playbook completed successfully.

### Play Recap

```text
PLAY RECAP

172.31.24.152 : ok=14 changed=4 unreachable=0 failed=0 skipped=13 rescued=0 ignored=0
```

### Understanding the Play Recap

| Result | Meaning |
|---|---|
| `ok=14` | 14 tasks completed successfully. |
| `changed=4` | 4 tasks made changes to the Managed Node. |
| `unreachable=0` | The Managed Node was reachable. |
| `failed=0` | No tasks failed. |
| `skipped=13` | 13 tasks were skipped because their conditions were not met. |

The Docker service was also started and enabled.

## 16. Verify Docker Installation

After the playbook completed, I connected to the Managed Node:

```bash
ssh -i ~/ansible-key.pem ubuntu@172.31.24.152
```

### Check Docker Version

```bash
docker --version
```

### Check Docker Service Status

```bash
sudo systemctl status docker
```

The service was running successfully.

Important output:

```text
Loaded: loaded (/usr/lib/systemd/system/docker.service; enabled)
Active: active (running)
```

This confirmed that Docker was running and enabled to start automatically during system boot.

### Check Docker Compose Version

```bash
docker compose version
```

Output:

```text
Docker Compose version v5.7.0
```

## 17. Run a Test Container

Finally, I verified Docker by running the official Hello World container.

```bash
sudo docker run hello-world
```

Docker downloaded the `hello-world` image from Docker Hub, created a container and executed it.

The following message confirmed that Docker was working correctly:

```text
Hello from Docker!

This message shows that your installation appears to be working correctly.
```

## 18. Final Result

Successfully installed and verified Docker on an AWS EC2 Managed Node using an Ansible Galaxy role.

The entire installation was automated through an Ansible Playbook without manually installing Docker on the Managed Node.

### What I Learned

- Understanding Ansible Galaxy and its role repository.
- Finding and selecting reusable Ansible roles.
- Installing roles from Ansible Galaxy.
- Understanding the relationship between Ansible Galaxy and GitHub role repositories.
- Exploring the structure of an Ansible role.
- Configuring SSH authentication between EC2 instances.
- Creating and configuring Ansible inventory files.
- Testing remote connectivity using the Ansible ping module.
- Executing Galaxy roles through Ansible Playbooks.
- Automating Docker installation on a remote EC2 instance.
- Verifying Docker and Docker Compose installations.

### Tools and Technologies Used

- AWS EC2
- Ubuntu Linux
- Ansible
- Ansible Galaxy
- geerlingguy.docker Role
- SSH
- Docker
- Docker Compose
- Vim

## 19. References

The following resources were used to find, understand and install the Docker role:

1. **Ansible Galaxy:** https://galaxy.ansible.com/
2. **geerlingguy.docker Role:** https://galaxy.ansible.com/geerlingguy/docker
3. **GitHub Repository:** https://github.com/geerlingguy/ansible-role-docker
4. **Ansible Galaxy Documentation:** https://docs.ansible.com/projects/ansible/latest/galaxy.html

### Important Note

The private SSH key is used only for authentication. It should never be uploaded to GitHub or committed to a repository.

For a reusable setup, replace the example IP addresses and local key paths with the values from your own EC2 environment.
