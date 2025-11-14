# Kubernetes Deployment Guide

Complete guide for deploying the Procurement System to Kubernetes.

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Quick Start](#quick-start)
3. [Manual Deployment](#manual-deployment)
4. [CI/CD Setup](#cicd-setup)
5. [Configuration](#configuration)
6. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required

- Kubernetes cluster (k3s, minikube, or cloud cluster)
- kubectl configured and connected
- Docker registry access
- NGINX Ingress Controller (or compatible)
- Storage class for PersistentVolumes

### Recommended for Test Environment

- k3s single-node cluster
- 2 vCPU / 8 GB RAM minimum
- Local storage class (local-path)

## Quick Start

### 1. Install k3s (Single Node)

```bash
# Install k3s
curl -sfL https://get.k3s.io | sh -

# Verify installation
sudo kubectl get nodes

# Copy kubeconfig for non-root access
mkdir -p ~/.kube
sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
sudo chown $USER ~/.kube/config
```

### 2. Install NGINX Ingress Controller

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

# Wait for ingress controller to be ready
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s
```

### 3. Configure Image Registry

Update image references in all service manifests:

```bash
# Replace YOUR_USERNAME with your actual registry path
find k8s/services -name "*.yaml" -exec sed -i 's/YOUR_USERNAME/your-username/g' {} \;
```

Or manually edit each file in `k8s/services/` and replace:
- `ghcr.io/YOUR_USERNAME/procurement-*` with your registry path

### 4. Create Namespace

```bash
kubectl create namespace procurement
```

### 5. Deploy PostgreSQL

```bash
# Deploy StatefulSet
kubectl apply -f k8s/postgresql/statefulset.yaml -n procurement

# Wait for PostgreSQL to be ready
kubectl wait --for=condition=ready pod -l app=postgresql -n procurement --timeout=300s

# Initialize databases
kubectl apply -f k8s/postgresql/init-job.yaml -n procurement

# Verify databases were created
kubectl logs job/postgresql-init -n procurement
```

### 6. Configure Secrets

**IMPORTANT**: Update `k8s/config/secrets.yaml` with secure passwords:

```yaml
stringData:
  jwt-secret: "your-secure-jwt-secret-here"
  postgres-password: "secure-password"
  # ... other passwords
```

Then apply:

```bash
kubectl apply -f k8s/config/ -n procurement
```

### 7. Deploy Services in Order

```bash
# 1. Eureka Server (Service Discovery)
kubectl apply -f k8s/services/eureka-server.yaml -n procurement
kubectl wait --for=condition=available deployment/eureka-server -n procurement --timeout=300s

# 2. Config Server
kubectl apply -f k8s/services/config-server.yaml -n procurement
sleep 30

# 3. API Gateway
kubectl apply -f k8s/services/api-gateway.yaml -n procurement

# 4. Microservices
kubectl apply -f k8s/services/auth-service.yaml -n procurement
kubectl apply -f k8s/services/procurement-service.yaml -n procurement
kubectl apply -f k8s/services/quotation-service.yaml -n procurement
kubectl apply -f k8s/services/purchase-order-service.yaml -n procurement
kubectl apply -f k8s/services/inventory-service.yaml -n procurement

# 5. Frontend
kubectl apply -f k8s/services/frontend.yaml -n procurement
```

### 8. Deploy Ingress

```bash
# For local development
kubectl apply -f k8s/ingress/ingress.yaml -n procurement

# Get ingress IP
kubectl get ingress -n procurement
```

### 9. Configure Local DNS

Add to `/etc/hosts` (Linux/Mac) or `C:\Windows\System32\drivers\etc\hosts` (Windows):

```
<INGRESS_IP> procurement.local
```

### 10. Verify Deployment

```bash
# Check all pods
kubectl get pods -n procurement

# Check services
kubectl get svc -n procurement

# Check ingress
kubectl get ingress -n procurement

# View logs
kubectl logs -f deployment/api-gateway -n procurement
```

## Manual Deployment

### Step-by-Step Manual Deployment

#### 1. Build and Push Docker Images

```bash
# Set your registry
REGISTRY="ghcr.io/your-username"
VERSION="latest"

# Build and push each service
docker build -f eureka-server/Dockerfile -t $REGISTRY/procurement-eureka-server:$VERSION .
docker push $REGISTRY/procurement-eureka-server:$VERSION

docker build -f config-server/Dockerfile -t $REGISTRY/procurement-config-server:$VERSION .
docker push $REGISTRY/procurement-config-server:$VERSION

docker build -f api-gateway/Dockerfile -t $REGISTRY/procurement-api-gateway:$VERSION .
docker push $REGISTRY/procurement-api-gateway:$VERSION

docker build -f auth-service/Dockerfile -t $REGISTRY/procurement-auth-service:$VERSION .
docker push $REGISTRY/procurement-auth-service:$VERSION

docker build -f procurement-service/Dockerfile -t $REGISTRY/procurement-procurement-service:$VERSION .
docker push $REGISTRY/procurement-procurement-service:$VERSION

docker build -f quotation-service/Dockerfile -t $REGISTRY/procurement-quotation-service:$VERSION .
docker push $REGISTRY/procurement-quotation-service:$VERSION

docker build -f purchase-order-service/Dockerfile -t $REGISTRY/procurement-purchase-order-service:$VERSION .
docker push $REGISTRY/procurement-purchase-order-service:$VERSION

docker build -f inventory-service/Dockerfile -t $REGISTRY/procurement-inventory-service:$VERSION .
docker push $REGISTRY/procurement-inventory-service:$VERSION

docker build -f frontend/Dockerfile -t $REGISTRY/procurement-frontend:$VERSION ./frontend
docker push $REGISTRY/procurement-frontend:$VERSION
```

#### 2. Update Kubernetes Manifests

Update image references in all `k8s/services/*.yaml` files to match your registry.

#### 3. Apply Manifests

Follow the deployment order in the Quick Start section.

## CI/CD Setup

### GitHub Actions

The repository includes a complete CI/CD pipeline (`.github/workflows/ci-cd.yml`).

#### Setup Steps

1. **Configure GitHub Secrets**

   Go to Repository Settings → Secrets and variables → Actions, add:

   - `KUBECONFIG`: Base64 encoded kubeconfig file
     ```bash
     cat ~/.kube/config | base64 -w 0
     ```
   - `SLACK_WEBHOOK_URL`: (Optional) For notifications

2. **Update Workflow File**

   Edit `.github/workflows/ci-cd.yml`:

   ```yaml
   env:
     REGISTRY: ghcr.io
     IMAGE_PREFIX: your-username/procurement
   ```

3. **Enable GitHub Container Registry**

   - Go to Repository Settings → Actions → General
   - Enable "Read and write permissions" for GITHUB_TOKEN

4. **Push to Main Branch**

   The pipeline will:
   - Build all services
   - Run tests
   - Build Docker images
   - Push to registry
   - Deploy to Kubernetes

### GitLab CI

For GitLab CI, create `.gitlab-ci.yml`:

```yaml
stages:
  - build
  - test
  - docker
  - deploy

build:
  stage: build
  image: maven:3.9-eclipse-temurin-21
  script:
    - mvn clean package -DskipTests

docker-build:
  stage: docker
  image: docker:latest
  services:
    - docker:dind
  script:
    - docker build -f eureka-server/Dockerfile -t $CI_REGISTRY_IMAGE/eureka-server:$CI_COMMIT_SHA .
    - docker push $CI_REGISTRY_IMAGE/eureka-server:$CI_COMMIT_SHA
    # Repeat for all services...

deploy:
  stage: deploy
  image: bitnami/kubectl:latest
  script:
    - kubectl apply -f k8s/ -n procurement
```

## Configuration

### Environment Variables

All services use ConfigMaps and Secrets for configuration:

- **ConfigMap** (`k8s/config/configmap.yaml`): Non-sensitive configuration
- **Secrets** (`k8s/config/secrets.yaml`): Sensitive data (passwords, JWT secrets)

### Resource Limits

Optimized for 2 vCPU / 8 GB RAM:

- **Microservices**: 256Mi-512Mi memory, 200m-500m CPU
- **PostgreSQL**: 256Mi-512Mi memory, 200m-500m CPU
- **Frontend**: 64Mi-128Mi memory, 50m-100m CPU
- **JVM Heap**: Limited to 512MB (`-Xmx512m`)

### Storage

PostgreSQL uses PersistentVolumeClaim (10Gi) with `local-path` storage class (k3s default).

### Scaling

For production, update replicas in deployments:

```yaml
spec:
  replicas: 3  # Increase from 1
```

Or use HPA:

```bash
kubectl apply -f k8s/hpa/hpa.yaml -n procurement
```

## Troubleshooting

### Pods Not Starting

```bash
# Check pod status
kubectl describe pod <pod-name> -n procurement

# Check events
kubectl get events -n procurement --sort-by='.lastTimestamp'

# Common issues:
# - ImagePullBackOff: Check image registry and credentials
# - CrashLoopBackOff: Check logs for errors
# - Pending: Check resource availability
```

### Database Connection Issues

```bash
# Verify PostgreSQL is running
kubectl get pods -l app=postgresql -n procurement

# Check PostgreSQL logs
kubectl logs -l app=postgresql -n procurement

# Test database connectivity
kubectl run -it --rm psql-test --image=postgres:15-alpine --restart=Never -- \
  psql -h postgresql -U postgres -d procurement_auth_db

# Verify databases exist
kubectl exec -it postgresql-0 -n procurement -- psql -U postgres -c "\l"
```

### Service Discovery Issues

```bash
# Check Eureka Server
kubectl logs deployment/eureka-server -n procurement

# Port-forward to access Eureka dashboard
kubectl port-forward svc/eureka-server 8761:8761 -n procurement
# Access: http://localhost:8761

# Verify services are registered
kubectl exec -it deployment/eureka-server -n procurement -- curl http://localhost:8761/eureka/apps
```

### Ingress Issues

```bash
# Check ingress controller
kubectl get pods -n ingress-nginx

# Check ingress status
kubectl describe ingress procurement-ingress -n procurement

# Test ingress
curl -H "Host: procurement.local" http://<INGRESS_IP>/
```

### Resource Exhaustion

```bash
# Check node resources
kubectl top nodes

# Check pod resources
kubectl top pods -n procurement

# If resources are low, reduce replicas or resource limits
```

### Flyway Migration Issues

```bash
# Check service logs for Flyway errors
kubectl logs deployment/auth-service -n procurement | grep -i flyway

# Verify database schema
kubectl exec -it postgresql-0 -n procurement -- \
  psql -U postgres -d procurement_auth_db -c "\dt"
```

## Accessing Services

### Port Forwarding

```bash
# API Gateway
kubectl port-forward svc/api-gateway 8080:8080 -n procurement

# Eureka Dashboard
kubectl port-forward svc/eureka-server 8761:8761 -n procurement

# Frontend
kubectl port-forward svc/frontend 3000:80 -n procurement
```

### Ingress Access

After configuring ingress and DNS:

- **Frontend**: http://procurement.local
- **API**: http://procurement.local/api
- **Eureka**: Port-forward required

## Production Checklist

- [ ] Update all secrets with secure values
- [ ] Configure TLS certificates for Ingress
- [ ] Set up monitoring (Prometheus/Grafana)
- [ ] Configure logging aggregation
- [ ] Set up PostgreSQL backups
- [ ] Configure resource limits based on load
- [ ] Enable HPA for auto-scaling
- [ ] Set up PodDisruptionBudgets
- [ ] Configure network policies
- [ ] Set up health checks and alerts
- [ ] Review and update security policies

## Additional Resources

- [k3s Documentation](https://k3s.io/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [NGINX Ingress Controller](https://kubernetes.github.io/ingress-nginx/)

