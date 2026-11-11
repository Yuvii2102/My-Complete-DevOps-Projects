# Infrastructure Orchestration Using Terraform, Ansible and Spacelift

## 1. Project Overview

In this project, I used Terraform, Ansible and Spacelift to automate infrastructure provisioning and configuration management on AWS.

The main goal was to create EC2 instances using Terraform and configure them using Ansible. Spacelift was used to connect and manage both workflows.

### Tools and Technologies

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

## 2. Project Architecture

```text
                 GitHub Repository
                        |
                        v
                 Spacelift Platform
                        |
              +---------+---------+
              |                   |
              v                   |
        Terraform Stack           |
              |                   |
              v                   |
        AWS Integration           |
              |                   |
              v                   |
        AWS EC2 Instances         |
              |                   |
              v                   |
       Terraform Outputs          |
      instance_public_ips         |
              |                   |
              v                   |
         Ansible Stack <----------+
              |
              v
       Dynamic Inventory
              |
              v
       SSH Authentication
              |
              v
        EC2 Instances
              |
              v
          Install htop
```

## 3. GitHub Repository Setup

We created a separate folder for this project inside our existing GitHub repository.

### Project Structure

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

We connected the GitHub repository to Spacelift using the GitHub integration.

## 4. AWS IAM and Spacelift Integration

First, we created an IAM role in AWS to allow Spacelift to access and manage AWS resources.

### Steps

1. Created an IAM role named `space`.
2. Configured the trust relationship.
3. Attached the required EC2 permissions.
4. Created an AWS integration in Spacelift.
5. Connected the AWS integration to the Terraform and Ansible stacks.

The AWS region used in this project was:

```text
us-east-1
```

For this lab, we used the `AmazonEC2FullAccess` policy. In production, a restricted least-privilege policy should be used.

## 5. SSH Key Configuration

We generated an SSH key pair on our Ubuntu working EC2 instance.

### Generate the SSH key

```bash
ssh-keygen -t rsa -b 4096
```

This created two files:

| File | Purpose |
|---|---|
| `id_rsa` | Private key |
| `id_rsa.pub` | Public key |

We copied the public key into the Terraform folder:

```bash
cp ~/.ssh/id_rsa.pub TF/id_rsa.pub
```

The public key was used by Terraform to create an AWS key pair.

The private key was uploaded to a Spacelift Context as a secret mounted file.

Mounted file path:

```text
/mnt/workspace/id_rsa
```

**Note:** The private key must never be uploaded to GitHub.

## 6. Terraform Configuration

We created a Terraform stack named:

```text
terraform-infrastructure
```

The Terraform configuration was divided into three files.

### 6.1 main.tf

This file contains the AWS provider and infrastructure resources.

It performs the following tasks:

1. Connects to AWS in the `us-east-1` region.
2. Finds the latest Ubuntu 22.04 AMI.
3. Creates an AWS key pair.
4. Creates a security group.
5. Creates EC2 instances using the configured instance count.

### 6.2 variables.tf

This file contains the input variables.

Our initial configuration was:

```hcl
variable "instance_type" {
  description = "EC2 instance type"
  default     = "t3.micro"
}

variable "instance_count" {
  description = "Number of EC2 instances"
  default     = 3
}
```

We initially configured Terraform to create three EC2 instances.

### 6.3 outputs.tf

This file contains the outputs generated after Terraform provisions the infrastructure.

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

The two outputs provide:

- Public IP addresses of the EC2 instances.
- EC2 instance IDs.

We later used `instance_public_ips` to connect Terraform with Ansible.

### 6.4 Terraform Stack Configuration

We created the Terraform stack in Spacelift with these settings:

| Setting | Value |
|---|---|
| Stack name | terraform-infrastructure |
| Repository | My-Complete-DevOps-Projects |
| Branch | main |
| Project root | 19-infrastructure-orchestration/TF |
| Terraform version | 1.5.7 |
| Manage state | Enabled |

We attached the AWS integration to the stack.

### 6.5 Provision Three EC2 Instances

After configuring the Terraform stack, we triggered a run in Spacelift.

Terraform successfully created:

- Three Ubuntu EC2 instances.
- One AWS key pair.
- One security group.

The public IP addresses were generated by AWS and displayed in Terraform outputs.

