#!/bin/bash

set -e

NAMESPACE="procurement"
REGISTRY="${REGISTRY:-ghcr.io/YOUR_USERNAME}"
VERSION="${VERSION:-latest}"

echo "🚀 Deploying Procurement System to Kubernetes"
echo "Namespace: $NAMESPACE"
echo "Registry: $REGISTRY"
echo "Version: $VERSION"
echo ""

# Create namespace
echo "📦 Creating namespace..."
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Update image references
echo "🔄 Updating image references..."
find k8s/services -name "*.yaml" -exec sed -i.bak "s|ghcr.io/YOUR_USERNAME|$REGISTRY|g" {} \;
find k8s/services -name "*.yaml" -exec sed -i.bak "s|:latest|:$VERSION|g" {} \;

# Deploy PostgreSQL
echo "🗄️  Deploying PostgreSQL..."
kubectl apply -f k8s/postgresql/statefulset.yaml -n $NAMESPACE
kubectl wait --for=condition=ready pod -l app=postgresql -n $NAMESPACE --timeout=300s || true
kubectl apply -f k8s/postgresql/init-job.yaml -n $NAMESPACE
echo "⏳ Waiting for databases to initialize..."
sleep 10

# Deploy ConfigMaps and Secrets
echo "🔐 Deploying ConfigMaps and Secrets..."
kubectl apply -f k8s/config/ -n $NAMESPACE

# Deploy Eureka Server
echo "🔍 Deploying Eureka Server..."
kubectl apply -f k8s/services/eureka-server.yaml -n $NAMESPACE
kubectl wait --for=condition=available deployment/eureka-server -n $NAMESPACE --timeout=300s || true
sleep 10

# Deploy Config Server
echo "⚙️  Deploying Config Server..."
kubectl apply -f k8s/services/config-server.yaml -n $NAMESPACE
sleep 20

# Deploy API Gateway
echo "🚪 Deploying API Gateway..."
kubectl apply -f k8s/services/api-gateway.yaml -n $NAMESPACE

# Deploy Microservices
echo "🔧 Deploying Microservices..."
kubectl apply -f k8s/services/auth-service.yaml -n $NAMESPACE
kubectl apply -f k8s/services/procurement-service.yaml -n $NAMESPACE
kubectl apply -f k8s/services/quotation-service.yaml -n $NAMESPACE
kubectl apply -f k8s/services/purchase-order-service.yaml -n $NAMESPACE
kubectl apply -f k8s/services/inventory-service.yaml -n $NAMESPACE

# Deploy Frontend
echo "🎨 Deploying Frontend..."
kubectl apply -f k8s/services/frontend.yaml -n $NAMESPACE

# Deploy Ingress
echo "🌐 Deploying Ingress..."
kubectl apply -f k8s/ingress/ingress.yaml -n $NAMESPACE

# Restore original files
echo "🧹 Cleaning up..."
find k8s/services -name "*.yaml.bak" -delete

echo ""
echo "✅ Deployment complete!"
echo ""
echo "📊 Check status:"
echo "   kubectl get pods -n $NAMESPACE"
echo ""
echo "🌐 Get ingress IP:"
echo "   kubectl get ingress -n $NAMESPACE"
echo ""

