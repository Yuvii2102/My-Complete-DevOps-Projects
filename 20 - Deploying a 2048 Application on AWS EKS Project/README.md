# Complete DevOps Project : Deploying a 2048 Application on AWS EKS ( Practicals )

## 1. Project Overview

In this project, I deployed the 2048 Game Application on Amazon Elastic Kubernetes Service (EKS).

I used AWS Fargate to run the application pods and the AWS Load Balancer Controller to expose the application to the internet through an Application Load Balancer (ALB).

The main goal of this project was to understand how to deploy and expose a Kubernetes application on AWS EKS.

### What I implemented

- Created an Amazon EKS cluster.
- Configured AWS CLI, kubectl and eksctl.
- Created a Fargate profile for the application namespace.
- Deployed the 2048 application using a Kubernetes manifest.
- Configured IAM OIDC integration.
- Created an IAM policy, IAM role and Kubernetes service account.
- Installed AWS Load Balancer Controller using Helm.
- Created an Application Load Balancer through Kubernetes Ingress.
- Accessed the 2048 application using the ALB DNS address.

---

## 2. Prerequisites

Before starting the project, I prepared an Ubuntu EC2 instance and installed the required tools.

### Tools Used

| Tool | Purpose |
|---|---|
| AWS CLI | To interact with AWS services |
| kubectl | To communicate with the Kubernetes cluster |
| eksctl | To create and manage EKS clusters |
| Helm | To install the AWS Load Balancer Controller |
| AWS EKS | Managed Kubernetes service |
| AWS Fargate | To run application containers |
| AWS IAM | To manage permissions and roles |
| Application Load Balancer | To expose the application to the internet |

### 2.1 Install AWS CLI

I installed AWS CLI using the Ubuntu package manager.

```bash
sudo apt update
sudo apt install awscli -y
```

Verify the installation:

```bash
aws --version
```

### 2.2 Configure AWS CLI

I configured AWS CLI using my AWS account credentials.

```bash
aws configure
```

It asks for the following details:

- AWS Access Key ID
- AWS Secret Access Key
- Default region name: `us-east-1`
- Default output format: `json`

I used `us-east-1` as the AWS region throughout this project.

**Important:** I never share my AWS access keys or upload them to GitHub.

### 2.3 Install kubectl

kubectl is a command-line tool used to communicate with Kubernetes clusters.

First, I installed the required packages.

```bash
sudo apt update
sudo apt install -y apt-transport-https ca-certificates curl gpg
```

Created the directory for the Kubernetes repository key.

```bash
sudo mkdir -p -m 755 /etc/apt/keyrings
```

Downloaded and added the Kubernetes repository key.

```bash
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.37/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
```

Added the Kubernetes APT repository.

```bash
echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.37/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list
```

Updated the package list and installed kubectl.

```bash
sudo apt update
sudo apt install -y kubectl
```

Verify the installation:

```bash
kubectl version --client
```

### 2.4 Install eksctl

eksctl is a command-line tool used to create and manage Amazon EKS clusters.

I downloaded the latest Linux AMD64 release.

```bash
ARCH=amd64
PLATFORM=$(uname -s)_$ARCH

curl -sLO "https://github.com/eksctl-io/eksctl/releases/latest/download/eksctl_${PLATFORM}.tar.gz"
```

Extracted the downloaded archive.

```bash
tar -xzf eksctl_${PLATFORM}.tar.gz
```

Installed the binary.

```bash
sudo install -m 0755 eksctl /usr/local/bin/eksctl
```

Verify the installation:

```bash
eksctl version
```

Optional cleanup:

```bash
rm -f eksctl_Linux_amd64.tar.gz
```

---

## 3. Verify AWS Configuration

Before creating the EKS cluster, I checked whether AWS CLI was configured correctly.

### Check AWS identity

```bash
aws sts get-caller-identity
```

This command displays the AWS account and IAM identity being used.

### Check the configured region

```bash
aws configure get region
```

Expected output:

```text
us-east-1
```

---

## 4. Create an EKS Cluster

Amazon EKS is a managed Kubernetes service provided by AWS.

AWS manages the Kubernetes control plane, while the application workloads can run on compute resources such as EC2 worker nodes or Fargate.

For this project, I used AWS Fargate.

### Create the EKS cluster

```bash
eksctl create cluster \
  --name=demo-cluster \
  --region=us-east-1 \
  --fargate
```

### Understanding the command

