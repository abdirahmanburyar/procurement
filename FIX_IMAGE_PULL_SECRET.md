# Fix ImagePullBackOff - Create GitHub Container Registry Secret

## 🔴 Problem
- Pods showing `ImagePullBackOff` error
- Missing `ghcr-image-pull-secret` to pull images from GitHub Container Registry

## ✅ Solution

### Step 1: Create GitHub Personal Access Token

1. Go to: https://github.com/settings/tokens
2. Click "Generate new token" → "Generate new token (classic)"
3. Name: `k8s-image-pull`
4. Select scope: `read:packages`
5. Click "Generate token"
6. **Copy the token** (you won't see it again!)

### Step 2: Create the Image Pull Secret

Run these commands on your VPS:

```bash
# Set your GitHub username and token
GITHUB_USER="abdirahmanburyar"  # Your GitHub username
GITHUB_TOKEN="your_personal_access_token_here"  # The token from Step 1

# Create the secret
kubectl create secret docker-registry ghcr-image-pull-secret \
  --docker-server=ghcr.io \
  --docker-username=$GITHUB_USER \
  --docker-password=$GITHUB_TOKEN \
  -n procurement
```

### Step 3: Also Create app-secrets (if not done)

```bash
# Create app-secrets
kubectl create secret generic app-secrets \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-password="postgres" \
  --from-literal=auth-db-password="postgres" \
  --from-literal=procurement-db-password="postgres" \
  --from-literal=quotation-db-password="postgres" \
  --from-literal=po-db-password="postgres" \
  --from-literal=inventory-db-password="postgres" \
  -n procurement

# Create app-config
kubectl create configmap app-config \
  --from-literal=eureka-url="http://eureka-server:8761/eureka/" \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-host="postgresql" \
  --from-literal=postgres-port="5432" \
  --from-literal=postgres-user="postgres" \
  -n procurement
```

### Step 4: Restart Deployments

```bash
kubectl rollout restart deployment/api-gateway -n procurement
kubectl rollout restart deployment/auth-service -n procurement
kubectl rollout restart deployment/config-server -n procurement
```

### Step 5: Verify

```bash
# Check secrets
kubectl get secret ghcr-image-pull-secret -n procurement
kubectl get secret app-secrets -n procurement

# Check pods
kubectl get pods -n procurement

# Watch pods restart
kubectl get pods -n procurement -w
```

## 🔍 Troubleshooting

### If images still can't be pulled:

1. **Check if images exist in registry:**
   ```bash
   # Check what images are available
   # You need to be logged in to GitHub Container Registry
   echo $GITHUB_TOKEN | docker login ghcr.io -u $GITHUB_USER --password-stdin
   docker pull ghcr.io/abdirahmanburyar/procurement-api-gateway:latest
   ```

2. **Check image tags in deployments:**
   ```bash
   kubectl get deployment api-gateway -n procurement -o yaml | grep image:
   ```

3. **Verify secret is correct:**
   ```bash
   kubectl get secret ghcr-image-pull-secret -n procurement -o yaml
   ```

4. **Check pod events:**
   ```bash
   kubectl describe pod <pod-name> -n procurement | grep -A 10 Events
   ```

## 📝 Alternative: Make Images Public

If you want to avoid using secrets, you can make your GitHub Container Registry packages public:

1. Go to: https://github.com/abdirahmanburyar?tab=packages
2. Click on each package (procurement-api-gateway, etc.)
3. Go to "Package settings" → "Change visibility" → "Make public"

Then remove `imagePullSecrets` from your deployments.

