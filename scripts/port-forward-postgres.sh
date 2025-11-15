#!/bin/bash
# Port forward PostgreSQL from Kubernetes to localhost
# Usage: ./port-forward-postgres.sh
# Then connect using: psql -h localhost -p 5432 -U postgres -d procurement_auth_db

echo "🔌 Port forwarding PostgreSQL (5432) from Kubernetes..."
echo "📝 Connect using: psql -h localhost -p 5432 -U postgres -d procurement_auth_db"
echo "⚠️  Press Ctrl+C to stop port forwarding"
echo ""

kubectl port-forward svc/postgresql 5432:5432

