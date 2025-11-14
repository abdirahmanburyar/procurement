# Quick Setup: KUBECONFIG Secret

## 🚨 Current Error

```
ERROR: KUBECONFIG secret is not set!
```

This means you need to add the KUBECONFIG secret to GitHub.

## ✅ Quick Fix (5 minutes)

### Step 1: Connect to Your VPS

```bash
ssh root@31.97.58.62
# Password: Buryar@2020#
```

### Step 2: Get the kubeconfig File

```bash
# For k3s, the config is usually here:
cat /etc/rancher/k3s/k3s.yaml
```

### Step 3: Update Server IP

The kubeconfig will have `127.0.0.1` or `localhost`. Replace it with your VPS IP:

```bash
# Create a copy and replace localhost with VPS IP
cat /etc/rancher/k3s/k3s.yaml | sed 's/127.0.0.1/31.97.58.62/g' > kubeconfig.yaml
cat /etc/rancher/k3s/k3s.yaml | sed 's/localhost/31.97.58.62/g' > kubeconfig.yaml

# View the updated file
cat kubeconfig.yaml
```

**Important**: Check that the `server:` line shows:
```yaml
server: https://31.97.58.62:6443
```

### Step 4: Base64 Encode

```bash
# On the VPS, encode it
cat kubeconfig.yaml | base64 -w 0
```

**Copy the entire output** (it's a long string, make sure you get it all!)

### Step 5: Add to GitHub Secrets

1. Go to: https://github.com/abdirahmanburyar/procurement/settings/secrets/actions
2. Click **"New repository secret"**
3. **Name**: `KUBECONFIG`
4. **Value**: Paste the base64 encoded string (from Step 4)
5. Click **"Add secret"**

### Step 6: Verify

1. Push a commit to trigger the workflow
2. Check the "Configure kubectl" step
3. Should see: `Verifying kubectl connection...` ✅

## 🔧 Alternative: If You Have kubeconfig Locally

If you already have kubeconfig on your local machine:

### On Windows (PowerShell):

```powershell
# Read the file
$content = Get-Content ~\.kube\config -Raw

# Replace localhost with VPS IP
$content = $content -replace '127.0.0.1', '31.97.58.62'
$content = $content -replace 'localhost', '31.97.58.62'

# Base64 encode
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($content))
```

### On Windows (Git Bash):

```bash
# Read and replace
cat ~/.kube/config | sed 's/127.0.0.1/31.97.58.62/g' | sed 's/localhost/31.97.58.62/g' | base64 -w 0
```

### On Linux/Mac:

```bash
cat ~/.kube/config | sed 's/127.0.0.1/31.97.58.62/g' | sed 's/localhost/31.97.58.62/g' | base64 -w 0
```

## ✅ What the kubeconfig Should Look Like

After updating, it should have:

```yaml
apiVersion: v1
clusters:
- cluster:
    server: https://31.97.58.62:6443  # ✅ Your VPS IP
    certificate-authority-data: LS0tLS1CRUdJTi...
  name: default
contexts:
- context:
    cluster: default
    user: default
  name: default
current-context: default
kind: Config
users:
- name: default
  user:
    client-certificate-data: LS0tLS1CRUdJTi...
    client-key-data: LS0tLS1CRUdJTi...
```

## 🚨 Common Issues

### Issue: "Connection refused" after setting secret

**Fix**: 
- Verify k3s is running: `sudo systemctl status k3s` on VPS
- Check firewall allows port 6443
- Verify server IP in kubeconfig is correct

### Issue: "Failed to decode KUBECONFIG"

**Fix**:
- Ensure no line breaks in the base64 string
- Copy the entire output (it's one long string)
- Try encoding again

### Issue: "Authentication failed"

**Fix**:
- Verify the kubeconfig file is correct
- Check that certificate data is included
- Ensure you're using the k3s.yaml file, not a modified version

## 📝 Quick Test

After setting the secret, test locally first:

```bash
# Export kubeconfig
export KUBECONFIG=~/.kube/config

# Test connection
kubectl cluster-info
kubectl get nodes
```

If this works, your kubeconfig is correct!

---

**Once set, the deployment will work!** 🎉

