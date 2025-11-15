# How to Access the Frontend

## 🌐 Access Methods

### Method 1: Via Traefik Ingress (Recommended)

Traefik is running in your cluster. Access the frontend through Traefik:

**On your VPS**, check Traefik service port:
```bash
kubectl get svc traefik -n kube-system
```

Then access:
- **Frontend**: `http://31.97.58.62:<TRAEFIK_PORT>/`
- **API**: `http://31.97.58.62:<TRAEFIK_PORT>/api`

### Method 2: Via NodePort (Direct Access)

The frontend service is now configured as NodePort on port **30080**.

**Access directly:**
- **Frontend**: `http://31.97.58.62:30080`

### Method 3: Port Forward (For Testing)

**On your VPS**, run:
```bash
kubectl port-forward svc/frontend 8080:80 -n procurement
```

Then access: `http://localhost:8080`

## 🔍 Check Current Setup

**On your VPS**, run:

```bash
# Check Traefik service port
kubectl get svc traefik -n kube-system

# Check frontend service
kubectl get svc frontend -n procurement

# Check ingress
kubectl get ingress -n procurement

# Check Traefik ingress routes
kubectl get ingressroute -A
```

## 📝 Quick Access Commands

**From your local machine:**

```bash
# Method 1: Direct NodePort
curl http://31.97.58.62:30080

# Method 2: Via Traefik (check port first)
# Get Traefik port:
kubectl get svc traefik -n kube-system -o jsonpath='{.spec.ports[0].nodePort}'
# Then access: http://31.97.58.62:<PORT>
```

## 🚨 If Frontend Not Accessible

1. **Check if frontend pod is running:**
   ```bash
   kubectl get pods -n procurement | grep frontend
   ```

2. **Check frontend service:**
   ```bash
   kubectl get svc frontend -n procurement
   ```

3. **Check ingress:**
   ```bash
   kubectl get ingress -n procurement
   kubectl describe ingress procurement-ingress -n procurement
   ```

4. **Check Traefik logs:**
   ```bash
   kubectl logs -n kube-system -l app.kubernetes.io/name=traefik
   ```

---

**Try accessing: `http://31.97.58.62:30080` first!**

