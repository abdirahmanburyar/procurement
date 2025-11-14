# VPS Setup Guide for Kubernetes Deployment

## 🖥️ Your VPS Details

- **OS**: CentOS
- **Requirements**: 2 vCPU, 8 GB RAM (minimum)
- **Purpose**: Run Kubernetes cluster (k3s) and deploy services

## 📋 Step 1: Prepare Your VPS

### Connect to Your VPS

```bash
# SSH into your VPS
ssh root@YOUR_VPS_IP
# or
ssh your-username@YOUR_VPS_IP
```

### Update System

```bash
# Update CentOS
sudo yum update -y

# Install essential tools
sudo yum install -y curl wget git vim
```

## 🚀 Step 2: Install k3s (Lightweight Kubernetes)

### Install k3s

```bash
# Install k3s (single-node cluster)
curl -sfL https://get.k3s.io | sh -

# Verify installation
sudo kubectl get nodes
```

### Configure kubectl for Non-Root User

```bash
# Copy kubeconfig to your home directory
mkdir -p ~/.kube
sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
sudo chown $USER ~/.kube/config

# Test kubectl
kubectl get nodes
```

### Install NGINX Ingress Controller

```bash
# Install NGINX Ingress
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

# Wait for it to be ready
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s
```

## 🔐 Step 3: Get kubeconfig for GitHub Actions

### Option A: Export kubeconfig (Recommended)

On your VPS, run:

```bash
# View the kubeconfig
cat ~/.kube/config

# Or if using root:
sudo cat /etc/rancher/k3s/k3s.yaml
```

**Important**: Replace `127.0.0.1` or `localhost` with your VPS IP address:

```yaml
# Change this line in kubeconfig:
server: https://127.0.0.1:6443

# To your VPS IP:
server: https://YOUR_VPS_IP:6443
```

### Option B: Create kubeconfig with VPS IP

```bash
# On your VPS, create a kubeconfig with your IP
VPS_IP="YOUR_VPS_IP"
sudo cat /etc/rancher/k3s/k3s.yaml | sed "s/127.0.0.1/$VPS_IP/g" > ~/kubeconfig-vps.yaml

# View it
cat ~/kubeconfig-vps.yaml
```

## 🔑 Step 4: Add GitHub Secrets

### Get Base64 Encoded kubeconfig

**On your local machine or VPS:**

```bash
# Linux/Mac
cat ~/.kube/config | base64 -w 0

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$HOME\.kube\config"))

# Or if you created kubeconfig-vps.yaml on VPS:
cat ~/kubeconfig-vps.yaml | base64 -w 0
```

### Add to GitHub Secrets

1. Go to: https://github.com/abdirahmanburyar/procurement/settings/secrets/actions
2. Click **"New repository secret"**
3. Add these secrets:

   **Secret 1: KUBECONFIG**
   - **Name**: `KUBECONFIG`
   - **Value**: Paste the base64 encoded kubeconfig
   - Click **"Add secret"**

   **Secret 2: VPS_IP** (Optional, for reference)
   - **Name**: `VPS_IP`
   - **Value**: Your VPS IP address (e.g., `192.168.1.100` or `your-domain.com`)
   - Click **"Add secret"**

   **Secret 3: SLACK_WEBHOOK_URL** (Optional)
   - **Name**: `SLACK_WEBHOOK_URL`
   - **Value**: Your Slack webhook URL (if you want notifications)

## 🔥 Step 5: Configure Firewall

### Open Required Ports on CentOS

```bash
# Check if firewalld is running
sudo systemctl status firewalld

# If running, open ports
sudo firewall-cmd --permanent --add-port=6443/tcp  # Kubernetes API
sudo firewall-cmd --permanent --add-port=80/tcp    # HTTP
sudo firewall-cmd --permanent --add-port=443/tcp   # HTTPS
sudo firewall-cmd --permanent --add-port=10250/tcp # Kubelet
sudo firewall-cmd --reload

# Or disable firewall (NOT recommended for production)
# sudo systemctl stop firewalld
# sudo systemctl disable firewalld
```

