# VPS Setup Commands for Your Server

## 🖥️ Your VPS Details

- **IP Address**: `31.97.58.62`
- **User**: `root`
- **OS**: CentOS

## 🚀 Step-by-Step Setup Commands

### Step 1: Connect to Your VPS

```bash
ssh root@31.97.58.62
# Enter password when prompted: Buryar@2020#
```

### Step 2: Update System

```bash
# Update CentOS
yum update -y

# Install essential tools
yum install -y curl wget git vim
```

### Step 3: Install k3s (Kubernetes)

```bash
# Install k3s
curl -sfL https://get.k3s.io | sh -

# Verify installation
k3s kubectl get nodes
```

**Expected output:**
```
NAME              STATUS   ROLES                  AGE   VERSION
your-hostname     Ready    control-plane,master   30s   v1.x.x+k3s1
```

### Step 4: Configure kubectl

```bash
# Create .kube directory
mkdir -p ~/.kube

# Copy kubeconfig
cp /etc/rancher/k3s/k3s.yaml ~/.kube/config

# Set permissions
chmod 600 ~/.kube/config

# Test kubectl
kubectl get nodes
```

### Step 5: Update kubeconfig with VPS IP

**IMPORTANT**: Replace localhost with your VPS IP address

```bash
# Edit kubeconfig
vi ~/.kube/config
```

**Find this line:**
```yaml
server: https://127.0.0.1:6443
```

**Change to:**
```yaml
server: https://31.97.58.62:6443
```

**Save and exit** (in vi: press `Esc`, type `:wq`, press `Enter`)

### Step 6: Verify kubeconfig

```bash
# Test connection
kubectl cluster-info
kubectl get nodes
```

### Step 7: Install NGINX Ingress Controller

```bash
# Install NGINX Ingress
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

# Wait for it to be ready (takes 1-2 minutes)
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s

# Verify ingress is running
kubectl get pods -n ingress-nginx
```

### Step 8: Configure Firewall

```bash
# Check if firewalld is running
systemctl status firewalld

# If running, open required ports
firewall-cmd --permanent --add-port=6443/tcp  # Kubernetes API
firewall-cmd --permanent --add-port=80/tcp    # HTTP
firewall-cmd --permanent --add-port=443/tcp   # HTTPS
firewall-cmd --permanent --add-port=10250/tcp # Kubelet
firewall-cmd --reload

# Verify ports are open
firewall-cmd --list-ports
```

**Alternative**: If you're using a cloud provider firewall (AWS, DigitalOcean, etc.), configure it in their dashboard:
- Port **6443** (Kubernetes API)
- Port **80** (HTTP)
- Port **443** (HTTPS)

### Step 9: Get kubeconfig for GitHub

```bash
# View kubeconfig (should have your IP: 31.97.58.62)
cat ~/.kube/config

# Copy the entire output
```

**Verify it has your IP:**
```yaml
server: https://31.97.58.62:6443
```

### Step 10: Encode kubeconfig to Base64

**On your VPS**, run:

```bash
# Encode to base64
cat ~/.kube/config | base64 -w 0
```

**Copy the entire base64 string** (it will be very long, one line)

### Step 11: Add to GitHub Secrets

1. Go to: **https://github.com/abdirahmanburyar/procurement/settings/secrets/actions**
2. Click **"New repository secret"**
3. **Name**: `KUBECONFIG`
4. **Value**: Paste the base64 encoded kubeconfig
5. Click **"Add secret"**

## ✅ Verification Commands

Run these to verify everything is set up:

```bash
# Check k3s is running
systemctl status k3s

# Check kubectl works
kubectl get nodes
kubectl get pods --all-namespaces

# Check ingress is ready
kubectl get pods -n ingress-nginx

# Check firewall ports
firewall-cmd --list-ports
```

## 🔍 Troubleshooting

### k3s not starting

```bash
# Check k3s logs
journalctl -u k3s -f

# Restart k3s
systemctl restart k3s
```

### Cannot connect from outside

```bash
# Check if port 6443 is listening
netstat -tulpn | grep 6443

# Test from VPS itself
curl -k https://31.97.58.62:6443
```

### Firewall blocking

```bash
# Temporarily disable firewall to test (NOT for production)
systemctl stop firewalld

# Or check firewall rules
firewall-cmd --list-all
```

## 📝 Quick Reference

**Your VPS IP**: `31.97.58.62`  
**kubeconfig location**: `~/.kube/config`  
**k3s config**: `/etc/rancher/k3s/k3s.yaml`

## 🎯 Next Steps After Setup

1. ✅ k3s installed and running
2. ✅ kubeconfig updated with VPS IP
3. ✅ NGINX Ingress installed
4. ✅ Firewall configured
5. ✅ kubeconfig base64 encoded
6. ✅ Added to GitHub as `KUBECONFIG` secret
7. 🚀 Push to GitHub - deployment will happen automatically!

---

**Security Note**: Never commit passwords or kubeconfig files to Git!