**Result:** Three EC2 instances were successfully provisioned.

## 7. Ansible Configuration

Next, we created a separate Spacelift stack named:

```text
ansible-configuration
```

The purpose of this stack was to configure the EC2 instances created by Terraform.

### 7.1 Create the Ansible Playbook

We created a file named:

```text
install_htop.yaml
```

The playbook was used to update the package list and install `htop`.

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

### Explanation

| Configuration | Purpose |
|---|---|
| `hosts: all` | Runs tasks on all inventory hosts |
| `become: true` | Runs tasks with administrator privileges |
| `update_cache: yes` | Updates the package list |
| `name: htop` | Specifies the package |
| `state: present` | Ensures htop is installed |

### 7.2 Create the Inventory File

We created:

```text
inventory.ini
```

Initially, it contained a placeholder comment:

```ini
# Ansible inventory will be generated during the Spacelift run
```

The actual inventory was generated automatically using Terraform outputs.

## 8. Configure SSH Authentication in Spacelift

We created a Spacelift Context named:

```text
ansible-key
```

We uploaded our private SSH key as a mounted file.

Configuration:

| Setting | Value |
|---|---|
| Context name | ansible-key |
| Mounted file | /mnt/workspace/id_rsa |
| Secret | Enabled |

We attached this context to the Ansible stack.

### Environment Variables

We configured the following environment variables in the Ansible stack:

| Variable | Value |
|---|---|
| `ANSIBLE_INVENTORY` | `/mnt/workspace/inventory.ini` |
| `ANSIBLE_PRIVATE_KEY_FILE` | `/mnt/workspace/id_rsa` |
| `ANSIBLE_REMOTE_USER` | `ubuntu` |

These variables tell Ansible where to find the inventory, which SSH key to use and which remote user to connect with.

## 9. Dynamic Inventory Using Spacelift Hooks

We configured Before hooks in the Planning and Applying phases.

These hooks prepare the SSH key and generate the Ansible inventory.

### Hook 1: Set SSH Key Permissions

```bash
chmod 600 /mnt/workspace/id_rsa
```

This gives the private key the correct permissions.

### Hook 2: Generate Ansible Inventory

```bash
printf '[all]\n' > /mnt/workspace/inventory.ini && python3 -c 'import os,json; ips=json.loads(os.environ["instances"]); f=open("/mnt/workspace/inventory.ini","a"); [f.write(f"{ip} ansible_user=ubuntu\n") for ip in ips]; f.close()'
```

### How It Works

1. Terraform generates the EC2 public IP addresses.
2. Spacelift passes the output to the Ansible stack.
3. The input is available through the environment variable `instances`.
4. Python reads the IP addresses.
5. The inventory file is generated automatically.
6. Ansible uses the inventory to connect to the EC2 instances.

For example, the generated inventory for four instances looks like this:

```ini
[all]
INSTANCE_PUBLIC_IP_1 ansible_user=ubuntu
INSTANCE_PUBLIC_IP_2 ansible_user=ubuntu
INSTANCE_PUBLIC_IP_3 ansible_user=ubuntu
INSTANCE_PUBLIC_IP_4 ansible_user=ubuntu
```

The actual IP addresses are supplied automatically by Terraform.

## 10. Connect Terraform and Ansible Using Dependencies

This was one of the most important parts of the project.

We connected the Terraform stack with the Ansible stack using Spacelift stack dependencies.

### Dependency Configuration

We opened:

```text
ansible-configuration → Settings → Dependencies
```

We configured the following mapping:

| Setting | Value |
|---|---|
| Dependency stack | terraform-infrastructure |
| Input name | instances |
| Output name | instance_public_ips |

### Workflow

```text
terraform-infrastructure
          |
          |
          v
  instance_public_ips
          |
          v
       instances
          |
          v
  ansible-configuration
          |
          v
    Dynamic Inventory
          |
          v
    Install htop
```

This allows the Ansible stack to receive the EC2 public IP addresses directly from Terraform.

## 11. Execute the Ansible Playbook

After configuring the stack, contexts, environment variables, hooks and dependencies, we triggered an Ansible run.

Spacelift first executed the playbook in check mode during the Planning phase.

After reviewing the plan, we confirmed the run to execute the playbook.

