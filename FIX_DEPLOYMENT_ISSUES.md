# Fixed Deployment Issues

## ✅ What Was Updated

### 1. Image Registry References

Updated all Kubernetes service manifests to use your GitHub username:

**Changed:**
- `ghcr.io/YOUR_USERNAME/procurement-*` 
- **To:** `ghcr.io/abdirahmanburyar/procurement-*`

**Files Updated:**
- ✅ `k8s/services/eureka-server.yaml`
- ✅ `k8s/services/config-server.yaml`
- ✅ `k8s/services/api-gateway.yaml`
- ✅ `k8s/services/auth-service.yaml`
- ✅ `k8s/services/procurement-service.yaml`
- ✅ `k8s/services/quotation-service.yaml`
- ✅ `k8s/services/purchase-order-service.yaml`
- ✅ `k8s/services/inventory-service.yaml`
- ✅ `k8s/services/frontend.yaml`

### 2. GitHub Actions Workflow

Updated workflow to:
- Use commit SHA for image tags (better versioning)
- Automatically update manifests before deployment

### 3. VPS IP Address

**Important**: The VPS IP (`31.97.58.62`) should be in your kubeconfig file, not in the manifests.

**To verify kubeconfig has correct IP:**

1. SSH to your VPS:
   ```bash
   ssh root@31.97.58.62
   ```

2. Check kubeconfig:
   ```bash
   cat ~/.kube/config | grep server
   ```

3. Should show:
   ```yaml
   server: https://31.97.58.62:6443
   ```

4. If it shows `127.0.0.1`, update it:
   ```bash
   sed -i 's/127.0.0.1/31.97.58.62/g' ~/.kube/config
   ```

5. Re-encode and update GitHub secret:
   ```bash
   cat ~/.kube/config | base64 -w 0
   ```
   Then update the `KUBECONFIG` secret in GitHub.

## 🔍 Common GitHub Actions Failures

### Issue 1: Image Pull Errors

**Error**: `ImagePullBackOff` or `ErrImagePull`

**Solution**: 
- ✅ Images now use correct registry: `ghcr.io/abdirahmanburyar/procurement-*`
- Make sure Docker images were built successfully in previous job
- Check: https://github.com/abdirahmanburyar/procurement/pkgs

### Issue 2: Cannot Connect to VPS

**Error**: `Unable to connect to the server` or `connection refused`

**Solution**:
1. Verify kubeconfig has VPS IP (not localhost)
2. Check k3s is running on VPS:
   ```bash
   ssh root@31.97.58.62
   systemctl status k3s
   ```
3. Check firewall allows port 6443:
   ```bash
   firewall-cmd --list-ports
   ```

### Issue 3: Namespace Not Found

**Error**: `namespaces "procurement" not found`

**Solution**: The workflow creates it automatically, but you can create manually:
```bash
kubectl create namespace procurement
```

### Issue 4: PostgreSQL Not Ready

**Error**: `Timeout waiting for PostgreSQL`

**Solution**:
1. Check PostgreSQL pod:
   ```bash
   kubectl get pods -n procurement
   kubectl describe pod postgresql-0 -n procurement
   ```
2. Check PVC:
   ```bash
   kubectl get pvc -n procurement
   ```

## ✅ Verification Steps

### 1. Check GitHub Actions

1. Go to: https://github.com/abdirahmanburyar/procurement/actions
2. Check latest workflow run
3. Verify:
   - ✅ Build job completed
   - ✅ Docker build job completed
   - ✅ Deploy job running/completed

### 2. Check Docker Images

1. Go to: https://github.com/abdirahmanburyar/procurement/pkgs
2. Verify all 9 images exist:
   - procurement-eureka-server
   - procurement-config-server
   - procurement-api-gateway
   - procurement-auth-service
   - procurement-procurement-service
   - procurement-quotation-service
   - procurement-purchase-order-service
   - procurement-inventory-service
   - procurement-frontend

### 3. Check VPS Deployment

SSH to VPS and run:

```bash
ssh root@31.97.58.62

# Check pods
kubectl get pods -n procurement

# Check services
kubectl get svc -n procurement

# Check deployments
kubectl get deployments -n procurement

# View logs if errors
kubectl logs -f deployment/api-gateway -n procurement
```

## 🚀 Next Steps

1. **Commit and push these changes:**
   ```bash
   git add k8s/services/*.yaml .github/workflows/ci-cd.yml
   git commit -m "Fix: Update image registry to use correct GitHub username"
   git push
   ```

2. **Monitor GitHub Actions:**
   - Watch the workflow run
   - Check if deployment succeeds

3. **Verify on VPS:**
   - SSH and check pods are running
   - Test services are accessible

## 📝 Summary of Changes

- ✅ All image references updated to `ghcr.io/abdirahmanburyar/procurement-*`
- ✅ Workflow updated to use commit SHA for image tags
- ✅ Deployment script updated
- ✅ VPS IP should be in kubeconfig (not in manifests)

**The VPS IP (31.97.58.62) is configured in your kubeconfig file, which is stored as a GitHub secret. The manifests don't need the IP address - Kubernetes uses the kubeconfig to connect.**

---

**After pushing these changes, the deployment should work!** 🎉

