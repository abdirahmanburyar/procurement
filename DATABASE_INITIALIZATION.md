# Database Initialization Guide

## 🗄️ Where to Initialize Databases

The databases are initialized **automatically** when you deploy to Kubernetes using the init job. Here's where and how:

## 📍 Location of Init Files

The database initialization files are located in:
```
k8s/postgresql/
├── statefulset.yaml    # PostgreSQL StatefulSet (creates the database server)
├── init-job.yaml       # Init job (creates the databases)
└── init-databases.sh   # Shell script (alternative method)
```

## 🚀 Automatic Initialization (Recommended)

### Method 1: Using the Deployment Script

The deployment script automatically initializes databases:

```bash
# This will:
# 1. Deploy PostgreSQL StatefulSet
# 2. Wait for PostgreSQL to be ready
# 3. Run the init job to create databases
./scripts/deploy-k8s.sh
```

### Method 2: Manual Kubernetes Deployment

```bash
# Step 1: Deploy PostgreSQL
kubectl apply -f k8s/postgresql/statefulset.yaml -n procurement

# Step 2: Wait for PostgreSQL to be ready
kubectl wait --for=condition=ready pod -l app=postgresql -n procurement --timeout=300s

# Step 3: Initialize databases (THIS IS WHERE INIT HAPPENS)
kubectl apply -f k8s/postgresql/init-job.yaml -n procurement

# Step 4: Verify databases were created
kubectl logs job/postgresql-init -n procurement
```

## 🔍 Verify Database Initialization

### Check Init Job Status

```bash
# Check if init job completed successfully
kubectl get jobs -n procurement

# View init job logs
kubectl logs job/postgresql-init -n procurement

# Check job completion
kubectl describe job postgresql-init -n procurement
```

### Verify Databases Exist

```bash
# Connect to PostgreSQL pod
kubectl exec -it postgresql-0 -n procurement -- psql -U postgres

# Then run in psql:
\l

# You should see:
# - procurement_auth_db
# - procurement_procurement_db
# - procurement_quotation_db
# - procurement_po_db
# - procurement_inventory_db
```

Or in one command:

```bash
kubectl exec -it postgresql-0 -n procurement -- psql -U postgres -c "\l" | grep procurement
```

## 🔧 Manual Initialization (If Needed)

If the init job didn't run or you need to recreate databases:

### Option 1: Re-run Init Job

```bash
# Delete the old job
kubectl delete job postgresql-init -n procurement

# Re-apply the init job
kubectl apply -f k8s/postgresql/init-job.yaml -n procurement

# Check logs
kubectl logs -f job/postgresql-init -n procurement
```

### Option 2: Manual SQL Execution

```bash
# Connect to PostgreSQL
kubectl exec -it postgresql-0 -n procurement -- psql -U postgres

# Then run:
CREATE DATABASE procurement_auth_db;
CREATE DATABASE procurement_procurement_db;
CREATE DATABASE procurement_quotation_db;
CREATE DATABASE procurement_po_db;
CREATE DATABASE procurement_inventory_db;

# Verify
\l
```

### Option 3: Using kubectl exec with SQL

```bash
kubectl exec -it postgresql-0 -n procurement -- psql -U postgres <<EOF
CREATE DATABASE IF NOT EXISTS procurement_auth_db;
CREATE DATABASE IF NOT EXISTS procurement_procurement_db;
CREATE DATABASE IF NOT EXISTS procurement_quotation_db;
CREATE DATABASE IF NOT EXISTS procurement_po_db;
CREATE DATABASE IF NOT EXISTS procurement_inventory_db;
EOF
```

## 📋 What Gets Initialized

The init job creates these 5 databases:

1. **procurement_auth_db** - For Auth Service
2. **procurement_procurement_db** - For Procurement Service
3. **procurement_quotation_db** - For Quotation Service
4. **procurement_po_db** - For Purchase Order Service
5. **procurement_inventory_db** - For Inventory Service

## ⚠️ Important Notes

1. **Flyway Migrations**: After databases are created, Flyway will automatically run migrations when services start. You don't need to manually create tables.

2. **Init Job is Idempotent**: The init job uses `WHERE NOT EXISTS` checks, so it's safe to run multiple times.

3. **Order Matters**: 
   - PostgreSQL must be running first
   - Then run init job
   - Then deploy services (services will run Flyway migrations)

4. **If Init Job Fails**:
   ```bash
   # Check why it failed
   kubectl describe job postgresql-init -n procurement
   kubectl logs job/postgresql-init -n procurement
   
   # Common issues:
   # - PostgreSQL not ready yet (wait longer)
   # - Wrong credentials (check secrets)
   # - Network issues
   ```

## 🔄 Re-initializing After Cleanup

If you deleted the namespace and want to start fresh:

```bash
# 1. Create namespace
kubectl create namespace procurement

# 2. Deploy PostgreSQL
kubectl apply -f k8s/postgresql/statefulset.yaml -n procurement

# 3. Wait for PostgreSQL
kubectl wait --for=condition=ready pod -l app=postgresql -n procurement --timeout=300s

# 4. Initialize databases
kubectl apply -f k8s/postgresql/init-job.yaml -n procurement

# 5. Verify
kubectl logs job/postgresql-init -n procurement
```

## 📝 Summary

**Where to initialize**: Run `kubectl apply -f k8s/postgresql/init-job.yaml -n procurement`

**When**: After PostgreSQL StatefulSet is running and ready

**What it does**: Creates 5 empty databases (tables are created by Flyway when services start)

**How to verify**: Check logs or connect to PostgreSQL and list databases