### If Using Cloud Provider

- **AWS**: Configure Security Groups
- **DigitalOcean**: Configure Firewall Rules
- **Linode**: Configure Firewall
- **Hetzner**: Configure Firewall

Open ports:
- **6443** (Kubernetes API)
- **80** (HTTP)
- **443** (HTTPS)
- **10250** (Kubelet)

## ✅ Step 6: Verify VPS Setup

### Test kubectl from Local Machine

```bash
# Copy kubeconfig to your local machine
scp user@YOUR_VPS_IP:~/.kube/config ~/.kube/config-vps

# Test connection
export KUBECONFIG=~/.kube/config-vps
kubectl get nodes
kubectl get pods --all-namespaces
```

### Test from GitHub Actions

After adding secrets, the workflow will automatically:
1. Decode KUBECONFIG
2. Connect to your VPS
3. Deploy services

## 📝 Step 7: Update CI/CD Workflow

The workflow (`.github/workflows/ci-cd.yml`) is already configured to:
- Use `KUBECONFIG` secret
- Deploy to Kubernetes
- Apply all manifests

**No changes needed** - just make sure secrets are added!

## 🚀 Step 8: Deploy!

### Automatic Deployment

After you push to `main` branch:
1. GitHub Actions builds Docker images
2. Pushes images to GitHub Container Registry
3. Connects to your VPS using KUBECONFIG
4. Deploys all services to Kubernetes

### Manual Deployment (Alternative)

If you want to deploy manually:

```bash
# On your VPS or local machine with kubeconfig
kubectl create namespace procurement
kubectl apply -f k8s/postgresql/ -n procurement
kubectl apply -f k8s/config/ -n procurement
kubectl apply -f k8s/services/ -n procurement
kubectl apply -f k8s/ingress/ -n procurement
```

## 🔍 Troubleshooting

### Cannot Connect to VPS

**Check:**
```bash
# Test SSH access
ssh user@YOUR_VPS_IP

# Test Kubernetes API
curl -k https://YOUR_VPS_IP:6443
```

### kubectl Connection Fails

**Verify kubeconfig:**
```bash
# Check server address in kubeconfig
cat ~/.kube/config | grep server

# Should be: https://YOUR_VPS_IP:6443
# NOT: https://127.0.0.1:6443
```

### Firewall Blocking

```bash
# Check firewall status
sudo firewall-cmd --list-all

# Check if ports are open
sudo netstat -tulpn | grep 6443
```

### GitHub Actions Can't Connect

**Check:**
1. KUBECONFIG secret is base64 encoded correctly
2. Server IP in kubeconfig matches your VPS IP
3. Firewall allows port 6443
4. k3s is running: `sudo systemctl status k3s`

## 📊 Verify Deployment

After deployment:

```bash
# Check all pods
kubectl get pods -n procurement

# Check services
kubectl get svc -n procurement

# Check ingress
kubectl get ingress -n procurement

# View logs
kubectl logs -f deployment/api-gateway -n procurement
```

## 🎯 Quick Checklist

- [ ] VPS has CentOS installed
- [ ] k3s installed and running
- [ ] NGINX Ingress installed
- [ ] Firewall configured (ports 6443, 80, 443)
- [ ] kubeconfig exported with VPS IP (not localhost)
- [ ] kubeconfig base64 encoded
- [ ] KUBECONFIG secret added to GitHub
- [ ] Tested kubectl connection from local machine
- [ ] Ready for GitHub Actions deployment!

## 🔗 Useful Commands

```bash
# Check k3s status
sudo systemctl status k3s

# View k3s logs
sudo journalctl -u k3s -f

# Restart k3s
sudo systemctl restart k3s

# Get VPS IP
ip addr show
# or
hostname -I
```

---

**Next**: Add KUBECONFIG secret to GitHub, then push to trigger deployment! 🚀