| Option | Purpose |
|---|---|
| `eksctl create cluster` | Creates an EKS cluster |
| `--name` | Specifies the cluster name |
| `--region` | Specifies the AWS region |
| `--fargate` | Configures Fargate support |

During cluster creation, eksctl provisions the required EKS resources and configures the cluster.

The process takes several minutes. I waited until the cluster was ready.

### Verify the EKS cluster

```bash
eksctl get cluster --region=us-east-1
```

My cluster appeared as:

```text
NAME          REGION      EKSCTL CREATED
demo-cluster  us-east-1   True
```

---

## 5. Connect kubectl to the EKS Cluster

After creating the cluster, I configured kubectl to communicate with it.

### Update kubeconfig

```bash
aws eks update-kubeconfig \
  --name demo-cluster \
  --region us-east-1
```

This command updates the kubeconfig file with the connection details of the EKS cluster.

### Verify Kubernetes nodes

```bash
kubectl get nodes
```

My cluster showed four Fargate-backed Kubernetes nodes in the `Ready` state.

### Check all Kubernetes pods

```bash
kubectl get pods -A
```

The CoreDNS and Metrics Server pods were running successfully.

---

## 6. Create a Fargate Profile for the 2048 Application

A Fargate profile tells Amazon EKS which Kubernetes pods should run on AWS Fargate.

The application manifest uses the namespace `game-2048`, so I created a Fargate profile for that exact namespace.

### Create the Fargate profile

```bash
eksctl create fargateprofile \
  --cluster demo-cluster \
  --region us-east-1 \
  --name alb-sample-app \
  --namespace game-2048
```

### Understanding the command

| Option | Purpose |
|---|---|
| `--cluster` | Specifies the EKS cluster |
| `--region` | Specifies the AWS region |
| `--name` | Names the Fargate profile |
| `--namespace` | Specifies which namespace should use Fargate |

### Verify the Fargate profile

```bash
eksctl get fargateprofile \
  --cluster demo-cluster \
  --region us-east-1
```

Expected result:

```text
NAME             SELECTOR NAMESPACE
alb-sample-app   game-2048
fp-default       default
fp-default       kube-system
```

The `alb-sample-app` profile should show the `ACTIVE` status.

**Important:** The namespace must match the namespace in the Kubernetes manifest. In this project, the correct namespace is `game-2048`, including the hyphen.

If a Fargate profile is being deleted, wait until the deletion finishes before creating another profile with the same name.

---

## 7. Deploy the 2048 Application

After creating the Fargate profile, I deployed the 2048 application using a Kubernetes manifest.

The manifest creates the following Kubernetes resources:

- Namespace
- Deployment
- Service
- Ingress

### Deploy the application

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.5.4/docs/examples/2048/2048_full.yaml
```

### Understanding the command

- `kubectl apply` applies Kubernetes configuration.
- `-f` specifies the YAML manifest file.
- The URL points to the 2048 application manifest.

The manifest creates a namespace named `game-2048` and deploys the application inside it.

The deployment uses five replicas to run multiple application pods.

The Service is configured as a NodePort service, and the Ingress defines how external traffic should reach the application.

---

## 8. Verify the 2048 Application Resources

After deploying the manifest, I verified the Kubernetes resources.

### 8.1 Check application pods

```bash
kubectl get pods -n game-2048
```

Initially, the pods were in the `Pending` state while Fargate was provisioning the required compute resources.

After waiting for a few minutes, all five pods entered the `Running` state.

### 8.2 Check the deployment

```bash
kubectl get deployment -n game-2048
```

The deployment showed five desired replicas and five available replicas.

### 8.3 Check the service

```bash
kubectl get svc -n game-2048
```

The `service-2048` Service was created successfully.

### 8.4 Check the Ingress

```bash
kubectl get ingress -n game-2048
```

The Ingress resource was created, but its address was initially empty because the AWS Load Balancer Controller had not yet been installed.

The controller would create and configure the Application Load Balancer.

---

## 9. Associate IAM OIDC Provider

OIDC stands for OpenID Connect.

It allows Kubernetes service accounts to use IAM roles securely.

The AWS Load Balancer Controller needs AWS permissions to create and manage Application Load Balancers.

For this, I associated an IAM OIDC provider with my EKS cluster.

### Associate the OIDC provider

```bash
eksctl utils associate-iam-oidc-provider \
  --cluster demo-cluster \
  --region us-east-1 \
  --approve
