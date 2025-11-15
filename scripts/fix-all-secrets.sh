#!/bin/bash
# Fix all missing secrets in procurement namespace

NAMESPACE="procurement"

echo "🔧 Fixing all secrets in namespace: $NAMESPACE"
echo ""

# 1. Create app-secrets
echo "🔐 Creating app-secrets..."
kubectl create secret generic app-secrets \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-password="postgres" \
  --from-literal=auth-db-password="postgres" \
  --from-literal=procurement-db-password="postgres" \
  --from-literal=quotation-db-password="postgres" \
  --from-literal=po-db-password="postgres" \
  --from-literal=inventory-db-password="postgres" \
  -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# 2. Create app-config
echo "📝 Creating app-config ConfigMap..."
kubectl create configmap app-config \
  --from-literal=eureka-url="http://eureka-server:8761/eureka/" \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-host="postgresql" \
  --from-literal=postgres-port="5432" \
  --from-literal=postgres-user="postgres" \
  -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# 3. Create postgresql-secret
echo "🐘 Creating postgresql-secret..."
kubectl create secret generic postgresql-secret \
  --from-literal=postgres-password="postgres" \
  --from-literal=postgres-user="postgres" \
  -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# 4. Create GitHub Container Registry pull secret
echo "🐳 Creating ghcr-image-pull-secret..."
echo ""
echo "⚠️  You need a GitHub Personal Access Token (PAT) with 'read:packages' permission"
echo "   Create one at: https://github.com/settings/tokens"
echo ""
read -p "Enter your GitHub username: " GITHUB_USER
read -sp "Enter your GitHub Personal Access Token: " GITHUB_TOKEN
echo ""

kubectl create secret docker-registry ghcr-image-pull-secret \
  --docker-server=ghcr.io \
  --docker-username=$GITHUB_USER \
  --docker-password=$GITHUB_TOKEN \
  -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

echo ""
echo "✅ Verifying all secrets..."
kubectl get secret app-secrets -n $NAMESPACE
kubectl get secret postgresql-secret -n $NAMESPACE
kubectl get secret ghcr-image-pull-secret -n $NAMESPACE
kubectl get configmap app-config -n $NAMESPACE

echo ""
echo "🔄 Restarting deployments..."
kubectl rollout restart deployment/api-gateway -n $NAMESPACE
kubectl rollout restart deployment/auth-service -n $NAMESPACE
kubectl rollout restart deployment/config-server -n $NAMESPACE

echo ""
echo "📊 Current pod status:"
kubectl get pods -n $NAMESPACE

echo ""
echo "✅ Done! Watch pods with: kubectl get pods -n $NAMESPACE -w"

