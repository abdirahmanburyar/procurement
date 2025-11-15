#!/bin/bash
# Create Kubernetes secrets directly using kubectl
# This works in any namespace

NAMESPACE=${1:-default}

echo "🔐 Creating secrets in namespace: $NAMESPACE"

# Create app-secrets
kubectl create secret generic app-secrets \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-password="postgres" \
  --from-literal=auth-db-password="postgres" \
  --from-literal=procurement-db-password="postgres" \
  --from-literal=quotation-db-password="postgres" \
  --from-literal=po-db-password="postgres" \
  --from-literal=inventory-db-password="postgres" \
  --namespace=$NAMESPACE \
  --dry-run=client -o yaml | kubectl apply -f -

# Create app-config ConfigMap
kubectl create configmap app-config \
  --from-literal=eureka-url="http://eureka-server:8761/eureka/" \
  --from-literal=jwt-secret="your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm-change-in-production" \
  --from-literal=postgres-host="postgresql" \
  --from-literal=postgres-port="5432" \
  --from-literal=postgres-user="postgres" \
  --namespace=$NAMESPACE \
  --dry-run=client -o yaml | kubectl apply -f -

# Create postgresql-secret
kubectl create secret generic postgresql-secret \
  --from-literal=postgres-password="postgres" \
  --from-literal=postgres-user="postgres" \
  --namespace=$NAMESPACE \
  --dry-run=client -o yaml | kubectl apply -f -

echo ""
echo "✅ Secrets created! Verifying..."
kubectl get secret app-secrets -n $NAMESPACE
kubectl get secret postgresql-secret -n $NAMESPACE
kubectl get configmap app-config -n $NAMESPACE

echo ""
echo "🔄 Restarting deployments..."
kubectl rollout restart deployment/api-gateway -n $NAMESPACE
kubectl rollout restart deployment/auth-service -n $NAMESPACE
kubectl rollout restart deployment/config-server -n $NAMESPACE

echo ""
echo "✅ Done! Check pods with: kubectl get pods -n $NAMESPACE"

