# Infrastructure Orchestration Using Terraform, Ansible and Spacelift

## 1. Project Overview

In this project, I used Terraform, Ansible and Spacelift to automate AWS infrastructure provisioning and configuration management.

The main objective was to provision EC2 instances using Terraform and configure them using Ansible. Spacelift was used to manage both workflows and connect them through stack dependencies.

### Technologies Used

| Technology | Purpose |
|---|---|
| AWS EC2 | Cloud infrastructure |
| AWS IAM | Access management |
| Terraform | Infrastructure provisioning |
| Ansible | Configuration management |
| Spacelift | Workflow orchestration |
| Git | Version control |
| GitHub | Source code management |
| Ubuntu 22.04 | Operating system |

### Project Workflow

1. Store Terraform and Ansible configurations in GitHub.
2. Connect GitHub to Spacelift.
3. Configure AWS integration.
4. Create EC2 instances using Terraform.
5. Share Terraform outputs with the Ansible stack.
6. Generate the Ansible inventory dynamically.
7. Connect to EC2 instances using SSH key-based authentication.
8. Install `htop` using an Ansible playbook.
9. Increase the Terraform instance count from three to four.

---

## 2. Project Architecture

```text
                    GitHub Repository
                           |
                           |
                    Spacelift Platform
                           |
              +------------+------------+
              |                         |
              v                         |
       Terraform Stack                  |
              |                         |
       AWS Integration                  |
              |                         |
              v                         |
       AWS EC2 Instances                 |
              |                         |
       Terraform Outputs                 |
       instance_public_ips              |
              |                         |
              v                         |
        Ansible Stack <-----------------+
              |
       Dynamic Inventory
              |
       SSH Authentication
              |
              v
       EC2 Instances
              |
       Install htop
```

---

## 3. Prerequisites

Before starting this project, make sure you have:

- An AWS account.
- A GitHub account.
- A Spacelift account.
- An Ubuntu 22.04 EC2 instance to use as a working machine.
- Basic knowledge of Linux, Git, Terraform and Ansible.

We used the following working machine packages:

- Git
- OpenSSH Client
- Curl
- Unzip

### Install the required packages

Connect to your Ubuntu working EC2 instance and run:

```bash
sudo apt update

sudo apt install git openssh-client curl unzip -y
```

### Clone the GitHub repository

```bash
git clone https://github.com/Yuvii2102/My-Complete-DevOps-Projects.git
```

Navigate to the project directory:

```bash
cd My-Complete-DevOps-Projects
```

---

## 4. Create the Project Directory Structure

Create the project folder:

```bash
mkdir -p 19-infrastructure-orchestration/TF

mkdir -p 19-infrastructure-orchestration/Ansible
```

The final project structure is:

```text
19-infrastructure-orchestration/
│
├── TF/
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── id_rsa.pub
│
├── Ansible/
│   ├── install_htop.yaml
│   └── inventory.ini
│
└── README.md
```

---

# Part 1: SSH Key Configuration

## 5. Generate an SSH Key Pair

We generated an SSH key pair on the Ubuntu working EC2 instance.

Run:

```bash
ssh-keygen -t rsa -b 4096
```

Press Enter to accept the default file location.

For this lab, leave the passphrase empty.

This creates two files:

| File | Purpose |
|---|---|
| `~/.ssh/id_rsa` | Private key |
| `~/.ssh/id_rsa.pub` | Public key |

**Important:** The private key must never be uploaded to GitHub.

### Copy the public key into the Terraform directory

Navigate to the project directory:

```bash
cd ~/My-Complete-DevOps-Projects/19-infrastructure-orchestration
```

Copy the public key:

```bash
cp ~/.ssh/id_rsa.pub TF/id_rsa.pub
```

The public key will be used by Terraform to create an AWS key pair.

The private key will be used by Ansible to connect to the EC2 instances.

---

# Part 2: Terraform Configuration

## 6. Create the Terraform Configuration Files

Navigate to the Terraform directory:

```bash
cd ~/My-Complete-DevOps-Projects/19-infrastructure-orchestration/TF
```

