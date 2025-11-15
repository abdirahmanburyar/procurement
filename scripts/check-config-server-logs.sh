#!/bin/bash
# Check config-server logs to diagnose crash issues

echo "🔍 Checking config-server pod status..."
kubectl get pods | grep config-server

echo ""
echo "📋 Getting config-server logs..."
CONFIG_POD=$(kubectl get pods -l app=config-server -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

if [ -z "$CONFIG_POD" ]; then
    echo "❌ No config-server pod found!"
    exit 1
fi

echo "Pod: $CONFIG_POD"
echo ""
kubectl logs $CONFIG_POD --tail=50

echo ""
echo "📊 Pod events:"
kubectl describe pod $CONFIG_POD | grep -A 10 "Events:"

