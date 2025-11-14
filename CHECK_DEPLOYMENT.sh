#!/bin/bash

# Quick script to check deployment status on VPS
# Run on your VPS: ssh root@31.97.58.62

echo "🔍 Checking Procurement System Deployment Status"
echo "================================================"
echo ""

# Check namespace exists
echo "📦 Namespace:"
kubectl get namespace procurement 2>/dev/null && echo "✅ Namespace exists" || echo "❌ Namespace not found"

echo ""
echo "🔄 Deployments:"
kubectl get deployments -n procurement 2>/dev/null || echo "No deployments found"

echo ""
echo "📋 Pods:"
kubectl get pods -n procurement 2>/dev/null || echo "No pods found"

echo ""
echo "🌐 Services:"
kubectl get svc -n procurement 2>/dev/null || echo "No services found"

echo ""
echo "🚪 Ingress:"
kubectl get ingress -n procurement 2>/dev/null || echo "No ingress found"

echo ""
echo "🗄️  PostgreSQL:"
kubectl get statefulset -n procurement 2>/dev/null || echo "PostgreSQL not found"

echo ""
echo "📊 Pod Status Details:"
kubectl get pods -n procurement -o wide 2>/dev/null || echo "No pods to show"

echo ""
echo "🔍 Recent Events:"
kubectl get events -n procurement --sort-by='.lastTimestamp' | tail -10 2>/dev/null || echo "No events"

echo ""
echo "================================================"
echo "✅ Check complete!"
echo ""
echo "To view logs: kubectl logs -f <pod-name> -n procurement"
echo "To describe pod: kubectl describe pod <pod-name> -n procurement"

