#!/bin/bash
# Quick fix script to create missing secrets
# This script will find the namespace and create the secrets

echo "🔍 Finding namespace with pods..."

# Find namespace with api-gateway pod
NAMESPACE=$(kubectl get pods --all-namespaces -o jsonpath='{range .items[*]}{.metadata.namespace}{"\t"}{.metadata.name}{"\n"}{end}' | grep api-gateway | head -1 | awk '{print $1}')

if [ -z "$NAMESPACE" ]; then
    echo "⚠️  No api-gateway pod found. Checking all namespaces..."
    kubectl get namespaces
    echo ""
    read -p "Enter namespace name (or press Enter for 'default'): " NAMESPACE
    NAMESPACE=${NAMESPACE:-default}
fi

echo "📦 Using namespace: $NAMESPACE"
echo ""

# Create app-secrets
echo "🔐 Creating app-secrets..."
kubectl create secret generic app-secrets \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-password="postgres" \
  --from-literal=auth-db-password="postgres" \
  --from-literal=procurement-db-password="postgres" \
  --from-literal=quotation-db-password="postgres" \
  --from-literal=po-db-password="postgres" \
  --from-literal=inventory-db-password="postgres" \
  -n $NAMESPACE 2>&1 | grep -v "already exists" || echo "✅ app-secrets already exists or created"

# Create app-config
echo "📝 Creating app-config ConfigMap..."
kubectl create configmap app-config \
  --from-literal=eureka-url="http://eureka-server:8761/eureka/" \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-host="postgresql" \
  --from-literal=postgres-port="5432" \
  --from-literal=postgres-user="postgres" \
  -n $NAMESPACE 2>&1 | grep -v "already exists" || echo "✅ app-config already exists or created"

# Create postgresql-secret
echo "🐘 Creating postgresql-secret..."
kubectl create secret generic postgresql-secret \
  --from-literal=postgres-password="postgres" \
  --from-literal=postgres-user="postgres" \
  -n $NAMESPACE 2>&1 | grep -v "already exists" || echo "✅ postgresql-secret already exists or created"

echo ""
echo "✅ Verifying secrets..."
kubectl get secret app-secrets -n $NAMESPACE
kubectl get configmap app-config -n $NAMESPACE
kubectl get secret postgresql-secret -n $NAMESPACE

echo ""
echo "🔄 Restarting deployments..."
kubectl rollout restart deployment/api-gateway -n $NAMESPACE 2>/dev/null || echo "⚠️  api-gateway deployment not found"
kubectl rollout restart deployment/auth-service -n $NAMESPACE 2>/dev/null || echo "⚠️  auth-service deployment not found"
kubectl rollout restart deployment/config-server -n $NAMESPACE 2>/dev/null || echo "⚠️  config-server deployment not found"

echo ""
echo "📊 Current pod status:"
kubectl get pods -n $NAMESPACE

echo ""
echo "✅ Done! Watch pods with: kubectl get pods -n $NAMESPACE -w"

