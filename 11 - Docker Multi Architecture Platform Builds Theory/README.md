# Docker Multi-Architecture / Multi-Platform Images

## 1. Why Was Docker Introduced ?

Before Docker, developers and QA engineers faced a common problem called **"It works on my machine!"**

Let's understand this with an example.

Imagine an organization where a developer is working on a Java application using a Windows 8 machine.

The developer has installed:
- JDK 1.5
- Maven
- Tomcat

After developing the application, the developer builds a JAR file using Maven and deploys it on Tomcat. Everything works fine on the developer's machine.

Now the developer shares the application with a QA engineer for testing.

But the QA engineer uses a Windows 10 server with JDK 1.6 and a different environment configuration.

When the QA engineer tries to build and run the application, it fails because of differences in software versions and configurations.

Now the developer says:

**"It works on my machine!"**

This was a common problem because both environments had different configurations.

System administrators had to spend hours or sometimes days identifying and fixing these differences.

### How Docker Solved This Problem

Docker introduced containerization to solve these environment-related problems.

Instead of manually installing all the required dependencies on every machine, developers can create a Dockerfile.

A Dockerfile contains instructions to build a Docker image, including:

- Base image
- Required software versions
- Application dependencies
- Environment configurations
- Commands to run the application

The Docker image packages the application and its required dependencies.

Now both the developer and QA engineer can use the same Docker image to run the application.

This provides a consistent environment and reduces the chances of environment-related issues.

**Note:** Docker containers share the host operating system's kernel. They do not contain a complete operating system like virtual machines.

---

## 2. What Problem Did Docker Still Have?

Docker solved most of the environment consistency problems, but there was another challenge.

That was **different CPU architectures**.

For example:

- A developer builds a Docker image on an AMD64 machine.
- A QA engineer tries to run the same image on an ARM64 machine.

The image might not work because it contains architecture-specific binaries and dependencies.

Earlier, most traditional computers and servers used AMD64 architecture. But today, we have different CPU architectures such as AMD64, ARM64, IBM Z and IBM Power.

Managing separate images for different architectures became another challenge.

---

## 3. Understanding CPU Architecture

CPU architecture defines how a processor works and executes instructions.

Some commonly used CPU architectures are:

| Architecture | Common Usage |
|---|---|
| AMD64 (x86_64) | Intel and AMD 64-bit processors |
| ARM64 (AArch64) | Apple Silicon Macs and AWS Graviton |
| IBM Z | IBM mainframe systems |
| IBM Power (PPC64LE) | IBM Power systems |

### AMD64

AMD64 is also known as x86_64.

It is commonly used in traditional desktop computers, laptops and cloud servers.

### ARM64

ARM64 is a 64-bit ARM architecture.

It is used in Apple Silicon Macs, AWS Graviton instances and other ARM-based devices.

**Important:**

- AMD64 and ARM64 are CPU architectures.
- Windows and Linux are operating systems.

For example:

- Linux + AMD64
- Linux + ARM64
- Windows + AMD64
- Windows + ARM64

These are different operating system and architecture combinations.

---

## 4. What Are Multi-Architecture / Multi-Platform Images?

A multi-architecture Docker image supports multiple CPU architectures under a single image tag.

Earlier, if we wanted to support AMD64 and ARM64, we might create two separate images.

For example:

```bash
myapp:amd64
myapp:arm64
```

We would have to manage different image tags for different architectures.

But Docker provides a feature called multi-platform builds.

Using this feature, we can build images for multiple architectures using a single Dockerfile.

For example:

```bash
myapp:latest
```

The same image tag can point to multiple architecture-specific images.

### How Does It Work?

When we push a multi-platform image to Docker Hub, Docker stores an image index (also called a manifest list in this context).

This index contains references to different platform-specific images.

Example:

```text
                 Docker Hub
                     |
                myapp:latest
                     |
               Image Index
                     |
           ---------------------
           |                   |
        AMD64                 ARM64
           |                   |
       AMD Image            ARM Image
```

Now imagine two developers.

**Developer 1:**
- Uses an AMD64 machine.
- Runs `docker pull myapp:latest`.
- Docker selects the AMD64 image.

**Developer 2:**
- Uses an ARM64 machine.
- Runs `docker pull myapp:latest`.
- Docker selects the ARM64 image.

Both developers use the same Docker pull command.

```bash
docker pull myapp:latest
```

Docker automatically selects the appropriate platform-specific image based on the requested platform and the machine's architecture.

