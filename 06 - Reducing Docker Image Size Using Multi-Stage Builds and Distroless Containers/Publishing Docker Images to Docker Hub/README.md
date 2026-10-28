## 🐳 Final Step : Publishing Docker Images to Docker Hub

After building both Docker images, I published them to Docker Hub. This allows me to store, share and pull the images without rebuilding them on another machine.

### 1. Check the Available Images

I listed the locally available Docker images before publishing them.

```bash
docker images
```

### 2. Log in to Docker Hub

I logged in to my Docker Hub account.

```bash
docker login
```

### 3. Tag the Optimized Multi-Stage Image

I tagged the optimized image with my Docker Hub repository name.

```bash
sudo docker tag simplecalculator-multistage yuvi2102/go-calculator:multistage
```

### 4. Push the Optimized Image 

I pushed the multi-stage image to Docker Hub.

```bash
sudo docker push yuvi2102/go-calculator:multistage
```

During the initial attempt, I encountered an authorization error. I logged out and logged in again before retrying the push.

```bash
sudo docker logout
sudo docker login
sudo docker push yuvi2102/go-calculator:multistage
```

After resolving the authentication issue, I continued with the image-publishing process.

### 4. Create a Repository on Docker Hub

Before uploading the images, I manually created a repository named go-calculator on Docker Hub.

This repository stores both Docker images under separate tags, allowing me to distinguish the standard image from the optimized multi-stage image.

### 5. Tag and Push the Standard Image

Next, I tagged the image built without a multi-stage build.

```bash
sudo docker tag simplecalculator yuvi2102/go-calculator:without-multistage
```

I then pushed it to Docker Hub.

```bash
sudo docker push yuvi2102/go-calculator:without-multistage
```

### 6. Verify the Images

I checked the local Docker images again.

```bash
sudo docker images
```

I also pulled both published images from Docker Hub to verify that they were available for download.

**Pull the standard image:**

```bash
sudo docker pull yuvi2102/go-calculator:without-multistage
```

**Pull the optimized multi-stage image:**

```bash
sudo docker pull yuvi2102/go-calculator:multistage
```

### 7. Docker Hub Repository

Both images use different tags within the same repository.

🔗 **Docker Hub Repository:** https://hub.docker.com/r/yuvi2102/go-calculator

| Image Tag | Build Method | Approximate Local Size |
|---|---|---:|
| `without-multistage` | Ubuntu-based image | 252 MB |
| `multistage` | Multi-stage build using `scratch` | 1.47 MB |

*Note: Image sizes are based on my earlier local build results and may vary slightly depending on the build environment.*

## 📚 What I Learned

- How to authenticate with Docker Hub.
- How to tag Docker images using a repository name and version tag.
- How to push Docker images to a remote registry.
- How to pull published images for use on another machine.
- How to maintain separate tags for standard and optimized images.

This step helped me understand how Docker images are stored, shared, and distributed through a container registry.
