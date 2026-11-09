# Practical: Encrypting and Managing Sensitive Data Using Ansible Vault

## Introduction

While working with Ansible, we sometimes need to store sensitive information such as AWS credentials, database passwords and API tokens.

Storing these details directly in plain text files is not secure.

Ansible provides a built-in feature called **Ansible Vault** that helps us encrypt sensitive data and protect it using a password.

In this practical, I learned how to create a Vault password file, encrypt YAML files, view encrypted files, edit encrypted files and decrypt them when required.

## 1. Create a Practice Directory

First, I created a separate directory to perform the Ansible Vault practical.

```bash
mkdir ~/ansible-vault-practice
cd ~/ansible-vault-practice
pwd
```

## 2. Create a Vault Password File

Ansible Vault requires a password to encrypt and decrypt files.

I created a password file named `vault.pass` using Vim.

```bash
vim vault.pass
```

I added my practice password inside the file:

```text
MyVault@123
```

After saving the file, I changed its permissions to make it accessible only to the owner.

```bash
chmod 600 vault.pass
ls -l vault.pass
```

The permissions were:

```text
-rw------- 1 ubuntu ubuntu ... vault.pass
```

This ensures that other users cannot read or modify the password file.

## 3. Create a Credentials YAML File

Next, I created a YAML file containing dummy credentials.

```bash
vim credentials.yaml
```

I added the following variables:

```yaml
aws_access_key: "DUMMY_ACCESS_KEY_12345"
aws_secret_key: "DUMMY_SECRET_KEY_67890"
db_username: "admin"
db_password: "MyDBPassword@123"
```

I checked the contents using:

```bash
cat credentials.yaml
```

## 4. Encrypt the Credentials File

Now I used Ansible Vault to encrypt the credentials file.

```bash
ansible-vault encrypt credentials.yaml --vault-password-file vault.pass
```

Output:

```text
Encryption successful
```

To verify the encryption, I used:

```bash
cat credentials.yaml
```

The file now displayed encrypted content starting with:

```text
$ANSIBLE_VAULT;1.1;AES256
```

The original credentials were no longer readable directly from the file.

## 5. View an Encrypted File

Ansible Vault allows us to view the original contents of an encrypted file without permanently decrypting it.

I used:

```bash
ansible-vault view credentials.yaml --vault-password-file vault.pass
```

Output:

```yaml
aws_access_key: "DUMMY_ACCESS_KEY_12345"
aws_secret_key: "DUMMY_SECRET_KEY_67890"
db_username: "admin"
db_password: "MyDBPassword@123"
```

The file remained encrypted after viewing it.

## 6. Edit an Encrypted File

Ansible Vault also allows us to edit encrypted files directly.

I used:

```bash
ansible-vault edit credentials.yaml --vault-password-file vault.pass
```

This opened the decrypted contents in Vim.

I added another variable:

```yaml
environment: "development"
```

After saving and exiting, Ansible Vault automatically encrypted the file again.

I verified it using:

```bash
cat credentials.yaml
```

The file still displayed encrypted content.

## 7. Encrypt an Existing YAML File

Next, I created another YAML file named `database.yaml`.

```bash
vim database.yaml
```

I added the following database details:

```yaml
database_host: "localhost"
database_port: 3306
database_name: "devops_db"
```

After creating the file, I encrypted it using:

```bash
ansible-vault encrypt database.yaml --vault-password-file vault.pass
```

Output:

```text
Encryption successful
```

I verified the encryption using:

```bash
cat database.yaml
```

The file now displayed encrypted content.

## 8. Decrypt an Encrypted File

Finally, I learned how to decrypt an encrypted file and restore its original contents.

I used:

```bash
ansible-vault decrypt database.yaml --vault-password-file vault.pass
```

Output:

```text
Decryption successful
```

To verify the result:

```bash
cat database.yaml
```

Output:

```yaml
database_host: "localhost"
database_port: 3306
database_name: "devops_db"
```

The file was successfully decrypted and its original contents were restored.

## Important Ansible Vault Commands

| Command | Purpose |
|---|---|
| `ansible-vault create file.yaml` | Create a new encrypted file |
| `ansible-vault encrypt file.yaml` | Encrypt an existing file |
| `ansible-vault view file.yaml` | View encrypted file contents |
| `ansible-vault edit file.yaml` | Edit an encrypted file |
| `ansible-vault decrypt file.yaml` | Decrypt an encrypted file |

To use a password file with these commands, add:

```bash
--vault-password-file vault.pass
```

## What I Learned

Through this practical, I learned:

- How Ansible Vault protects sensitive information.
- How to create and secure a Vault password file.
- How to encrypt existing YAML files.
- How to view encrypted data without decrypting the file.
- How to edit encrypted files directly.
- How to decrypt files and restore their original contents.

## Best Practices

- Never store actual passwords or AWS credentials in plain text.
- Use strong passwords for Ansible Vault.
- Keep Vault password files secure.
- Never upload Vault password files or private keys to GitHub.
- Add sensitive files such as `vault.pass` and private keys to `.gitignore`.
- Use AWS Secrets Manager or AWS Systems Manager Parameter Store when managing secrets in AWS environments.

## Conclusion

This practical helped me understand how Ansible Vault works and how it can be used to protect sensitive information in automation projects.

It is an important feature when working with configuration management and infrastructure automation because security should always be considered while handling credentials.
