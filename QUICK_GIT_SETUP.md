# Quick Git Setup for Procurement Repository

## 🚀 Fast Setup (2 Minutes)

### 1. Initialize Git

```bash
# In project root: C:\procurement
git init
```

### 2. Add and Commit

```bash
git add .
git commit -m "Initial commit: Procurement System"
```

### 3. Create GitHub Repository

1. Go to https://github.com/new
2. Repository name: **`procurement`**
3. Description: "Microservices-based Procurement System"
4. Choose Public or Private
5. **DO NOT** check any initialization options
6. Click **"Create repository"**

### 4. Connect and Push

```bash
# Add remote (replace YOUR_USERNAME)
git remote add origin https://github.com/abdirahmanburyar/procurement.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## ✅ Done!

Your repository is now at: `https://github.com/abdirahmanburyar/procurement`

GitHub Actions will automatically run on the next push!

## 📝 Before First Push

**IMPORTANT**: Update `k8s/config/secrets.yaml` with secure passwords:

```yaml
stringData:
  jwt-secret: "your-secure-secret-here"
  postgres-password: "secure-password"
```

## 🔧 Update Workflow (Optional)

Edit `.github/workflows/ci-cd.yml` if you want to change the image registry path.

