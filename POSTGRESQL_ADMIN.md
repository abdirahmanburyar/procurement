# PostgreSQL Administration Guide

This guide covers how to administer PostgreSQL in both **local (Docker Compose)** and **production (Kubernetes)** environments.

## 📋 Connection Details

### Local (Docker Compose)
- **Host**: `localhost`
- **Port**: `5432`
- **Username**: `postgres`
- **Password**: `postgres`
- **Database**: `procurement_auth_db`

### Production (Kubernetes)
- **Service Name**: `postgresql`
- **Port**: `5432` (internal)
- **Username**: `postgres`
- **Password**: `postgres` (from secret)
- **Database**: `procurement_auth_db`

---

## 🔧 Method 1: Command Line (psql)

### Local (Docker Compose)

#### Option A: Direct Connection
```bash
# Connect to PostgreSQL
psql -h localhost -p 5432 -U postgres -d procurement_auth_db

# Or using connection string
psql "postgresql://postgres:postgres@localhost:5432/procurement_auth_db"
```

#### Option B: Using Docker Exec
```bash
# Access PostgreSQL container shell
docker exec -it postgres psql -U postgres -d procurement_auth_db

# Or just get psql prompt
docker exec -it postgres psql -U postgres
```

### Production (Kubernetes)

#### Step 1: Port Forward (Required)
```bash
# Forward PostgreSQL port to local machine
kubectl port-forward svc/postgresql 5432:5432 -n default

# Keep this terminal open, then in another terminal:
```

#### Step 2: Connect
```bash
# Connect via localhost (after port-forward)
psql -h localhost -p 5432 -U postgres -d procurement_auth_db

# Password: postgres
```

#### Alternative: Direct kubectl exec
```bash
# Get PostgreSQL pod name
kubectl get pods | grep postgresql

# Connect directly to pod
kubectl exec -it postgresql-0 -- psql -U postgres -d procurement_auth_db

# Or just psql prompt
kubectl exec -it postgresql-0 -- psql -U postgres
```

---

## 🖥️ Method 2: GUI Tools

### Option 1: pgAdmin (Recommended)

#### Installation
- **Windows**: Download from https://www.pgadmin.org/download/
- **Mac**: `brew install --cask pgadmin4`
- **Linux**: `sudo apt install pgadmin4` (Ubuntu/Debian)

#### Connection Settings

**Local (Docker Compose)**:
- **Name**: Procurement Local
- **Host**: `localhost`
- **Port**: `5432`
- **Username**: `postgres`
- **Password**: `postgres`
- **Database**: `procurement_auth_db`

**Production (Kubernetes)**:
1. First, run port-forward:
   ```bash
   kubectl port-forward svc/postgresql 5432:5432
   ```
2. Then in pgAdmin:
   - **Name**: Procurement Production
   - **Host**: `localhost`
   - **Port**: `5432`
   - **Username**: `postgres`
   - **Password**: `postgres`
   - **Database**: `procurement_auth_db`

### Option 2: DBeaver (Free, Cross-platform)

#### Installation
- Download from: https://dbeaver.io/download/

#### Connection Settings
- **Database Type**: PostgreSQL
- **Host**: `localhost` (or use port-forward for Kubernetes)
- **Port**: `5432`
- **Database**: `procurement_auth_db`
- **Username**: `postgres`
- **Password**: `postgres`

### Option 3: TablePlus (Mac/Windows)

#### Installation
- **Mac**: `brew install --cask tableplus`
- **Windows**: Download from https://tableplus.com/

#### Connection
- Use same settings as pgAdmin

---

## 📊 Common Administration Tasks

### 1. List All Databases
```sql
\l
-- or
SELECT datname FROM pg_database;
```

### 2. Connect to a Database
```sql
\c procurement_auth_db
```

### 3. List All Tables
```sql
\dt
-- or
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public';
```

### 4. View Table Structure
```sql
\d users
-- or
\d+ users  -- detailed view
```

