# Fix Kubernetes Deployment Issues

## 🔴 Current Issues

1. **Missing Secret**: `app-secrets` not found
   - Affects: `api-gateway`, `auth-service`
   - Error: `CreateContainerConfigError`

2. **Config Server Crashing**: `CrashLoopBackOff`
   - Needs investigation via logs

## ✅ Quick Fix

Run the fix script:

```bash
# Make script executable (Linux/Mac)
chmod +x scripts/fix-k8s-secrets.sh

# Run the fix
./scripts/fix-k8s-secrets.sh
```

Or manually apply:

```bash
# Apply ConfigMap
kubectl apply -f k8s/config/configmap.yaml

# Apply Secrets
kubectl apply -f k8s/config/secrets.yaml

# Apply PostgreSQL secret
kubectl apply -f k8s/postgresql/statefulset.yaml

# Restart deployments
kubectl rollout restart deployment/api-gateway
kubectl rollout restart deployment/auth-service
kubectl rollout restart deployment/config-server
```

## 🔍 Check Config Server Logs

```bash
# Get config-server pod name
kubectl get pods | grep config-server

# View logs
kubectl logs <config-server-pod-name> --tail=50

# Or use the script
chmod +x scripts/check-config-server-logs.sh
./scripts/check-config-server-logs.sh
```

## 📊 Verify Fix

```bash
# Check secrets exist
kubectl get secret app-secrets
kubectl get secret postgresql-secret
kubectl get configmap app-config

# Check pod status
kubectl get pods

# Watch pods restart
kubectl get pods -w
```

## 🔄 After Applying Secrets

The pods should automatically restart and pick up the secrets. If not:

```bash
# Force delete pods (they will recreate)
kubectl delete pod -l app=api-gateway
kubectl delete pod -l app=auth-service
kubectl delete pod -l app=config-server
```

## 📝 Expected Result

After applying secrets, you should see:
- ✅ `api-gateway`: Running (1/1)
- ✅ `auth-service`: Running (1/1)
- ✅ `config-server`: Running (1/1) or at least not crashing

