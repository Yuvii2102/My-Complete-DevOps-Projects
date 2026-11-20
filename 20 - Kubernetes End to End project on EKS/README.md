# Complete DevOps Project : Deploying an Application on AWS EKS

## 1. Project Introduction

In this project, I am going to deploy a 2048 Game Application on Amazon EKS (Elastic Kubernetes Service) using Kubernetes.

The main goal of this project is to understand how to deploy and manage containerized applications on AWS using Kubernetes.

I will create an EKS cluster, deploy the application using Kubernetes and expose it to the internet using an AWS Application Load Balancer (ALB).

I will also use AWS Fargate to run application Pods without managing EC2 worker instances.

### What I Will Learn

- How Amazon EKS works.
- How to create and manage an EKS cluster.
- How to run applications using AWS Fargate.
- How to deploy applications using Kubernetes.
- How Kubernetes Services work.
- How Ingress and Ingress Controllers work.
- How to configure AWS IAM and OIDC.
- How to install applications using Helm.
- How to expose applications using an AWS Load Balancer.

---

## 2. What is Amazon EKS?

Amazon EKS stands for **Elastic Kubernetes Service**. It is a managed Kubernetes service provided by AWS.

When we create a Kubernetes cluster by ourselves, we need to install and manage its components, such as the API Server, etcd, Scheduler, Controller Manager and worker nodes.

We also need to configure networking and maintain the cluster. This requires a lot of manual work.

AWS provides Amazon EKS to make this easier. EKS manages the Kubernetes Control Plane for us. We don't have to install or maintain the Control Plane components manually.

**In simple words, Amazon EKS allows us to run Kubernetes on AWS without managing the Control Plane ourselves.**

---

## 3. Understanding Kubernetes Architecture

A Kubernetes cluster mainly has two parts:

1. Control Plane
2. Data Plane (Worker Nodes)

### 3.1 Control Plane

The Control Plane manages the entire Kubernetes cluster. It receives requests, makes decisions and helps ensure that the cluster works as expected.

Its main components are:

| Component | Description |
|---|---|
| API Server | Handles communication with the Kubernetes cluster. |
| etcd | Stores Kubernetes cluster information. |
| Scheduler | Decides which worker node should run a Pod. |
| Controller Manager | Helps the cluster maintain its desired state. |
| Cloud Controller Manager | Connects Kubernetes with cloud providers. |

**In Amazon EKS:** AWS manages the Control Plane for us.

### 3.2 Data Plane (Worker Nodes)

The Data Plane is responsible for running our applications.

Worker nodes provide the resources required to run application containers inside Kubernetes Pods.

For example:

- We create a Deployment.
- Kubernetes creates Pods.
- Pods run our application containers.
- Users access the application through the configured networking resources.

**In Amazon EKS:** We can use EC2 instances or AWS Fargate to run our application workloads.

---

## 4. Worker Node Options in EKS

AWS provides different options to run applications inside an EKS cluster. Two important options are EC2 and Fargate.

### 4.1 EC2 Worker Nodes

EC2 instances can be used as worker nodes in Kubernetes.

We need to manage the EC2 instances and take care of their configuration, scaling and capacity.

### 4.2 AWS Fargate

AWS Fargate is a serverless compute service that allows us to run containers without managing EC2 worker instances.

AWS manages the underlying compute infrastructure.

**Why am I using Fargate in this project?**

I am using Fargate because I don't want to manage worker EC2 instances manually. It allows me to focus on deploying and managing my Kubernetes applications.

| EC2 | Fargate |
|---|---|
| Uses EC2 instances as worker nodes. | Runs containers without managing worker EC2 instances. |
| We manage instance capacity and scaling. | AWS manages the underlying compute infrastructure. |
| Provides more control over worker nodes. | Reduces infrastructure management. |

**Remember:**
- EKS manages the Control Plane.
- Fargate provides compute capacity for running Pods.

---

## 5. Why Do We Use EKS?

Managing Kubernetes manually can be difficult, especially when working with large applications and multiple clusters.

Amazon EKS makes this easier by managing the Control Plane.

### Advantages of Amazon EKS

