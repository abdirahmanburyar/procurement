# Git Repository Setup for GitHub Actions

## 📍 Where to Initialize Git

Initialize Git in the **root directory** of the project:

```bash
# You should be in: C:\procurement (or /path/to/procurement)
cd C:\procurement

# Initialize Git repository
git init
```

## 🚀 Complete Git Setup Steps

### 1. Initialize Git Repository

```bash
# Navigate to project root
cd C:\procurement

# Initialize Git
git init

# Verify it worked
git status
```

### 2. Add All Files

```bash
# Add all files to staging
git add .

# Check what will be committed
git status
```

### 3. Create Initial Commit

```bash
# Create first commit
git commit -m "Initial commit: Procurement System with Kubernetes and CI/CD"
```

### 4. Create GitHub Repository

1. Go to [GitHub](https://github.com) and sign in
2. Click the **"+"** icon → **"New repository"**
3. Repository name: `procurement`
4. Description: "Microservices-based Procurement System"
5. Choose **Public** or **Private**
6. **DO NOT** initialize with README, .gitignore, or license (we already have these)
7. Click **"Create repository"**

### 5. Connect Local Repository to GitHub

```bash
# Add remote (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/procurement.git

# Or if using SSH:
# git remote add origin git@github.com:YOUR_USERNAME/procurement.git

# Verify remote
git remote -v
```

### 6. Push to GitHub

```bash
# Rename branch to main (if needed)
git branch -M main

# Push to GitHub
git push -u origin main
```

## 🔧 Configure GitHub Actions

### 1. Update Workflow File

Edit `.github/workflows/ci-cd.yml` and update the image registry:

```yaml
env:
  REGISTRY: ghcr.io
  IMAGE_PREFIX: YOUR_USERNAME/procurement  # Replace YOUR_USERNAME
```

### 2. Enable GitHub Container Registry

1. Go to your repository on GitHub
2. Click **Settings** → **Actions** → **General**
3. Scroll to **"Workflow permissions"**
4. Select **"Read and write permissions"**
5. Check **"Allow GitHub Actions to create and approve pull requests"**
6. Click **Save**

### 3. Add GitHub Secrets (For Deployment)

If you want automatic Kubernetes deployment:

1. Go to **Settings** → **Secrets and variables** → **Actions**
2. Click **"New repository secret"**
3. Add these secrets:

   - **Name**: `KUBECONFIG`
     - **Value**: Your base64-encoded kubeconfig file
     ```bash
     # On Linux/Mac
     cat ~/.kube/config | base64 -w 0
     
     # On Windows (PowerShell)
     [Convert]::ToBase64String([IO.File]::ReadAllBytes("$HOME\.kube\config"))
     ```

   - **Name**: `SLACK_WEBHOOK_URL` (Optional)
     - **Value**: Your Slack webhook URL for notifications

## ✅ Verify Setup

### Check Git Status

```bash
git status
```

### Check Remote

```bash
git remote -v
```

### Test Push

```bash
# Make a small change
echo "# Test" >> README.md

# Commit and push
git add README.md
git commit -m "Test commit"
git push
```

### Check GitHub Actions

1. Go to your repository on GitHub
2. Click the **"Actions"** tab
3. You should see the workflow running (or waiting for a push)

## 📝 Important Notes

### Files NOT Committed (in .gitignore)

- `target/` - Maven build artifacts
- `node_modules/` - Node.js dependencies
- `*.class` - Compiled Java files
- `*.jar` - Built JAR files (except source)
- `.env` files - Environment variables
- `secrets.yaml` - Kubernetes secrets (update before committing)
- `kubeconfig` - Kubernetes config files

### Before First Push

**IMPORTANT**: Update `k8s/config/secrets.yaml` with secure passwords before pushing:

```yaml
stringData:
  jwt-secret: "your-secure-secret-here"
  postgres-password: "secure-password"
  # ... update all passwords
```

## 🔄 Common Git Commands

```bash
# Check status
git status

# Add files
git add .
git add <specific-file>

# Commit
git commit -m "Your commit message"

# Push
git push

# Pull latest changes
git pull

# Create new branch
git checkout -b feature/new-feature

# Switch branches
git checkout main

# View commit history
git log --oneline
```

## 🚨 Troubleshooting

### "Repository not found" error

- Check your GitHub username is correct
- Verify repository exists on GitHub
- Check you have push permissions

### GitHub Actions not running

- Ensure workflow file is in `.github/workflows/` directory
- Check file is named correctly (`.yml` or `.yaml`)
- Verify workflow syntax is correct
- Check Actions tab for error messages

### Permission denied

```bash
# If using HTTPS, you may need to use a Personal Access Token
# Generate one at: https://github.com/settings/tokens
# Then use it as password when pushing

# Or switch to SSH
git remote set-url origin git@github.com:YOUR_USERNAME/procurement.git
```

## 📚 Next Steps

After Git is set up:

1. ✅ Push code to GitHub
2. ✅ Configure GitHub Actions secrets
3. ✅ Update image registry in workflow
4. ✅ Push to trigger CI/CD pipeline
5. ✅ Monitor Actions tab for build status

## 🔗 Quick Reference

- **Git init location**: Project root (`C:\procurement`)
- **GitHub repo**: Create at github.com
- **Workflow file**: `.github/workflows/ci-cd.yml`
- **Secrets location**: Repository Settings → Secrets → Actions

