#!/bin/bash
# Fix all missing secrets in procurement namespace (non-interactive version)
# Designed for CI/CD pipelines

NAMESPACE="${NAMESPACE:-procurement}"
GITHUB_USER="${GITHUB_USER:-}"
GITHUB_TOKEN="${GITHUB_TOKEN:-}"

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

# 4. Create GitHub Container Registry pull secret (if credentials provided)
if [ -n "$GITHUB_USER" ] && [ -n "$GITHUB_TOKEN" ]; then
  echo "🐳 Creating ghcr-image-pull-secret..."
  kubectl delete secret ghcr-image-pull-secret -n $NAMESPACE --ignore-not-found=true
  kubectl create secret docker-registry ghcr-image-pull-secret \
    --docker-server=ghcr.io \
    --docker-username=$GITHUB_USER \
    --docker-password=$GITHUB_TOKEN \
    -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
else
  echo "⚠️  Skipping ghcr-image-pull-secret (GITHUB_USER or GITHUB_TOKEN not set)"
  echo "   Images must be public or secret created manually"
fi

echo ""
echo "✅ Verifying all secrets..."
kubectl get secret app-secrets -n $NAMESPACE || echo "⚠️  app-secrets not found"
kubectl get secret postgresql-secret -n $NAMESPACE || echo "⚠️  postgresql-secret not found"
kubectl get secret ghcr-image-pull-secret -n $NAMESPACE || echo "⚠️  ghcr-image-pull-secret not found"
kubectl get configmap app-config -n $NAMESPACE || echo "⚠️  app-config not found"

echo ""
echo "✅ Secrets setup complete!"