1. AWS manages the Kubernetes Control Plane.
2. Provides high availability for the Control Plane.
3. Reduces manual maintenance.
4. Integrates with other AWS services.
5. Supports EC2 and Fargate.
6. Helps us run containerized applications at scale.

---

## 6. How Do We Expose an Application Running Inside Kubernetes?

When we deploy an application inside Kubernetes, it runs inside a Pod. However, the application is not automatically accessible from the internet.

We need to configure networking to allow external users to access it.

For this, we use three important Kubernetes concepts:

- Service
- Ingress
- Ingress Controller

### 6.1 What is a Kubernetes Service?

A Kubernetes Service provides a stable way to access Pods.

Pods can be created and deleted dynamically, and their IP addresses can change. Instead of directly accessing Pods, we use a Service to communicate with them.

Kubernetes provides three main Service types.

#### ClusterIP

ClusterIP is the default Service type.

- Allows communication within the Kubernetes cluster.
- Other Pods can access the application.
- External users cannot directly access it.

#### NodePort

NodePort exposes the application through a port on the worker nodes.

- Allows access using the worker node IP and NodePort.
- Uses ports in the range 30000–32767 by default.
- Requires network access to the worker nodes.

#### LoadBalancer

LoadBalancer exposes the application through an external load balancer.

- Allows external users to access the application.
- In AWS, it can create an AWS Load Balancer.
- Creating a separate load balancer for every application can increase costs.

### 6.2 What is Kubernetes Ingress?

Ingress is a Kubernetes resource used to manage external HTTP and HTTPS access to applications running inside the cluster.

It helps us route incoming traffic to the correct application using rules.

For example, imagine we have two applications:

| URL | Application |
|---|---|
| `example.com/app1` | Application 1 |
| `example.com/app2` | Application 2 |

We can configure Ingress rules to send traffic to the correct Kubernetes Service.

The main advantage is that we can use one load balancer to route traffic to multiple applications instead of creating a separate load balancer for every application.

### 6.3 What is an Ingress Controller?

An Ingress Controller reads Ingress resources and implements their routing rules.

In simple words:

- Ingress defines the routing rules.
- Ingress Controller reads those rules.
- It configures the required routing infrastructure.

Examples of Ingress Controllers include:

- NGINX Ingress Controller
- AWS Load Balancer Controller
- F5 Ingress Controller

**In this project, I am using AWS Load Balancer Controller.**

---

## 7. What is AWS Application Load Balancer (ALB)?

Application Load Balancer is a load balancing service provided by AWS. It works at Layer 7 (Application Layer) of the OSI model.

It distributes incoming HTTP and HTTPS traffic to configured targets.

In this project, ALB acts as the entry point for external users.

When a user opens our application URL, the request reaches the ALB and is forwarded to the application through the configured Kubernetes routing.

### Why Do I Use ALB?

- Provides external access to applications.
- Distributes incoming traffic.
- Supports HTTP and HTTPS.
- Integrates with AWS Load Balancer Controller.

---

## 8. What is OIDC?

OIDC stands for **OpenID Connect**. It is an identity layer built on top of OAuth 2.0.

In our project, the AWS Load Balancer Controller needs permission to communicate with AWS services and create resources such as Application Load Balancers.

But how does a Kubernetes Pod get permission to access AWS services?

This is where OIDC and IAM Roles for Service Accounts (IRSA) come into the picture.

The basic process is:

1. Associate an OIDC identity provider with the EKS cluster.
2. Create an IAM policy with the required permissions.
3. Create an IAM role.
4. Attach the IAM role to a Kubernetes Service Account.
5. Allow the AWS Load Balancer Controller to use that Service Account.

This allows the controller to access AWS services without storing AWS access keys inside the Pod.

---

## 9. What is Helm?

Helm is a package manager for Kubernetes.

Just like we use package managers to install software on Linux, Helm helps us install and manage applications inside Kubernetes.

Helm uses packages called **Charts**.

Instead of manually creating multiple YAML files to install an application, we can use a Helm Chart.

In this project, I will use Helm to install AWS Load Balancer Controller.

### Common Helm Commands

```bash
# Check Helm version
helm version

# Add a Helm repository
helm repo add <repo-name> <repo-url>

# Update Helm repositories
helm repo update

# Install a Helm chart
helm install <release-name> <chart-name>

# List installed releases
helm list -A
```

