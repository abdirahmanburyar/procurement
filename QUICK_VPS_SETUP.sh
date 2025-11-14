#!/bin/bash

# Quick VPS Setup Script for Procurement System
# Run this on your CentOS VPS: ssh root@31.97.58.62

set -e

VPS_IP="31.97.58.62"

echo "🚀 Setting up VPS for Procurement System..."
echo "VPS IP: $VPS_IP"
echo ""

# Update system
echo "📦 Updating system..."
yum update -y
yum install -y curl wget git vim

# Install k3s
echo "🔧 Installing k3s..."
curl -sfL https://get.k3s.io | sh -

# Wait for k3s to be ready
echo "⏳ Waiting for k3s to start..."
sleep 10

# Configure kubectl
echo "⚙️  Configuring kubectl..."
mkdir -p ~/.kube
cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
chmod 600 ~/.kube/config

# Update kubeconfig with VPS IP
echo "🌐 Updating kubeconfig with VPS IP: $VPS_IP..."
sed -i "s/127.0.0.1/$VPS_IP/g" ~/.kube/config

# Verify kubectl
echo "✅ Verifying kubectl..."
kubectl get nodes

# Install NGINX Ingress
echo "🚪 Installing NGINX Ingress Controller..."
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

echo "⏳ Waiting for Ingress to be ready (this may take 1-2 minutes)..."
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s || echo "⚠️  Ingress may still be starting..."

# Configure firewall
echo "🔥 Configuring firewall..."
if systemctl is-active --quiet firewalld; then
    firewall-cmd --permanent --add-port=6443/tcp
    firewall-cmd --permanent --add-port=80/tcp
    firewall-cmd --permanent --add-port=443/tcp
    firewall-cmd --permanent --add-port=10250/tcp
    firewall-cmd --reload
    echo "✅ Firewall configured"
else
    echo "⚠️  Firewalld not running, skipping firewall configuration"
fi

# Display kubeconfig
echo ""
echo "✅ Setup complete!"
echo ""
echo "📋 Your kubeconfig (for GitHub Secret):"
echo "----------------------------------------"
cat ~/.kube/config | base64 -w 0
echo ""
echo "----------------------------------------"
echo ""
echo "📝 Next steps:"
echo "1. Copy the base64 string above"
echo "2. Go to: https://github.com/abdirahmanburyar/procurement/settings/secrets/actions"
echo "3. Add new secret: Name=KUBECONFIG, Value=<paste base64 string>"
echo "4. Push to GitHub - deployment will happen automatically!"
echo ""
echo "🔍 Verify setup:"
echo "   kubectl get nodes"
echo "   kubectl get pods --all-namespaces"

