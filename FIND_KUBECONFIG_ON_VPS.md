# Finding kubeconfig on Your VPS

## 🔍 Where to Find kubeconfig

### Step 1: Connect to Your VPS

```bash
ssh root@31.97.58.62
# Password: Buryar@2020#
```

### Step 2: Check if k3s is Installed

```bash
# Check if k3s service exists
systemctl status k3s

# Or check if k3s is running
ps aux | grep k3s
```

### Step 3: Find kubeconfig Location

Run these commands to find the kubeconfig:

```bash
# Option 1: Standard k3s location
ls -la /etc/rancher/k3s/k3s.yaml

# Option 2: Check if kubectl works (this will show config location)
kubectl config view

# Option 3: Get kubectl config directly
kubectl config view --raw

# Option 4: Check common locations
ls -la ~/.kube/config
ls -la /root/.kube/config

# Option 5: Find all k3s.yaml files
find / -name "k3s.yaml" 2>/dev/null

# Option 6: Find all config files in .kube directories
find / -path "*/.kube/config" 2>/dev/null
```

### Step 4: If k3s is Not Installed

If k3s is not installed, install it first:

```bash
# Install k3s
curl -sfL https://get.k3s.io | sh -

# Wait for installation (check status)
systemctl status k3s

# Once running, get the kubeconfig
sudo cat /etc/rancher/k3s/k3s.yaml
```

### Step 5: If kubectl Works

If `kubectl` command works, you can get the config directly:

```bash
# Get the config
kubectl config view --raw

# Or save it to a file
kubectl config view --raw > kubeconfig.yaml
```

## 📝 Once You Find It

After finding the kubeconfig file:

1. **Update the server IP** (replace localhost with your VPS IP):
   ```bash
   cat /etc/rancher/k3s/k3s.yaml | sed 's/127.0.0.1/31.97.58.62/g' | sed 's/localhost/31.97.58.62/g' > kubeconfig.yaml
   ```

2. **Base64 encode it**:
   ```bash
   cat kubeconfig.yaml | base64 -w 0
   ```

3. **Copy the output** and add it as GitHub secret `KUBECONFIG`

## 🚨 Quick Diagnostic Commands

Run these on your VPS to diagnose:

```bash
# 1. Check k3s status
systemctl status k3s

# 2. Check if kubectl works
kubectl version --client
kubectl cluster-info

# 3. Find kubeconfig
find / -name "k3s.yaml" 2>/dev/null
find / -name "config" -path "*/.kube/*" 2>/dev/null

# 4. Check if kubectl can get config
kubectl config view --raw
```

---

**Run these commands on your VPS and share the output!**

