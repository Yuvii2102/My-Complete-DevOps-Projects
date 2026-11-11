# Input variables for Terraform configuration

variable "instance_type" {
  description = "EC2 instance type"
  default     = "t3.micro"
}

variable "instance_count" {
  description = "Number of EC2 instances"
  default     = 4
}
