# How to Configure VPS Connection for GitHub Actions

## 🎯 Quick Answer

You need to add your VPS connection details as **GitHub Secrets**. The workflow will use these to connect to your CentOS VPS.

## 📍 Step-by-Step Setup

### 1. Get Your VPS IP Address

On your VPS, run:
```bash
hostname -I
# or
ip addr show
```

**Note your VPS IP** (e.g., `192.168.1.100` or `your-domain.com`)

### 2. Install k3s on Your CentOS VPS

SSH into your VPS and run:

```bash
# Install k3s
curl -sfL https://get.k3s.io | sh -

# Verify it's running
sudo kubectl get nodes
```

### 3. Get kubeconfig from VPS

On your VPS:

```bash
# Get kubeconfig
sudo cat /etc/rancher/k3s/k3s.yaml

# IMPORTANT: Replace 127.0.0.1 with your VPS IP
# Change: server: https://127.0.0.1:6443
# To:     server: https://YOUR_VPS_IP:6443
```

### 4. Encode kubeconfig to Base64

**On your local machine or VPS:**

```bash
# Linux/Mac
cat ~/.kube/config | base64 -w 0

# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$HOME\.kube\config"))
```

**Copy the entire base64 string** (it will be very long)

### 5. Add Secret to GitHub

1. Go to: **https://github.com/abdirahmanburyar/procurement/settings/secrets/actions**
2. Click **"New repository secret"**
3. **Name**: `KUBECONFIG`
4. **Value**: Paste the base64 encoded kubeconfig
5. Click **"Add secret"**

### 6. Configure Firewall on VPS

```bash
# Open Kubernetes API port
sudo firewall-cmd --permanent --add-port=6443/tcp
sudo firewall-cmd --reload
```

## ✅ That's It!

After adding the `KUBECONFIG` secret:
- GitHub Actions will automatically connect to your VPS
- Deploy services to Kubernetes
- No need to manually specify VPS address in workflow

## 🔍 How It Works

The workflow (`.github/workflows/ci-cd.yml`) already has this code:

```yaml
- name: Configure kubectl
  run: |
    echo "${{ secrets.KUBECONFIG }}" | base64 -d > kubeconfig
    export KUBECONFIG=./kubeconfig
```

It:
1. Reads `KUBECONFIG` secret from GitHub
2. Decodes it (base64 → kubeconfig file)
3. Uses it to connect to your VPS Kubernetes cluster

## 📝 Complete VPS Setup

See `VPS_SETUP.md` for complete instructions including:
- k3s installation
- NGINX Ingress setup
- Firewall configuration
- Troubleshooting

## 🚀 Next Steps

1. ✅ Install k3s on your CentOS VPS
2. ✅ Get kubeconfig with VPS IP (not localhost)
3. ✅ Encode to base64
4. ✅ Add as GitHub secret: `KUBECONFIG`
5. ✅ Push to GitHub - deployment will happen automatically!

---

**The VPS address is in the kubeconfig file, not in the workflow!** 🎯