### Original Three-Instance Execution

Ansible successfully connected to all three original EC2 instances.

The final recap showed:

```text
PLAY RECAP

174.129.138.209 : ok=3 changed=1 unreachable=0 failed=0
54.90.220.102   : ok=3 changed=1 unreachable=0 failed=0
98.94.95.76     : ok=3 changed=1 unreachable=0 failed=0
```

**Result:**

- SSH connections were successful.
- Ansible gathered facts from all three instances.
- Package lists were updated.
- `htop` was installed or confirmed present.

All three original instances were successfully configured.

## 12. Scale EC2 Instances from Three to Four

After completing the original practical, we demonstrated how to increase the infrastructure using Terraform.

### 12.1 Update variables.tf

We opened the Terraform variables file:

```bash
cd ~/My-Complete-DevOps-Projects/19-infrastructure-orchestration/TF

vim variables.tf
```

Initially, the instance count was:

```hcl
default = 3
```

We changed it to:

```hcl
default = 4
```

The final variable configuration became:

```hcl
variable "instance_type" {
  description = "EC2 instance type"
  default     = "t3.micro"
}

variable "instance_count" {
  description = "Number of EC2 instances"
  default     = 4
}
```

Save the file using:

```text
Esc
:wq
```

### 12.2 Push the Changes to GitHub

Navigate to the repository root:

```bash
cd ~/My-Complete-DevOps-Projects
```

Add the changes:

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

### 12.3 Run Terraform Through Spacelift

We opened the `terraform-infrastructure` stack.

Spacelift detected the GitHub change and generated a Terraform plan.

The plan showed:

```text
Plan: 1 to add, 0 to change, 0 to destroy.
```

This meant Terraform would create one additional EC2 instance without changing the original three.

We confirmed the run.

### 12.4 Final Terraform Result

Terraform successfully provisioned four EC2 instances.

The final output showed:

```text
Apply complete!
Resources: 4 added, 0 changed, 0 destroyed.
```

The four EC2 instance public IP addresses were:

| Instance | Public IP |
|---|---|
| Spacelift-Server-1 | 107.21.68.47 |
| Spacelift-Server-2 | 18.234.36.227 |
| Spacelift-Server-3 | 54.235.36.152 |
| Spacelift-Server-4 | 44.223.25.143 |

**Result:** Four EC2 instances were successfully provisioned using Terraform.

## 13. Ansible Verification on All Four Instances

After provisioning the fourth EC2 instance, we started another Ansible run through Spacelift.

The dynamically generated inventory included all four EC2 public IP addresses.

### Planning Results

Ansible successfully gathered facts from all four instances.

| EC2 Public IP | Gathering Facts |
|---|---|
| 107.21.68.47 | Successful |
| 18.234.36.227 | Successful |
| 54.235.36.152 | Successful |
| 44.223.25.143 | Successful |

The `Update package list` and `Install htop` tasks also completed successfully in the Planning check mode for all four hosts.

The Planning recap showed:

```text
PLAY RECAP

107.21.68.47  : ok=3 changed=1 unreachable=0 failed=0
18.234.36.227 : ok=3 changed=1 unreachable=0 failed=0
44.223.25.143 : ok=3 changed=1 unreachable=0 failed=0
54.235.36.152 : ok=3 changed=1 unreachable=0 failed=0
```

This confirmed that all four hosts were reachable and the playbook passed its check-mode run.

The final Applying phase is the next step to confirm that the changes have been applied to all four instances.

## 14. What We Achieved

| Area | Achievement |
|---|---|
| AWS | Configured IAM and EC2 |
| Terraform | Provisioned four EC2 instances |
| Ansible | Successfully configured the original three instances |
| SSH | Configured key-based authentication |
| Spacelift | Created and connected two stacks |
| Dependencies | Passed Terraform outputs to Ansible |
| Hooks | Generated Ansible inventory dynamically |
| GitHub | Connected the repository to Spacelift |
| Scaling | Increased EC2 instance count from three to four |
| Ansible Planning | Successfully detected all four instances |

### Key Learnings

