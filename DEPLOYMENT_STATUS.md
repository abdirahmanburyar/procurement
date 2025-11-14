# Deployment Status & Monitoring

## 🚀 What Happens When You Push

When you push to GitHub, the CI/CD pipeline automatically:

### 1. Build Stage (Runs on every push)
- ✅ Builds all Spring Boot services
- ✅ Runs unit tests
- ✅ Builds React frontend
- ✅ Creates build artifacts

### 2. Docker Stage (Runs on push to main/develop)
- ✅ Builds Docker images for all 9 services
- ✅ Tags images with commit SHA
- ✅ Pushes to GitHub Container Registry (ghcr.io)

### 3. Deploy Stage (Runs on push to main branch)
- ✅ Connects to your VPS using KUBECONFIG secret
- ✅ Deploys PostgreSQL StatefulSet
- ✅ Deploys all microservices
- ✅ Applies Ingress rules
- ✅ Waits for all services to be ready

## 📊 How to Check Deployment Status

### Check GitHub Actions

1. **Go to**: https://github.com/abdirahmanburyar/procurement/actions
2. **Click** on the latest workflow run
3. **View** each job status:
   - 🟡 Yellow = Running
   - ✅ Green = Success
   - ❌ Red = Failed

### Check Deployment Job

In the workflow run, look for:
- **Job**: "Deploy to Kubernetes"
- **Status**: Should show if it's running or completed

### Check Your VPS

SSH into your VPS and check:

```bash
ssh root@31.97.58.62

# Check if services are deployed
kubectl get pods -n procurement

# Check all resources
kubectl get all -n procurement

# Check deployments
kubectl get deployments -n procurement

# View logs
kubectl logs -f deployment/api-gateway -n procurement
```

## ✅ Success Indicators

You'll know deployment worked when:

1. **GitHub Actions**:
   - ✅ All jobs show green checkmarks
   - ✅ "Deploy to Kubernetes" job completed successfully

2. **On Your VPS**:
   ```bash
   kubectl get pods -n procurement
   ```
   Should show pods running:
   - postgresql-0
   - eureka-server-*
   - config-server-*
   - api-gateway-*
   - auth-service-*
   - procurement-service-*
   - quotation-service-*
   - purchase-order-service-*
   - inventory-service-*
   - frontend-*

3. **Services Available**:
   - Frontend: http://31.97.58.62 (or your domain)
   - API Gateway: http://31.97.58.62/api
   - Eureka Dashboard: Port-forward required

## 🔍 Troubleshooting

### Deployment Job Not Running

**Check:**
- Did you push to `main` branch? (Deploy only runs on main)
- Is `KUBECONFIG` secret added to GitHub?
- Check workflow file: `.github/workflows/ci-cd.yml`

### Deployment Fails

**Common issues:**

1. **Cannot connect to VPS**
   ```bash
   # On VPS, check k3s is running
   systemctl status k3s
   
   # Check firewall
   firewall-cmd --list-ports
   ```

2. **Image pull errors**
   - Check images exist: https://github.com/abdirahmanburyar/procurement/pkgs
   - Verify image names in `k8s/services/*.yaml` match your registry

3. **PostgreSQL not ready**
   ```bash
   kubectl describe pod postgresql-0 -n procurement
   kubectl logs postgresql-0 -n procurement
   ```

### View Detailed Logs

**In GitHub Actions:**
1. Click on failed job
2. Expand error messages
3. Check each step output

**On VPS:**
```bash
# View pod events
kubectl describe pod <pod-name> -n procurement

# View logs
kubectl logs <pod-name> -n procurement

# View all events
kubectl get events -n procurement --sort-by='.lastTimestamp'
```

## 📝 Current Deployment Status

To check right now:

1. **GitHub Actions**: https://github.com/abdirahmanburyar/procurement/actions
   - Look for latest workflow run
   - Check if "Deploy to Kubernetes" job is running/completed

2. **VPS Status**:
   ```bash
   ssh root@31.97.58.62
   kubectl get pods -n procurement
   ```

## 🎯 What to Do Now

1. ✅ **Check GitHub Actions**: See if workflow is running
2. ✅ **Wait for completion**: First deployment takes 5-10 minutes
3. ✅ **Verify on VPS**: Check if pods are running
4. ✅ **Test services**: Try accessing the frontend/API

## 🔄 Automatic Deployment

**Yes, it's automatic!** Every time you:
- Push to `main` branch
- The workflow will:
  1. Build everything
  2. Create Docker images
  3. Deploy to your VPS automatically

**No manual steps needed** after initial setup! 🎉

---

**Check your deployment**: https://github.com/abdirahmanburyar/procurement/actions

