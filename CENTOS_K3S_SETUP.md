# CentOS k3s Setup

## 🔍 CentOS-Specific Checks

### Step 1: Check if firewalld is Installed

```bash
# Check if firewalld package exists
rpm -qa | grep firewalld

# Check if firewalld service exists
systemctl status firewalld

# If not installed, you might be using nftables or provider firewall
```

### Step 2: Check k3s Status (Most Important)

```bash
# Check if k3s is installed and running
systemctl status k3s

# Check k3s process
ps aux | grep k3s

# Check if port 6443 is listening
netstat -tlnp | grep 6443
# OR
ss -tlnp | grep 6443
```

### Step 3: Install/Start k3s

```bash
# Install k3s
curl -sfL https://get.k3s.io | sh -

# Start k3s
systemctl start k3s

# Enable auto-start
systemctl enable k3s

# Check status
systemctl status k3s
```

### Step 4: Configure k3s for External Access

```bash
# Create config directory
mkdir -p /etc/rancher/k3s

# Create config file
cat > /etc/rancher/k3s/config.yaml << 'EOF'
bind-address: "0.0.0.0"
advertise-address: "31.97.58.62"
tls-san:
  - "31.97.58.62"
EOF

# Restart k3s
systemctl restart k3s

# Wait a moment, then check
systemctl status k3s
```

### Step 5: Check Firewall (CentOS)

```bash
# Check if firewalld is running
systemctl status firewalld

# If firewalld is running, open port
sudo firewall-cmd --permanent --add-port=6443/tcp
sudo firewall-cmd --reload
sudo firewall-cmd --list-ports

# If firewalld is not installed, check nftables
systemctl status nftables

# Or check if hosting provider manages firewall
# (Check your VPS control panel)
```

### Step 6: Test Locally

```bash
# Test kubectl on VPS
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml
kubectl cluster-info
kubectl get nodes
```

## 🎯 Quick Setup Script

Run this on your CentOS VPS:

```bash
# Install k3s
curl -sfL https://get.k3s.io | sh -

# Configure for external access
mkdir -p /etc/rancher/k3s
cat > /etc/rancher/k3s/config.yaml << 'EOF'
bind-address: "0.0.0.0"
advertise-address: "31.97.58.62"
tls-san:
  - "31.97.58.62"
EOF

# Start and enable k3s
systemctl enable k3s
systemctl restart k3s

# Wait 10 seconds
sleep 10

# Check status
systemctl status k3s

# Check if port is listening
netstat -tlnp | grep 6443

# Test kubectl
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml
kubectl cluster-info
kubectl get nodes
```

## 📝 Important Notes

1. **Hosting Provider Firewall**: Many VPS providers manage firewall in their control panel
   - Check your VPS provider's dashboard
   - Look for "Firewall", "Security Groups", or "Network Rules"
   - Add rule: Allow TCP port 6443 from anywhere (0.0.0.0/0)

2. **If firewalld is not installed**: The firewall might be:
   - Managed by hosting provider (most common)
   - Using nftables (newer CentOS)
   - Not configured (less secure)

3. **Test from outside**: After setup, test if port is accessible:
   ```bash
   # From your local machine
   telnet 31.97.58.62 6443
   ```

---

**Run `systemctl status k3s` first to see if k3s is installed and running!**