- Learned how to provision AWS infrastructure using Terraform.
- Understood how Terraform variables control infrastructure.
- Learned how to use Terraform outputs to share information.
- Practised SSH key-based authentication.
- Learned how to configure EC2 instances using Ansible.
- Understood dynamic inventory generation.
- Learned how to connect Terraform and Ansible through Spacelift dependencies.
- Practised using Spacelift contexts, environment variables and hooks.
- Demonstrated infrastructure scaling from three to four EC2 instances.

## 15. Remaining Step: Complete Ansible Apply

The latest Ansible Planning run successfully detected all four EC2 instances and completed its check-mode tasks.

To finish the practical:

1. Open Spacelift.
2. Go to `ansible-configuration`.
3. Open the latest run named `Increase EC2 instance count to four`.
4. Confirm the run.
5. Wait for the Applying phase to finish.
6. Check the final Ansible recap.
7. Confirm that all four hosts have `unreachable=0` and `failed=0`.

The Applying logs will provide the final confirmation that the playbook was executed on all four instances.

## 16. Cleanup AWS Resources

After completing the practical, remove the AWS resources created by Terraform to avoid unnecessary charges.

### Steps

1. Open Spacelift.
2. Open the `terraform-infrastructure` stack.
3. Use the Terraform Destroy workflow.
4. Review the destroy plan carefully.
5. Confirm the destroy run.
6. Check the AWS EC2 console to verify that the four project instances have been terminated.

**Important:** Only destroy resources managed by this Terraform stack. Do not terminate unrelated EC2 instances, including the existing `t2.xlarge` instance.

---

## 17. Official Documentation and References

The following official documentation links were used as references for the tools and configurations in this project.

### Terraform

- [Terraform Documentation](https://developer.hashicorp.com/terraform/docs)
- [Terraform Input Variables](https://developer.hashicorp.com/terraform/language/values/variables)
- [Terraform Output Values](https://developer.hashicorp.com/terraform/language/values/outputs)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [AWS EC2 Instance Resource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance)
- [AWS Key Pair Resource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/key_pair)
- [AWS Security Group Resource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group)

### Ansible

- [Ansible Documentation](https://docs.ansible.com/)
- [Ansible Inventory Guide](https://docs.ansible.com/projects/ansible/latest/inventory_guide/intro_inventory.html)
- [Ansible Playbooks](https://docs.ansible.com/projects/ansible/latest/playbook_guide/playbooks_intro.html)
- [Ansible APT Module](https://docs.ansible.com/projects/ansible/latest/collections/ansible/builtin/apt_module.html)
- [Ansible SSH Connection](https://docs.ansible.com/projects/ansible/latest/inventory_guide/connection_details.html)

### AWS

- [Amazon EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [AWS IAM Documentation](https://docs.aws.amazon.com/iam/)
- [EC2 Security Groups](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-security-groups.html)
- [EC2 Key Pairs](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html)

### Spacelift

- [Spacelift Documentation](https://docs.spacelift.io/)
- [Creating Spacelift Stacks](https://docs.spacelift.io/concepts/stack/creating-a-stack)
- [Stack Settings](https://docs.spacelift.io/concepts/stack/stack-settings)
- [Spacelift Contexts](https://docs.spacelift.io/concepts/configuration/context.html)
- [Stack Dependencies](https://docs.spacelift.io/concepts/stack/stack-dependencies)

### Git and GitHub

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Documentation](https://docs.github.com/)

---

## 18. Project Repository

**GitHub Repository:**

[My-Complete-DevOps-Projects](https://github.com/Yuvii2102/My-Complete-DevOps-Projects)

**Project Folder:**

[19-infrastructure-orchestration](https://github.com/Yuvii2102/My-Complete-DevOps-Projects/tree/main/19-infrastructure-orchestration)

**Terraform Files:**

[TF Directory](https://github.com/Yuvii2102/My-Complete-DevOps-Projects/tree/main/19-infrastructure-orchestration/TF)

**Ansible Files:**

[Ansible Directory](https://github.com/Yuvii2102/My-Complete-DevOps-Projects/tree/main/19-infrastructure-orchestration/Ansible)

---

**Project:** Infrastructure Orchestration Using Terraform, Ansible and Spacelift

**Main Achievement:** Provisioned four AWS EC2 instances using Terraform and successfully configured the original three using Ansible. Also verified that Ansible can discover and execute its tasks in check mode against all four instances.