We created three Terraform files:

- `main.tf`
- `variables.tf`
- `outputs.tf`

### 6.1 main.tf

Open the file:

```bash
vim main.tf
```

Add the following configuration:

```hcl
provider "aws" {
  region = "us-east-1"
}

# Get the latest Ubuntu 22.04 AMI
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }
}

# Create SSH key pair
resource "aws_key_pair" "spacelift_key" {
  key_name   = "spacelift-key"
  public_key = file("${path.module}/id_rsa.pub")
}

# Create security group
resource "aws_security_group" "spacelift_sg" {
  name = "spacelift-security-group"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Create EC2 instances
resource "aws_instance" "web" {
  count         = var.instance_count
  ami           = data.aws_ami.ubuntu.id
  instance_type = var.instance_type

  key_name               = aws_key_pair.spacelift_key.key_name
  vpc_security_group_ids = [aws_security_group.spacelift_sg.id]

  tags = {
    Name = "Spacelift-Server-${count.index + 1}"
  }
}
```

**What this configuration does:**

1. Configures the AWS provider in `us-east-1`.
2. Retrieves the latest Ubuntu 22.04 AMI.
3. Creates an AWS key pair using our public key.
4. Creates a security group allowing SSH access.
5. Creates EC2 instances using the configured instance count.
6. Assigns a unique name to each EC2 instance.

Note: This is a lab security group configuration. For production, restrict SSH access to trusted IP addresses instead of allowing `0.0.0.0/0`.

Save the file:

```text
Esc
:wq
```

### 6.2 variables.tf

Open the file:

```bash
vim variables.tf
```

Add:

```hcl
# Input variables for Terraform configuration

variable "instance_type" {
  description = "EC2 instance type"
  default     = "t3.micro"
}

variable "instance_count" {
  description = "Number of EC2 instances"
  default     = 3
}
```

**Explanation:**

- `instance_type` defines the EC2 instance type.
- `instance_count` defines how many EC2 instances Terraform should create.

We initially configured three instances.

Save the file:

```text
Esc
:wq
```

### 6.3 outputs.tf

Open the file:

```bash
vim outputs.tf
```

Add:

```hcl
output "instance_public_ips" {
  description = "Public IP addresses of EC2 instances"
  value       = aws_instance.web[*].public_ip
}

output "instance_ids" {
  description = "IDs of EC2 instances"
  value       = aws_instance.web[*].id
}
```

**Explanation:**

- `instance_public_ips` provides the public IP addresses of all created EC2 instances.
- `instance_ids` provides the instance IDs.

These outputs are important because we will share the public IP addresses with the Ansible stack.

Save the file:

```text
Esc
:wq
```

---

# Part 3: Ansible Configuration

## 7. Create the Ansible Playbook

Navigate to the Ansible directory:

```bash
cd ~/My-Complete-DevOps-Projects/19-infrastructure-orchestration/Ansible
```

Create the playbook:

```bash
vim install_htop.yaml
```

Add:

```yaml
---
- name: Install htop on EC2 instances
  hosts: all
  become: true

  tasks:
    - name: Update package list
      ansible.builtin.apt:
        update_cache: yes

    - name: Install htop
      ansible.builtin.apt:
        name: htop
        state: present
```

**Explanation:**

| Configuration | Purpose |
|---|---|
| `hosts: all` | Runs tasks on all inventory hosts |
| `become: true` | Executes tasks with elevated privileges |
| `update_cache: yes` | Updates the APT package list |
| `name: htop` | Specifies the package |
| `state: present` | Ensures the package is installed |

Save the file:

```text
Esc
:wq
```

## 8. Create the Ansible Inventory File

Create the inventory file:

```bash
vim inventory.ini
```

Add:

```ini
# Ansible inventory will be generated during the Spacelift run
```

We will generate the actual inventory dynamically using Terraform outputs.

Save the file:

```text
Esc
:wq
```

---

# Part 4: Push the Project to GitHub

## 9. Commit and Push the Configuration Files

Navigate to the repository root:

```bash
cd ~/My-Complete-DevOps-Projects
```

