# Project 15 – Deploy a Static Website Using Ansible Roles

# Practical Implementation

## 1. Project Overview

In this practical, I deployed a static website using Apache by creating and using an Ansible Role.

Instead of writing all automation tasks directly inside a playbook, I organized them into a separate role named `httpd`.

This practical helped me understand how Ansible Roles make automation more organized, reusable, and maintainable.

### Objective

- Set up an Ansible Control Node and a Managed Node.
- Install Ansible on the Control Node.
- Configure SSH connectivity between both instances.
- Create an Ansible Role using Ansible Galaxy.
- Organize Apache deployment tasks inside the role.
- Deploy a static website using Apache.
- Verify the deployment through a web browser.

## 2. Infrastructure Setup

For this practical, I used two AWS EC2 Ubuntu instances.

| Component | Description |
|---|---|
| Control Node | EC2 instance where Ansible is installed |
| Managed Node | EC2 instance where Apache is deployed |
| Operating System | Ubuntu |
| Automation Tool | Ansible |
| Web Server | Apache2 |
| Authentication | SSH Key-Based Authentication |

### 2.1 Launch the EC2 Instances

I launched two Ubuntu EC2 instances through the AWS Management Console.

**Instance 1 – Ansible Control Node**

This instance is responsible for running Ansible commands and managing the second instance.

**Instance 2 – Managed Node**

This instance receives instructions from the Control Node. Apache will be installed and configured on this instance.

### 2.2 Configure Security Groups

I configured the security groups to allow the required network traffic.

**Control Node inbound rules:**

| Type | Port | Source |
|---|---:|---|
| SSH | 22 | My IP address |

**Managed Node inbound rules:**

| Type | Port | Source |
|---|---:|---|
| SSH | 22 | Control Node security group |
| HTTP | 80 | 0.0.0.0/0 |

The SSH rule allows the Control Node to connect to the Managed Node.

The HTTP rule allows users to access the website through a browser.

Both instances must be able to communicate over their private IP addresses.

### 2.3 Connect to the Control Node

From my local terminal, I connected to the Control Node using its public IPv4 address and EC2 private key.

```bash
ssh -i "your-key.pem" ubuntu@CONTROL_NODE_PUBLIC_IP
```

After connecting, I verified the user:

```bash
whoami
```

Expected output:

```text
ubuntu
```

### 2.4 Identify the Private IP Addresses

I checked the private IP addresses of both instances in the AWS EC2 console.

I used these private IP addresses in my practical:

- Control Node: `172.31.24.9`
- Managed Node: `172.31.26.36`

**Note:** These are the IP addresses from my original setup. If I launch new instances, I must use their current private IP addresses instead.

## 3. Install Ansible on the Control Node

After connecting to the Control Node, I updated the package repository.

```bash
sudo apt update
```

Then I installed Ansible:

```bash
sudo apt install ansible -y
```

I verified the installation:

```bash
ansible --version
```

If Ansible is installed successfully, the command displays the installed version and configuration details.

Ansible only needs to be installed on the Control Node for this practical.

## 4. Configure SSH Connectivity

Ansible uses SSH to communicate with Linux managed nodes.

Before running any playbook, I needed to make sure that the Control Node could connect to the Managed Node.

### 4.1 Check the SSH Key

The Control Node needs access to a private SSH key that is authorized for the Managed Node.

If I launch both EC2 instances using the same EC2 key pair, I can use that key for the connection.

I transferred the PEM file securely from my local computer to the Control Node.

From my local Windows PowerShell, the command is:

```powershell
scp -i "C:\path\to\your-key.pem" "C:\path\to\your-key.pem" ubuntu@CONTROL_NODE_PUBLIC_IP:/home/ubuntu/
```

On the Control Node, I restricted access to the key:

```bash
chmod 400 ~/your-key.pem
```

**Security note:** Never upload private SSH keys to GitHub or place them inside a publicly accessible project directory.

### 4.2 Test SSH Connectivity

From the Control Node, I connected to the Managed Node using its private IP address.

```bash
ssh -i ~/your-key.pem ubuntu@MANAGED_NODE_PRIVATE_IP
```

If the connection is successful, I can access the Managed Node terminal.

I verified the hostname:

```bash
hostname
```

