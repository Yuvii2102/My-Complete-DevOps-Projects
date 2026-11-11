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

# Create three EC2 instances
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