---

## 5. Real-World Use Case

Let's take an open-source project like Argo CD.

Imagine a DevOps engineer is responsible for building and publishing Argo CD Docker images.

If the engineer builds only an AMD64 image and pushes it to Docker Hub, users with ARM64 machines might face compatibility issues.

But if the engineer creates a multi-platform image supporting both AMD64 and ARM64, users can pull the same image tag.

Docker automatically selects the appropriate image for their platform.

This makes it easier for developers and DevOps engineers to distribute applications across different architectures.

---

## 6. Advantages of Multi-Platform Images

1. **Single Dockerfile:** We can build images for multiple architectures using the same Dockerfile in most cases.

2. **Single image tag:** Different architecture-specific images can be accessed using the same tag.

3. **Automatic platform selection:** Docker selects the appropriate image based on the platform.

4. **Simplified CI/CD:** We can build and publish images for multiple architectures using a single multi-platform build workflow.

5. **Better compatibility:** Applications can support different CPU architectures.

6. **Easy maintenance:** We don't have to manage multiple image tags manually for every architecture.

---

## 7. Technologies Used for Multi-Platform Builds

Docker uses two important technologies for building multi-platform images.

### A. Docker BuildKit

BuildKit is the modern build engine used by Docker.

It improves the Docker image-building process and provides features such as:

- Better build performance
- Build caching
- Parallel builds
- Multi-platform builds

We can think of BuildKit as the engine that performs the image-building process.

### B. Docker Buildx

Buildx is a Docker CLI plugin that provides advanced build capabilities using BuildKit.

It allows us to build Docker images for multiple platforms.

Example:

```bash
docker buildx build \
--platform linux/amd64,linux/arm64 \
-t myapp:latest .
```

**Command explanation:**

| Command | Description |
|---|---|
| `docker buildx build` | Builds an image using Buildx |
| `--platform` | Specifies the target platforms |
| `linux/amd64` | Builds for AMD64 architecture |
| `linux/arm64` | Builds for ARM64 architecture |
| `-t myapp:latest` | Gives the image a name and tag |
| `.` | Uses the current directory as the build context |

With this command, we can request builds for both AMD64 and ARM64 architectures.

---

## 8. Multi-Platform Images in CI/CD

Multi-platform builds are also useful in CI/CD pipelines.

Imagine we are building an application using Jenkins or GitHub Actions.

Without multi-platform builds, we may need separate build processes for different architectures.

With Docker Buildx, we can use a single build command to build and publish images for multiple architectures.

Example workflow:

```text
             Source Code
                  |
              Dockerfile
                  |
             Docker Buildx
                  |
          -------------------
          |                 |
        AMD64              ARM64
          |                 |
       AMD Image          ARM Image
          |                 |
          -------------------
                  |
               Docker Hub
                  |
            Same Image Tag
```

This is especially useful for applications with large build times, such as machine learning applications.

For example, if one build takes 50 minutes and we need images for multiple architectures, managing separate build processes can increase the total build time and resource usage.

Multi-platform builds simplify this process by allowing us to manage multiple platform builds through one workflow.

---

## 9. Important Terms to Remember

| Term | Meaning |
|---|---|
| Docker | Platform used to build and run containers |
| Dockerfile | Instructions used to build a Docker image |
| Docker Image | Package containing an application and its dependencies |
| Container | Running instance of an image |
| CPU Architecture | Defines how a processor executes instructions |
| AMD64 | Traditional x86 64-bit architecture |
| ARM64 | ARM 64-bit architecture |
| Multi-Platform Image | Image supporting multiple CPU architectures |
| BuildKit | Modern Docker build engine |
| Buildx | Docker CLI plugin for advanced builds |
| Image Index | Contains references to platform-specific images |
| Docker Hub | Registry used to store and distribute Docker images |

---

## 10. Key Takeaways

- Docker solved the "It works on my machine" problem by providing consistent application environments.
- Docker images can contain architecture-specific binaries and dependencies.
- AMD64 and ARM64 are two different CPU architectures.
- Multi-platform images allow us to support multiple architectures under the same image tag.
- Docker BuildKit and Buildx help us build multi-platform images.
- Docker automatically selects the appropriate platform-specific image when pulling an image.

**One important point:** Multi-platform images do not mean that one identical image binary runs on every architecture. Instead, one image tag provides references to different platform-specific images.

---

**Next:** Practical implementation of Docker Multi-Architecture Images using Docker Buildx.