Then I exited the Managed Node:

```bash
exit
```

I returned to the Control Node.

**Important:** The private key must correspond to a key pair authorized on the Managed Node. The Managed Node security group must also allow SSH from the Control Node.

## 5. Create the Project Directory

I created a separate directory for this practical.

```bash
mkdir -p ~/ansible-roles-practical
```

I entered the directory:

```bash
cd ~/ansible-roles-practical
```

This directory will contain the Ansible Role, playbook, inventory, and website file.

## 6. Create an Ansible Role

Ansible provides the `ansible-galaxy` command to create roles with a predefined directory structure.

I created a role named `httpd`:

```bash
ansible-galaxy role init httpd
```

This command automatically generated the standard role directory structure.

I verified it using:

```bash
ls -R httpd
```

Generated structure:

```text
httpd/
├── defaults/
│   └── main.yml
├── files/
├── handlers/
│   └── main.yml
├── meta/
│   └── main.yml
├── tasks/
│   └── main.yml
├── templates/
├── tests/
│   ├── inventory
│   └── test.yml
├── vars/
│   └── main.yml
└── README.md
```

## 7. Create the Static Website

I created an HTML file that will be deployed to the Managed Node.

I opened a new file:

```bash
vim httpd/files/index.html
```

I added the following HTML content:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Ansible Roles Practical</title>
</head>
<body>
    <h1>I am learning Ansible</h1>
    <p>This website was deployed using Ansible Roles.</p>
</body>
</html>
```

I saved the file.

The `files/` directory stores static files that Ansible can copy to managed nodes.

## 8. Configure the Ansible Role Tasks

Next, I opened the main task file inside the role.

```bash
vim httpd/tasks/main.yml
```

I added the following tasks:

```yaml
---
- name: Install Apache
  ansible.builtin.apt:
    name: apache2
    state: present
    update_cache: true

- name: Copy website file
  ansible.builtin.copy:
    src: index.html
    dest: /var/www/html/index.html

- name: Start and enable Apache
  ansible.builtin.service:
    name: apache2
    state: started
    enabled: true
```

### Understanding the Tasks

**Task 1: Install Apache**

```yaml
- name: Install Apache
  ansible.builtin.apt:
    name: apache2
    state: present
    update_cache: true
```

- `ansible.builtin.apt` – Uses the APT package manager.
- `name: apache2` – Specifies the Apache package.
- `state: present` – Ensures Apache is installed.
- `update_cache: true` – Updates the package cache before installation.

**Task 2: Copy Website File**

```yaml
- name: Copy website file
  ansible.builtin.copy:
    src: index.html
    dest: /var/www/html/index.html
```

- `ansible.builtin.copy` – Copies a file from the Control Node to the Managed Node.
- `src: index.html` – Refers to the file inside the role's `files/` directory.
- `dest` – Specifies the destination path on the Managed Node.

**Task 3: Start and Enable Apache**

```yaml
- name: Start and enable Apache
  ansible.builtin.service:
    name: apache2
    state: started
    enabled: true
```

- `ansible.builtin.service` – Manages system services.
- `name: apache2` – Specifies the Apache service.
- `state: started` – Ensures the service is running.
- `enabled: true` – Ensures Apache starts automatically during system boot.

## 9. Create the Role-Based Playbook

After configuring the role, I created the main playbook that calls the `httpd` role.

I opened the playbook:

```bash
vim first-playbook.yaml
```

I added:

```yaml
---
- name: Deploy Static Website Using Ansible Roles
  hosts: all
  become: true

  roles:
    - httpd
