## References and Learning Resources

### Abhishek Veeramalla — AWS DevOps Zero to Hero

I followed Abhishek Veeramalla's Day 22 EKS practical and used the following GitHub files for the project:

1. [Prerequisites — AWS CLI, kubectl and eksctl](https://github.com/iam-veeramalla/aws-devops-zero-to-hero/blob/main/day-22/prerequisites.md)
2. [2048 App Deployment and Fargate Profile](https://github.com/iam-veeramalla/aws-devops-zero-to-hero/blob/main/day-22/2048-app-deploy-ingress.md)
3. [ALB Controller Add-on](https://github.com/iam-veeramalla/aws-devops-zero-to-hero/blob/main/day-22/alb-controller-add-on.md)
4. [Complete Day 22 Repository](https://github.com/iam-veeramalla/aws-devops-zero-to-hero/tree/main/day-22)

### Official Project Resources

- [AWS Load Balancer Controller IAM Policy](https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.11.0/docs/install/iam_policy.json)
- [2048 Kubernetes Manifest](https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.5.4/docs/examples/2048/2048_full.yaml)
- [Amazon EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Helm Documentation](https://helm.sh/docs/)

---

## 24. Important Notes

1. **Fargate namespace:** The Fargate profile must use `game-2048`. Initially, I created it using `game2048`. I deleted the incorrect profile and recreated it with the correct namespace. The final commands in this documentation use `game-2048`.

2. **Version details:** The IAM policy was downloaded from AWS Load Balancer Controller version `v2.11.0`, while the 2048 application manifest uses version `v2.5.4`. These are the versions I used during this practical.

3. **ALB DNS address:** AWS generated the Application Load Balancer DNS address during deployment. The address documented in this project belongs to my deployment. A new deployment will generate a different DNS address.

4. **AWS cleanup:** The cleanup section is a checklist for removing the resources after completing the practical. It does not mean that the AWS resources have already been deleted.

5. **AWS billing:** EKS, Fargate, Application Load Balancer, EC2 and associated networking resources can generate charges. I should delete unused resources after testing and verify their removal in the AWS Console.

6. **Credentials:** AWS access keys, secret keys and other sensitive account information should never be uploaded to GitHub.