```

### Understanding the command

| Option | Purpose |
|---|---|
| `associate-iam-oidc-provider` | Associates an IAM OIDC provider |
| `--cluster` | Specifies the EKS cluster |
| `--region` | Specifies the AWS region |
| `--approve` | Approves the operation |

The OIDC provider was created successfully.

---

## 10. Download the IAM Policy

The AWS Load Balancer Controller requires permissions to communicate with AWS services and manage load balancers.

I downloaded the IAM policy JSON file from the official AWS Load Balancer Controller repository.

### Download the policy

```bash
curl -O https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.11.0/docs/install/iam_policy.json
```

### Verify the downloaded file

```bash
ls -lh iam_policy.json
```

The `iam_policy.json` file was downloaded successfully.

---

## 11. Create the IAM Policy

After downloading the policy file, I created an IAM policy in AWS.

### Create the IAM policy

```bash
aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://iam_policy.json
```

### Understanding the command

| Option | Purpose |
|---|---|
| `aws iam create-policy` | Creates an IAM policy |
| `--policy-name` | Specifies the policy name |
| `--policy-document` | Specifies the JSON policy file |

The policy was created successfully.

The policy name is:

```text
AWSLoadBalancerControllerIAMPolicy
```

---

## 12. Create IAM Role and Kubernetes Service Account

After creating the IAM policy, I created an IAM role and connected it to a Kubernetes service account.

This allows the AWS Load Balancer Controller to access AWS services securely without storing AWS access keys inside the application pods.

### Get the AWS Account ID

```bash
aws sts get-caller-identity \
  --query Account \
  --output text
```

### Create IAM role and service account

I used the following command:

```bash
eksctl create iamserviceaccount \
  --cluster=demo-cluster \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --role-name AmazonEKSLoadBalancerControllerRole \
  --attach-policy-arn=arn:aws:iam::YOUR_ACCOUNT_ID:policy/AWSLoadBalancerControllerIAMPolicy \
  --approve
```

Replace `YOUR_ACCOUNT_ID` with the account ID returned by the previous command.

### Understanding the command

| Option | Purpose |
|---|---|
| `--cluster` | Specifies the EKS cluster |
| `--namespace` | Creates the service account in kube-system |
| `--name` | Specifies the Kubernetes service account name |
| `--role-name` | Specifies the IAM role name |
| `--attach-policy-arn` | Attaches the IAM policy |
| `--approve` | Approves the creation |

The IAM role and Kubernetes service account were created successfully.

The service account name is:

```text
aws-load-balancer-controller
```

---

## 13. Retrieve the EKS VPC ID

The AWS Load Balancer Controller needs the VPC ID to identify the VPC where the EKS cluster is running.

### Get the VPC ID

```bash
aws eks describe-cluster \
  --name demo-cluster \
  --region us-east-1 \
  --query "cluster.resourcesVpcConfig.vpcId" \
  --output text
```

The command returned my EKS VPC ID.

For my project, the VPC ID was:

```text
vpc-078a8e208403eaee0
```

---

## 14. Install Helm

Helm is a package manager for Kubernetes.

It helps install and manage applications inside Kubernetes clusters using Helm Charts.

I used Helm to install the AWS Load Balancer Controller.

### Install Helm

```bash
sudo snap install helm --classic
```

### Verify Helm installation

```bash
helm version --short
```

Helm was installed successfully.

---

## 15. Add the AWS EKS Helm Repository

Before installing the AWS Load Balancer Controller, I added the official AWS EKS Helm repository.

### Add the repository

```bash
helm repo add eks https://aws.github.io/eks-charts
```

### Update the repository

```bash
helm repo update eks
```

### Verify the repository

```bash
helm repo list
```

The `eks` repository was added successfully.

---

## 16. Install AWS Load Balancer Controller Using Helm

The AWS Load Balancer Controller watches Kubernetes Ingress resources and creates and configures AWS Application Load Balancers.

I installed the controller using the AWS EKS Helm chart.

### Install the controller

```bash
helm install aws-load-balancer-controller eks/aws-load-balancer-controller -n kube-system \
  --set clusterName=demo-cluster \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  --set region=us-east-1 \
  --set vpcId=vpc-078a8e208403eaee0