```

### Understanding the Playbook

- `name` – Describes the purpose of the playbook.
- `hosts: all` – Executes the play against all hosts in the selected inventory.
- `become: true` – Uses privilege escalation to execute administrative tasks.
- `roles` – Specifies the roles to execute.
- `httpd` – Calls the role created earlier.

Ansible automatically loads the tasks from:

```text
httpd/tasks/main.yml
```

This means I do not need to write all the Apache installation and deployment tasks again inside the playbook.

## 10. Configure the Inventory File

Next, I configured the inventory file to identify the Managed Node.

I created the inventory:

```bash
vim inventory.ini
```

I added the following configuration:

```ini
[webservers]
172.31.26.36 ansible_user=ubuntu ansible_ssh_private_key_file=/home/ubuntu/your-key.pem
```

Replace the IP address and private key path with the values from the current environment.

### Understanding the Inventory

- `[webservers]` – Defines a group of managed hosts.
- `172.31.26.36` – Private IP address of the Managed Node.
- `ansible_user=ubuntu` – Specifies the SSH user.
- `ansible_ssh_private_key_file` – Specifies the private key used for SSH authentication.

## 11. Protect the Inventory File

The inventory file contains environment-specific information such as server IP addresses and private key paths.

I created a `.gitignore` file:

```bash
vim .gitignore
```

I added:

```text
inventory.ini
```

This prevents the local inventory file from being accidentally committed to GitHub.

I can also verify that Git ignores the file using:

```bash
git check-ignore -v inventory.ini
```

## 12. Verify Ansible Connectivity

Before running the playbook, I checked whether the Control Node could communicate with the Managed Node.

```bash
ansible all -i inventory.ini -m ping
```

Expected successful output:

```text
172.31.26.36 | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
```

The Ansible `ping` module verifies Ansible connectivity to the managed node. It is not an ICMP network ping.

## 13. Execute the Ansible Playbook

Once the inventory and role were ready, I executed the playbook.

```bash
ansible-playbook -i inventory.ini first-playbook.yaml
```

During execution, Ansible performed the following operations:

1. Connected to the Managed Node through SSH.
2. Installed Apache2.
3. Copied `index.html` to `/var/www/html/index.html`.
4. Started the Apache service.
5. Enabled Apache to start automatically during system boot.

Ansible displayed the execution results for each task.

The play recap should show the Managed Node with successful task execution and no failed tasks.

## 14. Verify the Deployment

After executing the playbook, I verified the Apache service and website.

### 14.1 Connect to the Managed Node

```bash
ssh -i ~/your-key.pem ubuntu@MANAGED_NODE_PRIVATE_IP
```

### 14.2 Check Apache Service Status

```bash
sudo systemctl status apache2
```

Expected status:

```text
Active: active (running)
```

### 14.3 Verify the Website Locally

```bash
curl -I http://localhost
```

Expected response:

```text
HTTP/1.1 200 OK
```

I also checked the website content:

```bash
curl http://localhost
```

This should display the HTML content created earlier.

### 14.4 Access the Website Through a Browser

I opened the Managed Node's public IPv4 address in my browser:

```text
http://MANAGED_NODE_PUBLIC_IP
```

The website displayed:

**I am learning Ansible**

This confirmed that the static website was successfully deployed using an Ansible Role.

For browser access, the Managed Node must have a public IPv4 address or another accessible network route, and its security group must allow inbound HTTP traffic on TCP port 80.

## 15. Final Project Structure

After completing the practical, my directory structure looked like this:

```text
ansible-roles-practical/
│
├── httpd/
│   ├── defaults/
│   │   └── main.yml
│   │
│   ├── files/
│   │   └── index.html
│   │
│   ├── handlers/
│   │   └── main.yml
│   │
│   ├── meta/
│   │   └── main.yml
│   │
│   ├── tasks/
│   │   └── main.yml
│   │
│   ├── templates/
│   │
│   ├── tests/
│   │   ├── inventory
│   │   └── test.yml
│   │
│   ├── vars/
│   │   └── main.yml
│   │
│   └── README.md
│
├── first-playbook.yaml
├── inventory.ini
└── .gitignore
```

The `inventory.ini` file is stored locally and excluded from Git tracking.

## 16. Final Outcome

Successfully created an Ansible Role named `httpd` and used it to automate the deployment of a static website using Apache.

Through this practical, I learned how to:

- Set up an Ansible Control Node and Managed Node.
- Install Ansible on Ubuntu.
- Configure SSH connectivity between EC2 instances.
- Create roles using Ansible Galaxy.
- Organize automation tasks into reusable components.
- Use the `files/` directory to manage static files.
- Execute roles through an Ansible playbook.
- Configure inventories for managed nodes.
- Deploy and verify an Apache web server using Ansible.

**Key Learning:** Ansible Roles help convert large playbooks into smaller, reusable, and well-organized automation components.

---

**End of Project 15 – Ansible Roles Practicals**
