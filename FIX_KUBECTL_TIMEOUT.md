# Fix: kubectl Connection Timeout

## 🔍 The Problem

**Error**: `dial tcp 31.97.58.62:6443: i/o timeout`

**Meaning**: kubectl is trying to connect to your VPS, but the connection is timing out.

## ✅ Good News

The KUBECONFIG secret is now correct! It's using the right IP (31.97.58.62:6443).

## 🚨 The Issue

The connection timeout means one of these:

1. **k3s is not running** on your VPS
2. **Port 6443 is blocked** by firewall
3. **k3s is not listening** on the external IP
4. **Network connectivity** issue

## 🔧 Fix Steps

### Step 1: Check if k3s is Running

**On your VPS**, run:

```bash
# Check k3s status
systemctl status k3s

# Check if k3s process is running
ps aux | grep k3s

# Check if port 6443 is listening
sudo netstat -tlnp | grep 6443
# OR
sudo ss -tlnp | grep 6443
```

### Step 2: If k3s is Not Running

Install/start k3s:

```bash
# Install k3s (if not installed)
curl -sfL https://get.k3s.io | sh -

# Start k3s
systemctl start k3s

# Enable auto-start on boot
systemctl enable k3s

# Check status
systemctl status k3s
```

### Step 3: Check Firewall

**On your VPS**, check if port 6443 is open:

```bash
# Check firewall status (if using firewalld)
sudo firewall-cmd --list-all

# Check firewall status (if using ufw)
sudo ufw status

# Check iptables
sudo iptables -L -n | grep 6443
```

**Open port 6443** if needed:

```bash
# For firewalld
sudo firewall-cmd --permanent --add-port=6443/tcp
sudo firewall-cmd --reload

# For ufw
sudo ufw allow 6443/tcp

# For iptables
sudo iptables -A INPUT -p tcp --dport 6443 -j ACCEPT
```

### Step 4: Configure k3s to Listen on External IP

k3s might be listening only on localhost. Configure it:

```bash
# Edit k3s config
sudo nano /etc/rancher/k3s/config.yaml
```

Add:
```yaml
bind-address: "0.0.0.0"
advertise-address: "31.97.58.62"
```

Then restart:
```bash
sudo systemctl restart k3s
```

### Step 5: Verify from VPS

**On your VPS**, test locally:

```bash
# Test kubectl locally
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml
kubectl cluster-info
kubectl get nodes
```

If this works locally but not from GitHub Actions, it's a firewall/network issue.

## 🧪 Quick Test

**From your local machine** (not VPS), test if port 6443 is accessible:

```bash
# Test if port is open
telnet 31.97.58.62 6443
# OR
nc -zv 31.97.58.62 6443
# OR
curl -k https://31.97.58.62:6443
```

If these fail, the port is blocked or k3s is not running.

## 📝 Common Solutions

### Solution 1: k3s Not Running
```bash
sudo systemctl start k3s
sudo systemctl enable k3s
```

### Solution 2: Firewall Blocking
```bash
sudo firewall-cmd --permanent --add-port=6443/tcp
sudo firewall-cmd --reload
```

### Solution 3: k3s Not Listening on External IP
Edit `/etc/rancher/k3s/config.yaml`:
```yaml
bind-address: "0.0.0.0"
advertise-address: "31.97.58.62"
```
Then: `sudo systemctl restart k3s`

## ✅ Verification

After fixing, test from VPS:

```bash
# On VPS
kubectl cluster-info
kubectl get nodes
```

Then push to GitHub and check if deployment works!

---

**Run these diagnostics on your VPS and share the results!**