---

## 10. What is eksctl?

eksctl is a command-line tool used to create and manage Amazon EKS clusters.

Instead of manually creating everything through the AWS Console, we can use eksctl commands.

It helps us automate:

- EKS cluster creation.
- VPC and subnet configuration.
- Fargate profiles.
- Node groups.
- Cluster management.

Example command to create an EKS cluster:

```bash
eksctl create cluster \
  --name=demo-cluster \
  --region=us-east-1 \
  --fargate
```

---

## 11. What is VPC?

VPC stands for **Virtual Private Cloud**.

It is a logically isolated network in AWS where we can launch and manage our cloud resources.

Inside a VPC, we can create public and private subnets.

### Public Subnet

- Contains resources that need connectivity from the internet.
- Can contain internet-facing load balancers.
- Uses a route to an Internet Gateway for internet connectivity.

### Private Subnet

- Contains resources that should not be directly accessible from the internet.
- Can host application workloads.
- Can access the internet through a NAT Gateway when configured.

In our project:

- EKS will use VPC networking.
- Application workloads will run using Fargate.
- The internet-facing ALB will provide external access.

---

## 12. Complete Project Architecture

The main components work together to allow external users to access our application.

### Application Traffic Flow

1. An external user accesses the application through the internet.
2. The request reaches the AWS Application Load Balancer.
3. The ALB forwards traffic according to its configured routing rules.
4. Kubernetes Ingress defines the routing rules for the application.
5. The Kubernetes Service routes traffic to the matching Pods.
6. The application Pods running on AWS Fargate serve the request.

The AWS Load Balancer Controller watches Ingress resources and creates and configures the ALB. It manages the ALB configuration; it is not itself the component through which user traffic passes.

### Traffic Flow Summary

```text
External User
      |
      v
AWS Application Load Balancer (ALB)
      |
      v
Kubernetes Ingress routing
      |
      v
Kubernetes Service
      |
      v
Application Pods on AWS Fargate
      |
      v
2048 Game Application
```

---

## 13. Tools and Technologies Used

| Technology | Purpose |
|---|---|
| AWS EKS | Managed Kubernetes service |
| AWS Fargate | Runs application Pods |
| Kubernetes | Container orchestration |
| kubectl | Communicates with Kubernetes |
| eksctl | Creates and manages EKS clusters |
| AWS CLI | Interacts with AWS through the terminal |
| Helm | Installs Kubernetes applications |
| AWS IAM | Manages permissions and access |
| OIDC | Connects Kubernetes identity with AWS IAM |
| AWS ALB | Provides external application access |

---

## 14. Practical Implementation Steps

I will follow these steps to complete the project.

| Step | Task |
|---|---|
| 1 | Install prerequisites: kubectl, eksctl and AWS CLI |
| 2 | Configure AWS CLI |
| 3 | Create EKS cluster using eksctl |
| 4 | Configure kubectl and update kubeconfig |
| 5 | Create Fargate profile |
| 6 | Deploy the 2048 application using Deployment, Service and Ingress |
| 7 | Configure OIDC provider |
| 8 | Create IAM policy and IAM role |
| 9 | Create Kubernetes Service Account |
| 10 | Install AWS Load Balancer Controller using Helm |
| 11 | Verify Application Load Balancer |
| 12 | Access the 2048 application using the ALB DNS name |

---

## 15. Final Project Understanding

Through this project, I will learn how to deploy a real application on AWS EKS and make it accessible to external users.

I will understand how EKS manages the Kubernetes Control Plane, how Fargate runs application Pods and how Kubernetes resources work together.

I will also learn how to integrate Kubernetes with AWS services using IAM and OIDC.

Finally, I will configure AWS Load Balancer Controller to create an Application Load Balancer and expose my application to the internet.

**My final goal is to successfully deploy and access the 2048 Game Application running inside an AWS EKS cluster.**

---

**Theory Completed**

**Next: Practical Implementation – Installing Prerequisites**

I will now start with AWS CLI, kubectl and eksctl installation. I will follow my teacher's commands and document each practical step with its purpose and output.