### 5. View All Users
```sql
\du
-- or
SELECT usename FROM pg_user;
```

### 6. View Table Data
```sql
SELECT * FROM users LIMIT 10;
SELECT * FROM roles;
SELECT * FROM permissions;
```

### 7. Check Database Size
```sql
SELECT pg_size_pretty(pg_database_size('procurement_auth_db'));
```

### 8. Check Table Sizes
```sql
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### 9. View Active Connections
```sql
SELECT * FROM pg_stat_activity;
```

### 10. Kill a Connection
```sql
SELECT pg_terminate_backend(pid) 
FROM pg_stat_activity 
WHERE datname = 'procurement_auth_db' AND pid <> pg_backend_pid();
```

### 11. Backup Database
```bash
# Local
docker exec postgres pg_dump -U postgres procurement_auth_db > backup.sql

# Kubernetes
kubectl exec postgresql-0 -- pg_dump -U postgres procurement_auth_db > backup.sql
```

### 12. Restore Database
```bash
# Local
docker exec -i postgres psql -U postgres procurement_auth_db < backup.sql

# Kubernetes
kubectl exec -i postgresql-0 -- psql -U postgres procurement_auth_db < backup.sql
```

---

## 🔐 User Management

### Create New User
```sql
CREATE USER newuser WITH PASSWORD 'password123';
GRANT ALL PRIVILEGES ON DATABASE procurement_auth_db TO newuser;
```

### Change Password
```sql
ALTER USER postgres WITH PASSWORD 'newpassword';
```

### Grant Permissions
```sql
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO username;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO username;
```

---

## 🐛 Troubleshooting

### Cannot Connect to PostgreSQL

**Local (Docker Compose)**:
```bash
# Check if container is running
docker ps | grep postgres

# Check logs
docker logs postgres

# Restart container
docker restart postgres
```

**Production (Kubernetes)**:
```bash
# Check pod status
kubectl get pods | grep postgresql

# Check logs
kubectl logs postgresql-0

# Describe pod for issues
kubectl describe pod postgresql-0

# Restart pod
kubectl delete pod postgresql-0
```

### Reset Password (Kubernetes)
```bash
# Update secret
kubectl create secret generic postgresql-secret \
  --from-literal=postgres-user=postgres \
  --from-literal=postgres-password=newpassword \
  --dry-run=client -o yaml | kubectl apply -f -

# Restart StatefulSet
kubectl rollout restart statefulset postgresql
```

### Access Denied
- Verify username and password match the secrets/config
- Check if database exists: `\l`
- Verify user has permissions: `\du`

---

## 📝 Quick Reference

### Useful psql Commands
```sql
\?          -- Help
\l          -- List databases
\c dbname   -- Connect to database
\dt         -- List tables
\d table    -- Describe table
\du         -- List users
\q          -- Quit
\conninfo   -- Connection info
\timing     -- Toggle query timing
```

### Environment Variables (Local)
```bash
export PGHOST=localhost
export PGPORT=5432
export PGUSER=postgres
export PGPASSWORD=postgres
export PGDATABASE=procurement_auth_db
```

Then you can just run: `psql`

---

## 🔗 Quick Access Scripts

### Local Access Script
Create `scripts/connect-postgres-local.sh`:
```bash
#!/bin/bash
docker exec -it postgres psql -U postgres -d procurement_auth_db
```

### Kubernetes Access Script
Create `scripts/connect-postgres-k8s.sh`:
```bash
#!/bin/bash
kubectl exec -it postgresql-0 -- psql -U postgres -d procurement_auth_db
```

---

## ⚠️ Security Notes

1. **Change Default Passwords**: The default password `postgres` is not secure for production
2. **Use Secrets**: Always use Kubernetes secrets for passwords
3. **Limit Access**: Only grant necessary permissions to users
4. **Backup Regularly**: Set up automated backups
5. **Monitor Logs**: Regularly check PostgreSQL logs for issues

---

**Need Help?** Check PostgreSQL documentation: https://www.postgresql.org/docs/

