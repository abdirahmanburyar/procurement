# Check VPS Firewall and k3s

## 🔍 Your VPS doesn't have firewalld/iptables

This means either:
- Using a different firewall (nftables, cloud provider firewall)
- Firewall managed by hosting provider
- No firewall configured

## ✅ Check These Instead

### Step 1: Check if k3s is Running

```bash
# Check k3s status
systemctl status k3s

# Check if k3s process exists
ps aux | grep k3s

# Check if port 6443 is listening
netstat -tlnp | grep 6443
# OR
ss -tlnp | grep 6443
```

### Step 2: Check What's Listening on Port 6443

```bash
# See what's listening on 6443
netstat -tlnp | grep 6443
ss -tlnp | grep 6443

# Check if it's listening on all interfaces (0.0.0.0) or just localhost (127.0.0.1)
```

### Step 3: Check Cloud Provider Firewall

If you're using a cloud VPS (like Hetzner, DigitalOcean, AWS, etc.):
- Check the **cloud provider's firewall/security group settings**
- Make sure port **6443** is open for **inbound traffic**
- This is usually in the VPS control panel

### Step 4: Install/Start k3s if Needed

```bash
# Check if k3s is installed
which k3s

# If not installed, install it
curl -sfL https://get.k3s.io | sh -

# Start k3s
systemctl start k3s
systemctl enable k3s

# Check status
systemctl status k3s
```

### Step 5: Configure k3s to Listen on External IP

```bash
# Create config directory
mkdir -p /etc/rancher/k3s

# Create/edit config file
nano /etc/rancher/k3s/config.yaml
```

Add this content:
```yaml
bind-address: "0.0.0.0"
advertise-address: "31.97.58.62"
tls-san:
  - "31.97.58.62"
```

Then restart:
```bash
systemctl restart k3s
systemctl status k3s
```

### Step 6: Test Locally on VPS

```bash
# Test kubectl on the VPS itself
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml
kubectl cluster-info
kubectl get nodes
```

## 🎯 Most Important Checks

1. **Is k3s running?** → `systemctl status k3s`
2. **Is port 6443 listening?** → `netstat -tlnp | grep 6443`
3. **Is it listening on 0.0.0.0 or 127.0.0.1?** → Check the output
4. **Cloud provider firewall?** → Check VPS control panel

---

**Run `systemctl status k3s` first and share the output!**