Check the files:

```bash
git status
```

Add the project files:

```bash
git add .
```

Commit:

```bash
git commit -m "Add Terraform and Ansible infrastructure orchestration project"
```

Push to GitHub:

```bash
git push origin main
```

The Terraform and Ansible configuration files are now available in GitHub.

---

# Part 5: AWS and Spacelift Integration

## 10. Create an AWS IAM Role

Open the AWS Management Console.

Navigate to:

**IAM → Roles → Create role**

Create an IAM role for Spacelift.

Configure the trust relationship using the account information and external ID provided by your Spacelift AWS integration.

The role used in our project was:

```text
space
```

Attach the required EC2 permissions to the role.

For our lab, we used:

```text
AmazonEC2FullAccess
```

This is a broad lab permission. For production environments, create a least-privilege policy limited to the required resources and actions.

### Create the AWS Integration in Spacelift

Open Spacelift.

Navigate to:

**Integrations → AWS**

Create an AWS integration.

Configure:

| Setting | Value |
|---|---|
| Name | spacelift-aws-integration |
| Region | us-east-1 |
| IAM Role | ARN of the AWS IAM role |
| Space | root/dev-console |

Attach this integration to both Terraform and Ansible stacks as required.

The Terraform stack requires AWS read and write permissions for infrastructure provisioning.

---

## 11. Create a Spacelift Space

In Spacelift:

1. Open Spaces.
2. Create a new space.
3. Name it `dev-console`.
4. Use this space for the project stacks and context.

## 12. Connect GitHub to Spacelift

Install the Spacelift GitHub application.

Give it access to the repository:

```text
My-Complete-DevOps-Projects
```

This allows Spacelift to detect repository changes and start runs.

---

# Part 6: Create the Terraform Stack

## 13. Create the Terraform Stack

In Spacelift:

1. Open Stacks.
2. Click Create Stack.
3. Select Terraform as the vendor.
4. Connect the GitHub repository.

Configure:

| Setting | Value |
|---|---|
| Stack name | terraform-infrastructure |
| Space | dev-console |
| Repository | My-Complete-DevOps-Projects |
| Branch | main |
| Project root | 19-infrastructure-orchestration/TF |
| Terraform version | 1.5.7 |
| Manage state | Enabled |

Attach the AWS integration:

```text
spacelift-aws-integration
```

Select the appropriate read and write permissions.

Save the stack configuration.

### Run Terraform

Open the Terraform stack and trigger a run.

Spacelift will:

1. Download the GitHub repository.
2. Initialize Terraform.
3. Read the Ubuntu AMI.
4. Create the key pair.
5. Create the security group.
6. Create three EC2 instances.

Review the Terraform plan and confirm the run.

### Expected Result

Terraform should provision three EC2 instances.

The outputs should include:

```text
instance_ids
instance_public_ips
```

The actual public IP addresses are generated by AWS during provisioning.

---

# Part 7: Configure SSH Authentication in Spacelift

## 14. Create a Spacelift Context

We need to provide the Ansible stack with the private SSH key.

Open Spacelift:

**Share code & config → Contexts → Create Context**

Create:

| Setting | Value |
|---|---|
| Context name | ansible-key |
| Space | dev-console |

### Add the private key as a mounted file

Upload the private key generated earlier:

```text
~/.ssh/id_rsa
```

Configure the mounted file:

| Setting | Value |
|---|---|
| Mount path | /mnt/workspace/id_rsa |
| Secret | Enabled |
| Description | Private SSH key for Ansible EC2 authentication |

Attach this context to the Ansible stack.

**Important:** Never upload `id_rsa` to GitHub. Only the public key `id_rsa.pub` belongs in the project repository.

---

# Part 8: Create the Ansible Stack

## 15. Create the Ansible Stack

In Spacelift:

1. Open Stacks.
2. Click Create Stack.
3. Select Ansible as the vendor.
4. Connect the same GitHub repository.

Configure:

| Setting | Value |
|---|---|
| Stack name | ansible-configuration |
| Space | dev-console |
| Repository | My-Complete-DevOps-Projects |
| Branch | main |
| Project root | 19-infrastructure-orchestration/Ansible |
| Playbook | install_htop.yaml |

Attach:

- The `ansible-key` context.
- The AWS integration, if required by the configured stack.

## 16. Configure Ansible Environment Variables

Open:

**ansible-configuration → Settings → Environment**

Add the following environment variables:

| Variable | Value |
|---|---|
| `ANSIBLE_INVENTORY` | `/mnt/workspace/inventory.ini` |
| `ANSIBLE_PRIVATE_KEY_FILE` | `/mnt/workspace/id_rsa` |
| `ANSIBLE_REMOTE_USER` | `ubuntu` |

These variables tell Ansible where to find the inventory, which SSH private key to use and which remote user to connect with.

---

# Part 9: Generate the Inventory Dynamically

## 17. Configure Spacelift Hooks

We need to generate the Ansible inventory using the Terraform output.

Open:

**ansible-configuration → Settings → Hooks**

Add the following commands as Before hooks for the Planning and Applying phases.

### Hook 1: Set SSH key permissions

```bash
chmod 600 /mnt/workspace/id_rsa
```

This ensures that the SSH private key has the correct permissions.

### Hook 2: Generate the Ansible inventory

```bash
printf '[all]\n' > /mnt/workspace/inventory.ini && python3 -c 'import os,json; ips=json.loads(os.environ["instances"]); f=open("/mnt/workspace/inventory.ini","a"); [f.write(f"{ip} ansible_user=ubuntu\n") for ip in ips]; f.close()'
```

**Explanation:**

- `instances` contains the Terraform public IP output passed through the stack dependency.
- Python reads the IP addresses.
- The inventory file is created automatically.
- Every IP is added under the `[all]` group.
- The remote user is set to `ubuntu`.

For three instances, the generated inventory will look like:

```ini
[all]
INSTANCE_PUBLIC_IP_1 ansible_user=ubuntu
INSTANCE_PUBLIC_IP_2 ansible_user=ubuntu
INSTANCE_PUBLIC_IP_3 ansible_user=ubuntu
```

The actual IP addresses are supplied automatically by Terraform.

---

# Part 10: Connect Terraform and Ansible Using Dependencies

## 18. Configure the Stack Dependency

This is an important part of the project.

We want the Ansible stack to receive the Terraform outputs.

Open:

**ansible-configuration → Settings → Dependencies**

Add the following dependency:

| Setting | Value |
|---|---|
| Dependency stack | terraform-infrastructure |
| Input name | instances |
| Output name | instance_public_ips |

This creates the relationship:

```text
terraform-infrastructure
          |
          |
  instance_public_ips
          |
          v
    instances
          |
          v
 ansible-configuration
```

Terraform provides the public IP addresses.

Ansible uses those addresses to generate its inventory and configure the EC2 instances.

Save the dependency configuration.

---

# Part 11: Execute Ansible

## 19. Trigger the Ansible Run

Open:

```text
ansible-configuration
```

Go to Runs and trigger a run.

During the Planning phase, Spacelift executes Ansible in check mode.

Check the output to ensure:

1. SSH connections are successful.
2. Ansible gathers facts from the EC2 instances.
3. The package update task runs successfully.
4. The htop installation task is successful.

After reviewing the plan, confirm the run.

Spacelift will execute the Ansible playbook during the Applying phase.

### Expected Result

The Ansible playbook should complete without unreachable hosts or failed tasks.

Our original successful execution confirmed that all three original EC2 instances were reachable and that `htop` was installed.

Example recap:

```text
PLAY RECAP

INSTANCE_1 : ok=3 changed=1 unreachable=0 failed=0
INSTANCE_2 : ok=3 changed=1 unreachable=0 failed=0
INSTANCE_3 : ok=3 changed=1 unreachable=0 failed=0
```

---

# Part 12: Increase the EC2 Instance Count

## 20. Update the Terraform Variable

After successfully provisioning and configuring the original three instances, we demonstrated how to scale the infrastructure.

Navigate to the Terraform directory:

```bash
cd ~/My-Complete-DevOps-Projects/19-infrastructure-orchestration/TF
```

Open:

```bash
vim variables.tf
```

Change:

```hcl
variable "instance_count" {
  description = "Number of EC2 instances"
  default     = 3
}
```

To:

```hcl
variable "instance_count" {
  description = "Number of EC2 instances"
  default     = 4
}
```

Save the file:

```text
Esc
:wq
```

### Push the change to GitHub

Navigate to the repository root:

```bash
cd ~/My-Complete-DevOps-Projects
```

Run:

```bash
git add .
```

Commit:

```bash
git commit -m "Increase EC2 instance count to four"
```

Push:

```bash
git push origin main
```

## 21. Apply the Terraform Changes Through Spacelift

Open:

```text
terraform-infrastructure
```

Go to Runs.

Spacelift detects the GitHub change and creates a new Terraform run.

The expected plan is:

```text
Plan: 1 to add, 0 to change, 0 to destroy.
```

This indicates that Terraform will create one additional EC2 instance without replacing the original three.

Review the plan and confirm the run.

### Expected Result

Terraform successfully provisions four EC2 instances.

The final output contains:

```text
instance_ids = [
  "INSTANCE_ID_1",
  "INSTANCE_ID_2",
  "INSTANCE_ID_3",
  "INSTANCE_ID_4"
]

instance_public_ips = [
  "INSTANCE_PUBLIC_IP_1",
  "INSTANCE_PUBLIC_IP_2",
  "INSTANCE_PUBLIC_IP_3",
  "INSTANCE_PUBLIC_IP_4"
]
```

The fourth instance is named:

```text
Spacelift-Server-4
```

This demonstrates how Terraform can scale infrastructure by changing a single input variable.

---

# Part 13: Final Project Results

## 22. What We Achieved

### Terraform

- Configured the AWS provider.
- Retrieved the Ubuntu 22.04 AMI.
- Created an AWS key pair.
- Created a security group.
- Provisioned EC2 instances.
- Used Terraform variables to control the instance count.
- Exposed instance IDs and public IP addresses through outputs.
- Demonstrated scaling from three to four EC2 instances.

### Ansible

- Created an Ansible playbook.
- Configured SSH key-based authentication.
- Used a dynamic inventory generated from Terraform outputs.
- Updated the Ubuntu package list.
- Installed and verified `htop` on the three original EC2 instances.

### Spacelift

- Connected GitHub to Spacelift.
- Configured AWS integration.
- Created separate Terraform and Ansible stacks.
- Configured a mounted-file context for SSH authentication.
- Configured environment variables and hooks.
- Connected Terraform and Ansible using stack dependencies.
- Managed provisioning and configuration runs through Spacelift.

### Final Outcome

| Task | Result |
|---|---|
| AWS integration | Completed |
| Terraform stack | Completed |
| Three initial EC2 instances | Provisioned |
| Ansible stack | Completed |
| SSH authentication | Successful |
| htop installation on original three instances | Successful |
| Terraform and Ansible dependency | Configured |
| Scaling from three to four instances | Completed |
| Fourth EC2 instance provisioning | Successful |

The Terraform provisioning of four EC2 instances was successful. The Ansible Apply logs confirmed configuration of the original three instances.

---

# Part 14: Further Verification of the Fourth Instance

The fourth EC2 instance was successfully provisioned through Terraform. To validate Ansible configuration on all four instances, follow these steps when continuing the practical.

### Step 1: Open the Ansible stack

Go to:

```text
Spacelift → ansible-configuration
```

Open Runs.

### Step 2: Check the dependency

Open:

```text
Settings → Dependencies
```

Verify that:

```text
Dependency: terraform-infrastructure
Input: instances
Output: instance_public_ips
```

### Step 3: Trigger a fresh Ansible run

Start a new Ansible run after the latest Terraform run has successfully finished.

### Step 4: Check the generated inventory

During the Planning phase, confirm that the inventory includes all four current public IP addresses.

For the four-instance demonstration, the addresses returned by Terraform were:

```text
107.21.68.47
18.234.36.227
54.235.36.152
44.223.25.143
```

### Step 5: Review the Planning output

Confirm that Ansible can connect to all four hosts and that the tasks complete successfully in check mode.

### Step 6: Confirm and Apply

After verifying all four hosts in the Planning logs, confirm the Ansible run.

### Step 7: Verify the Apply output

Check that all four hosts appear in the final Ansible recap with:

```text
unreachable=0
failed=0
```

This completes the additional fourth-instance Ansible verification.

---

# Part 15: Cleanup

## 23. Destroy the Terraform-Managed AWS Resources

After finishing the project, clean up the AWS resources to avoid unnecessary charges.

Open:

```text
Spacelift → terraform-infrastructure
```

Use the Terraform stack's Destroy workflow.

Review the destroy plan carefully before confirming it.

Terraform should remove the resources managed by this stack, including:

- The four EC2 instances.
- The security group.
- The AWS key pair.

**Important:** Destroy only the resources managed by this Terraform stack. Do not terminate unrelated EC2 instances in your AWS account.

After the destroy run finishes, check the AWS EC2 console to verify that the project instances have been terminated.

---

# 24. Official Documentation and References

The following official documentation was used as a reference for the commands, configuration and concepts in this project.

### Terraform

1. [Terraform Documentation](https://developer.hashicorp.com/terraform/docs)
2. [Terraform Input Variables](https://developer.hashicorp.com/terraform/language/values/variables)
3. [Terraform Output Values](https://developer.hashicorp.com/terraform/language/values/outputs)
4. [Terraform Data Sources](https://developer.hashicorp.com/terraform/language/data-sources)
5. [AWS Provider Documentation](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
6. [AWS EC2 Instance Resource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance)
7. [AWS Key Pair Resource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/key_pair)
8. [AWS Security Group Resource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group)

### Ansible

1. [Ansible Documentation](https://docs.ansible.com/)
2. [Ansible Inventory Guide](https://docs.ansible.com/projects/ansible/latest/inventory_guide/intro_inventory.html)
3. [Ansible APT Module](https://docs.ansible.com/projects/ansible/latest/collections/ansible/builtin/apt_module.html)
4. [Ansible Playbooks](https://docs.ansible.com/projects/ansible/latest/playbook_guide/playbooks_intro.html)
5. [Ansible SSH Connection](https://docs.ansible.com/projects/ansible/latest/inventory_guide/connection_details.html)

### AWS

1. [Amazon EC2 Documentation](https://docs.aws.amazon.com/ec2/)
2. [AWS IAM Documentation](https://docs.aws.amazon.com/iam/)
3. [EC2 Security Groups](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-security-groups.html)
4. [EC2 Key Pairs](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html)

### Spacelift

1. [Spacelift Documentation](https://docs.spacelift.io/)
2. [Creating Spacelift Stacks](https://docs.spacelift.io/concepts/stack/creating-a-stack)
3. [Spacelift Stack Settings and Hooks](https://docs.spacelift.io/concepts/stack/stack-settings)
4. [Spacelift Contexts](https://docs.spacelift.io/concepts/configuration/context.html)
5. [Spacelift Stack Dependencies](https://docs.spacelift.io/concepts/stack/stack-dependencies)

### Git and GitHub

1. [Git Documentation](https://git-scm.com/doc)
2. [GitHub Documentation](https://docs.github.com/)

---

## 25. Project Repository

GitHub Repository:

[My-Complete-DevOps-Projects](https://github.com/Yuvii2102/My-Complete-DevOps-Projects)

Project Directory:

[19-infrastructure-orchestration](https://github.com/Yuvii2102/My-Complete-DevOps-Projects/tree/main/19-infrastructure-orchestration)

Terraform Directory:

[TF](https://github.com/Yuvii2102/My-Complete-DevOps-Projects/tree/main/19-infrastructure-orchestration/TF)

Ansible Directory:

[Ansible](https://github.com/Yuvii2102/My-Complete-DevOps-Projects/tree/main/19-infrastructure-orchestration/Ansible)

---

**End of Project Documentation**
