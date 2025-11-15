#!/bin/bash
# Connect to PostgreSQL in Kubernetes
# Usage: ./connect-postgres-k8s.sh

# Get pod name
POD_NAME=$(kubectl get pods -l app=postgresql -o jsonpath='{.items[0].metadata.name}')

if [ -z "$POD_NAME" ]; then
    echo "❌ PostgreSQL pod not found!"
    exit 1
fi

echo "🔌 Connecting to PostgreSQL pod: $POD_NAME"
kubectl exec -it $POD_NAME -- psql -U postgres -d procurement_auth_db