```

### Understanding the command

| Option | Purpose |
|---|---|
| `helm install` | Installs a Helm chart |
| `aws-load-balancer-controller` | Helm release name |
| `eks/aws-load-balancer-controller` | AWS Load Balancer Controller chart |
| `-n kube-system` | Installs the controller in kube-system |
| `clusterName` | Specifies the EKS cluster |
| `serviceAccount.create=false` | Uses the existing service account |
| `serviceAccount.name` | Specifies the service account |
| `region` | Specifies the AWS region |
| `vpcId` | Specifies the EKS VPC |

The Helm installation completed successfully.

The release status was:

```text
STATUS: deployed
DESCRIPTION: Install complete
```

---

## 17. Verify AWS Load Balancer Controller

After installing the controller, I checked whether its deployment and pods were running.

### 17.1 Check the controller deployment

```bash
kubectl get deployment -n kube-system aws-load-balancer-controller
```

The deployment showed:

```text
READY   UP-TO-DATE   AVAILABLE
2/2     2            2
```

### 17.2 Check controller pods

```bash
kubectl get pods -n kube-system | grep aws-load-balancer-controller
```

Both controller pods were in the `Running` state.

### 17.3 Check the application Ingress

```bash
kubectl get ingress -n game-2048
```

The Ingress received an AWS Application Load Balancer DNS address.

This confirmed that the AWS Load Balancer Controller had detected the Ingress resource and provisioned an ALB.

---

## 18. Access the 2048 Application

After the Application Load Balancer was provisioned, I accessed the application using its DNS address.

### Get the ALB DNS address

```bash
kubectl get ingress -n game-2048
```

I copied the address from the `ADDRESS` column and opened it in my browser using HTTP.

My application was accessible at:

http://k8s-game2048-ingress2-bcac0b5b37-742524492.us-east-1.elb.amazonaws.com

The 2048 game opened successfully in the browser.

This confirmed that the application was deployed and accessible from the internet through the AWS Application Load Balancer.

---

## 19. Application Traffic Flow

The following is the traffic flow used in this project:

```text
             External User
                   |
                   v
       AWS Application Load Balancer
                   |
                   v
         Kubernetes Ingress Rules
                   |
                   v
          Kubernetes Service
                   |
                   v
       2048 Application Pods
                   |
                   v
             AWS Fargate
```

The AWS Load Balancer Controller watches Kubernetes Ingress resources and creates and configures the ALB.

It manages the load balancer configuration; it is not itself part of the user's traffic path.

---

## 20. Project Verification Summary

After completing the deployment, I verified all the important resources.

| Component | Result |
|---|---|
| AWS CLI | Configured |
| kubectl | Installed |
| eksctl | Installed |
| EKS Cluster | Created and Ready |
| Fargate Profile | Active |
| 2048 Deployment | 5 replicas available |
| 2048 Pods | 5/5 Running |
| Kubernetes Service | Created |
| Kubernetes Ingress | Created |
| IAM OIDC Provider | Associated |
| IAM Policy | Created |
| IAM Role | Created |
| Kubernetes Service Account | Created |
| Helm | Installed |
| AWS EKS Helm Repository | Added |
| AWS Load Balancer Controller | 2/2 Running |
| Application Load Balancer | Provisioned |
| 2048 Game | Successfully accessed in browser |

---

## 21. AWS Billing and Cleanup Notes

AWS resources created for this project can generate charges while they exist.

The important billable resources include:

- Amazon EKS cluster.
- AWS Fargate compute used by the application pods.
- Application Load Balancer.
- Associated networking resources.
- EC2 instance used to execute the commands.

I should not leave the project running after completing the practical.

Before cleaning up, I will save my screenshots and GitHub documentation.

### Cleanup checklist

- Remove the 2048 application and Ingress.
- Wait for the Application Load Balancer and its associated resources to be deleted.
- Uninstall the AWS Load Balancer Controller.
- Delete the EKS cluster and its remaining cluster resources.
- Check for leftover IAM resources and remove them if they are no longer needed.
- Check the AWS Console for any remaining billable resources.

**Important:** The cleanup process is a separate step. I must verify that resources have actually been deleted instead of assuming that stopping my EC2 instance stops all AWS charges.

---

## 22. What I Learned

By completing this project, I learned how to deploy an application on Amazon EKS using AWS Fargate.

I also understood how Kubernetes Deployment, Service and Ingress work together to expose an application to external users.

I gained practical experience with IAM OIDC integration, IAM roles, Kubernetes service accounts, Helm and the AWS Load Balancer Controller.

Most importantly, I learned how an Application Load Balancer can be automatically provisioned and configured through a Kubernetes Ingress resource.

**Project Status: Successfully Completed!**
