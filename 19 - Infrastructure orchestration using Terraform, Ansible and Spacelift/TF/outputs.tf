# Outputs from Terraform resources

output "instance_public_ips" {
  description = "Public IP addresses of EC2 instances"
  value       = aws_instance.web[*].public_ip
}

output "instance_ids" {
  description = "IDs of EC2 instances"
  value       = aws_instance.web[*].id
}
