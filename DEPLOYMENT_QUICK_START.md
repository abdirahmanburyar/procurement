# Quick Deployment Guide

## 🚀 Quick Start (5 Minutes)

### Prerequisites Check

```bash
# Verify kubectl
kubectl version --client

# Verify cluster access
kubectl get nodes

# Verify ingress controller
kubectl get pods -n ingress-nginx
```

### 1. Update Image Registry

**IMPORTANT**: Before deploying, update image references:

```bash
# Option 1: Use sed (Linux/Mac)
sed -i 's/YOUR_USERNAME/your-github-username/g' k8s/services/*.yaml

# Option 2: Use the deployment script
export REGISTRY="ghcr.io/your-username"
export VERSION="latest"
./scripts/deploy-k8s.sh
```

Or manually edit each file in `k8s/services/`:
- Replace `ghcr.io/YOUR_USERNAME` with your registry path
- Replace `:latest` with your desired version tag

### 2. Update Secrets

Edit `k8s/config/secrets.yaml` with secure passwords:

```yaml
stringData:
  jwt-secret: "your-secure-jwt-secret-min-256-bits"
  postgres-password: "secure-password"
  # ... update all passwords
```

### 3. Initialize Databases

**This is where database initialization happens:**

```bash
# Deploy PostgreSQL first
kubectl apply -f k8s/postgresql/statefulset.yaml -n procurement
kubectl wait --for=condition=ready pod -l app=postgresql -n procurement --timeout=300s

# Initialize databases (THIS CREATES THE 5 DATABASES)
kubectl apply -f k8s/postgresql/init-job.yaml -n procurement

# Verify databases were created
kubectl logs job/postgresql-init -n procurement
```

See [DATABASE_INITIALIZATION.md](DATABASE_INITIALIZATION.md) for detailed instructions.

### 4. Deploy Everything

```bash
# Using the script (recommended - includes database init)
./scripts/deploy-k8s.sh

# Or manually
kubectl create namespace procurement
kubectl apply -f k8s/postgresql/statefulset.yaml -n procurement
kubectl wait --for=condition=ready pod -l app=postgresql -n procurement --timeout=300s
kubectl apply -f k8s/postgresql/init-job.yaml -n procurement
kubectl apply -f k8s/config/ -n procurement
kubectl apply -f k8s/services/eureka-server.yaml -n procurement
kubectl wait --for=condition=available deployment/eureka-server -n procurement --timeout=300s
kubectl apply -f k8s/services/config-server.yaml -n procurement
sleep 20
kubectl apply -f k8s/services/ -n procurement
kubectl apply -f k8s/ingress/ingress.yaml -n procurement
```

### 5. Verify

```bash
# Check all pods
kubectl get pods -n procurement

# Check services
kubectl get svc -n procurement

# Get ingress IP
kubectl get ingress -n procurement

# View logs
kubectl logs -f deployment/api-gateway -n procurement
```

### 6. Access

```bash
# Add to /etc/hosts (Linux/Mac) or C:\Windows\System32\drivers\etc\hosts (Windows)
<INGRESS_IP> procurement.local

# Access frontend
curl http://procurement.local

# Or port-forward
kubectl port-forward svc/frontend 3000:80 -n procurement
# Access: http://localhost:3000
```

## 🔧 Common Commands

### Check Status

```bash
# All resources
kubectl get all -n procurement

# Pods with details
kubectl get pods -n procurement -o wide

# Service endpoints
kubectl get endpoints -n procurement
```

### View Logs

```bash
# Specific pod
kubectl logs <pod-name> -n procurement

# Follow logs
kubectl logs -f deployment/api-gateway -n procurement

# All pods of a service
kubectl logs -l app=api-gateway -n procurement
```

### Restart Services

```bash
# Restart a deployment
kubectl rollout restart deployment/api-gateway -n procurement

# Restart all services
kubectl rollout restart deployment -n procurement
```

### Delete Everything

```bash
# Delete namespace (removes everything)
kubectl delete namespace procurement

# Or delete specific resources
kubectl delete -f k8s/services/ -n procurement
```

## 🐛 Quick Troubleshooting

### Pods Not Starting

```bash
# Describe pod
kubectl describe pod <pod-name> -n procurement

# Check events
kubectl get events -n procurement --sort-by='.lastTimestamp'
```

### Database Connection Issues

```bash
# Check PostgreSQL
kubectl get pods -l app=postgresql -n procurement
kubectl logs -l app=postgresql -n procurement

# Test connection
kubectl exec -it postgresql-0 -n procurement -- psql -U postgres -c "\l"
```

### Image Pull Errors

```bash
# Check image pull secrets
kubectl get secrets -n procurement

# Verify image exists
docker pull ghcr.io/your-username/procurement-api-gateway:latest
```

## 📝 Notes

- **First deployment**: Services may take 2-3 minutes to start (Flyway migrations)
- **Resource limits**: Optimized for 2 vCPU / 8 GB RAM
- **Storage**: PostgreSQL uses 10Gi PVC (adjust in `k8s/postgresql/statefulset.yaml`)
- **Secrets**: Always update `k8s/config/secrets.yaml` before deploying

## 🔗 Useful Links

- Full deployment guide: [KUBERNETES_DEPLOYMENT.md](KUBERNETES_DEPLOYMENT.md)
- Main README: [README.md](README.md)

