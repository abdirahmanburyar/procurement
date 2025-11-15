# Quick Fix: Create Missing Secrets

## 🔴 Problem
- `app-secrets` secret is missing
- Pods failing with `CreateContainerConfigError`
- Error: `secret "app-secrets" not found`

## ✅ Solution

### Option 1: Using kubectl directly (Recommended)

Run these commands on your VPS:

```bash
# First, check what namespace your pods are in
kubectl get pods --all-namespaces | grep api-gateway

# If they're in "procurement" namespace, use that. Otherwise use "default"
NAMESPACE="procurement"  # or "default"

# Create app-secrets
kubectl create secret generic app-secrets \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-password="postgres" \
  --from-literal=auth-db-password="postgres" \
  --from-literal=procurement-db-password="postgres" \
  --from-literal=quotation-db-password="postgres" \
  --from-literal=po-db-password="postgres" \
  --from-literal=inventory-db-password="postgres" \
  -n $NAMESPACE

# Create app-config ConfigMap
kubectl create configmap app-config \
  --from-literal=eureka-url="http://eureka-server:8761/eureka/" \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-host="postgresql" \
  --from-literal=postgres-port="5432" \
  --from-literal=postgres-user="postgres" \
  -n $NAMESPACE

# Create postgresql-secret
kubectl create secret generic postgresql-secret \
  --from-literal=postgres-password="postgres" \
  --from-literal=postgres-user="postgres" \
  -n $NAMESPACE

# Restart deployments
kubectl rollout restart deployment/api-gateway -n $NAMESPACE
kubectl rollout restart deployment/auth-service -n $NAMESPACE
kubectl rollout restart deployment/config-server -n $NAMESPACE
```

### Option 2: Using the script

If you have the repository on your VPS:

```bash
cd /path/to/procurement
chmod +x scripts/create-secrets-kubectl.sh
./scripts/create-secrets-kubectl.sh procurement
```

### Option 3: Apply YAML files

If you have the k8s directory on your VPS:

```bash
# Check namespace first
kubectl get pods --all-namespaces

# Apply to the correct namespace
kubectl apply -f k8s/config/configmap.yaml -n procurement
kubectl apply -f k8s/config/secrets.yaml -n procurement
kubectl apply -f k8s/postgresql/statefulset.yaml -n procurement

# Restart deployments
kubectl rollout restart deployment/api-gateway -n procurement
kubectl rollout restart deployment/auth-service -n procurement
kubectl rollout restart deployment/config-server -n procurement
```

## 🔍 Verify

```bash
# Check secrets exist
kubectl get secret app-secrets -n procurement
kubectl get secret postgresql-secret -n procurement
kubectl get configmap app-config -n procurement

# Check pods
kubectl get pods -n procurement

# Watch pods restart
kubectl get pods -n procurement -w
```

## 📝 Note

Based on your pod events, it looks like your resources might be in a namespace called `procurement`. Make sure to use the correct namespace when creating secrets!

To check all namespaces:
```bash
kubectl get namespaces
kubectl get pods --all-namespaces
```

