#!/bin/bash
# Fix missing Kubernetes secrets and configmaps
# This script applies the necessary secrets and configmaps

echo "🔧 Applying Kubernetes secrets and configmaps..."

# Apply ConfigMap
echo "📝 Applying app-config ConfigMap..."
kubectl apply -f k8s/config/configmap.yaml

# Apply Secrets
echo "🔐 Applying app-secrets Secret..."
kubectl apply -f k8s/config/secrets.yaml

# Apply PostgreSQL secret
echo "🐘 Applying postgresql-secret..."
kubectl apply -f k8s/postgresql/statefulset.yaml

# Verify secrets exist
echo ""
echo "✅ Verifying secrets..."
kubectl get secret app-secrets
kubectl get secret postgresql-secret
kubectl get configmap app-config

echo ""
echo "🔄 Restarting deployments to pick up secrets..."
kubectl rollout restart deployment/api-gateway
kubectl rollout restart deployment/auth-service
kubectl rollout restart deployment/config-server

echo ""
echo "✅ Done! Waiting for pods to restart..."
sleep 5
kubectl get pods

