# Procurement System - Microservices Architecture

A comprehensive microservices-based procurement system built with Spring Boot 3.3.4 and React.

## 🏗️ Architecture Overview

This system follows a microservices architecture pattern with the following components:

- **Service Discovery**: Eureka Server
- **Configuration Server**: Spring Cloud Config Server
- **API Gateway**: Spring Cloud Gateway (single entry point)
- **Microservices**:
  - Auth Service (Authentication & Authorization)
  - Procurement Service (Enquiries & Suppliers)
  - Quotation Service (Supplier Quotations)
  - Purchase Order Service (POs with approval workflow)
  - Inventory Service (Multi-location inventory management)

## 🛠️ Technology Stack

### Backend
- **Java**: 21
- **Spring Boot**: 3.3.4
- **Spring Cloud**: 2023.0.3
- **Spring Cloud Gateway**: API Gateway
- **Spring Cloud Config**: Centralized configuration
- **Spring Cloud Eureka**: Service discovery
- **Spring Security + JWT**: Authentication & Authorization
- **Spring Data JPA**: Database access
- **OpenFeign**: Service-to-service communication
- **Flyway**: Database migrations
- **PostgreSQL**: Database (external, not dockerized)

### Frontend
- **React**: 18.2.0
- **Vite**: Build tool
- **React Router**: Navigation
- **Tailwind CSS**: Styling
- **Axios**: HTTP client

### DevOps
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **Kubernetes**: Container orchestration (k3s or standard cluster)
- **GitHub Actions**: CI/CD pipeline

## 📋 Prerequisites

- Java 21 or 17
- Maven 3.8+
- Node.js 18+ and npm
- PostgreSQL 14+ (external instance)
- Docker and Docker Compose (optional, for containerized deployment)

## 🗄️ Database Setup

**Important**: PostgreSQL is NOT dockerized. You need to set up external PostgreSQL databases.

Create the following databases:

```sql
CREATE DATABASE procurement_auth_db;
CREATE DATABASE procurement_procurement_db;
CREATE DATABASE procurement_quotation_db;
CREATE DATABASE procurement_po_db;
CREATE DATABASE procurement_inventory_db;
```

### Database Connection

Update the connection strings in `application.yml` or use environment variables:

```bash
# Auth Service
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/procurement_auth_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# Procurement Service
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/procurement_procurement_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# Quotation Service
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/procurement_quotation_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# Purchase Order Service
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/procurement_po_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# Inventory Service
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/procurement_inventory_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
```

## 🚀 Building the Project

### Build All Services

From the root directory:

```bash
mvn clean install
```

This will build all microservices and create JAR files in each service's `target/` directory.

## 🏃 Running Services Locally

### 1. Start Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

Eureka Dashboard: http://localhost:8761

### 2. Start Config Server

```bash
cd config-server
mvn spring-boot:run
```

Config Server: http://localhost:8888

### 3. Start Microservices

In separate terminals:

```bash
# Auth Service
cd auth-service
mvn spring-boot:run

# Procurement Service
cd procurement-service
mvn spring-boot:run

# Quotation Service
cd quotation-service
mvn spring-boot:run

# Purchase Order Service
cd purchase-order-service
mvn spring-boot:run

# Inventory Service
cd inventory-service
mvn spring-boot:run
```

### 4. Start API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

API Gateway: http://localhost:8080

### Service Ports

- Eureka Server: 8761
- Config Server: 8888
- API Gateway: 8080
- Auth Service: 8081
- Procurement Service: 8082
- Quotation Service: 8083
- Purchase Order Service: 8085
- Inventory Service: 8086

## 🐳 Running with Docker

### Prerequisites

1. Ensure PostgreSQL is running externally
2. Set environment variables for database connections

### Build Docker Images

First, build all services:

```bash
mvn clean install
```

Then build Docker images (Docker Compose will handle this automatically):

```bash
docker-compose build
```

### Start All Services

```bash
docker-compose up -d
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f auth-service
```

### Stop Services

```bash
docker-compose down
```

### Environment Variables for Docker

Create a `.env` file in the root directory:

```env
# JWT Secret
JWT_SECRET=your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm

# Auth Service DB
AUTH_DB_URL=jdbc:postgresql://your-postgres-host:5432/procurement_auth_db
AUTH_DB_USERNAME=postgres
AUTH_DB_PASSWORD=your_password

# Procurement Service DB
PROCUREMENT_DB_URL=jdbc:postgresql://your-postgres-host:5432/procurement_procurement_db
PROCUREMENT_DB_USERNAME=postgres
PROCUREMENT_DB_PASSWORD=your_password

# Quotation Service DB
QUOTATION_DB_URL=jdbc:postgresql://your-postgres-host:5432/procurement_quotation_db
QUOTATION_DB_USERNAME=postgres
QUOTATION_DB_PASSWORD=your_password

# Purchase Order Service DB
PO_DB_URL=jdbc:postgresql://your-postgres-host:5432/procurement_po_db
PO_DB_USERNAME=postgres
PO_DB_PASSWORD=your_password

# Inventory Service DB
INVENTORY_DB_URL=jdbc:postgresql://your-postgres-host:5432/procurement_inventory_db
INVENTORY_DB_USERNAME=postgres
INVENTORY_DB_PASSWORD=your_password
```

## 🎨 Frontend Setup

### Install Dependencies

```bash
cd frontend
npm install
```

### Development Server

```bash
npm run dev
```

Frontend: http://localhost:3000

### Build for Production

```bash
npm run build
```

## 📡 API Endpoints

All API calls should go through the API Gateway at `http://localhost:8080`

### Auth Service

- `POST /auth/login` - Login
- `POST /auth/register` - Register new user
- `GET /users` - Get all users (protected)
- `GET /roles` - Get all roles (protected)
- `GET /permissions` - Get all permissions (protected)

### Procurement Service

- `POST /enquiries` - Create enquiry
- `GET /enquiries` - Get all enquiries
- `GET /suppliers` - Get all suppliers

### Quotation Service

- `POST /quotations` - Create quotation
- `GET /quotations?enquiryId={id}` - Get quotations by enquiry ID
- `GET /quotations` - Get all quotations

### Purchase Order Service

- `POST /pos` - Create purchase order
- `GET /pos` - Get all purchase orders
- `GET /pos/{id}` - Get purchase order by ID
- `PATCH /pos/{id}/submit` - Submit purchase order
- `PATCH /pos/{id}/approve` - Approve purchase order

### Inventory Service

- `GET /stock` - Get all stock
- `GET /stock/location/{locationId}` - Get stock by location
- `POST /stock/transfer` - Transfer stock between locations
- `POST /stock/adjustment` - Adjust inventory
- `GET /locations` - Get all locations

## 🔐 Authentication

The system uses JWT-based authentication. After login, include the token in requests:

```
Authorization: Bearer <token>
```

## 📁 Project Structure

```
procurement/
├── eureka-server/          # Service discovery
├── config-server/          # Configuration server
├── api-gateway/            # API Gateway
├── auth-service/           # Authentication & Authorization
├── procurement-service/    # Enquiries & Suppliers
├── quotation-service/       # Quotations
├── purchase-order-service/ # Purchase Orders
├── inventory-service/      # Inventory Management
├── frontend/              # React frontend
├── docker-compose.yml      # Docker orchestration
└── pom.xml                 # Parent POM
```

## 🔄 Service Communication

- **Service Discovery**: Services register with Eureka
- **Load Balancing**: Spring Cloud LoadBalancer (via Eureka)
- **Inter-Service Calls**: OpenFeign clients
- **Configuration**: Spring Cloud Config Server

## 🗄️ Database Migrations

Each service uses Flyway for database migrations. Migration files are located in:

```
{service}/src/main/resources/db/migration/
```

Migrations run automatically on service startup.

## 🧪 Testing

### Test Endpoints

You can test the services using curl or Postman:

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Create Enquiry (with token)
curl -X POST http://localhost:8080/enquiries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"Test Enquiry","description":"Test"}'
```

## 🐛 Troubleshooting

### Services not registering with Eureka

- Ensure Eureka Server is running first
- Check service configuration for correct Eureka URL
- Verify network connectivity

### Database connection issues

- Verify PostgreSQL is running
- Check database credentials
- Ensure databases are created
- Check Flyway migration logs

### Gateway routing issues

- Verify service names match Eureka registration
- Check route configuration in `application.yml`
- Ensure services are registered with Eureka

## 📝 Notes

- PostgreSQL is **NOT** included in docker-compose.yml
- Each service has its own database schema
- JWT secret should be changed in production
- All services use Flyway for database migrations
- API Gateway is the only public entry point

## 🔄 Development Workflow

1. Start PostgreSQL (external)
2. Create databases
3. Start Eureka Server
4. Start Config Server
5. Start microservices (order doesn't matter after Eureka is up)
6. Start API Gateway
7. Start frontend

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [React Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)

## ☸️ Kubernetes Deployment

### Prerequisites for Kubernetes

- Kubernetes cluster (k3s or standard cluster)
- kubectl configured
- Docker registry access (GitHub Container Registry or other)
- Ingress controller (NGINX Ingress recommended)
- Storage class for PVCs (local-path for k3s)

### Quick Start with k3s

#### 1. Install k3s

```bash
curl -sfL https://get.k3s.io | sh -
sudo kubectl get nodes
```

#### 2. Install NGINX Ingress Controller

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
```

#### 3. Configure Image Registry

Update all Kubernetes manifests in `k8s/services/` to use your Docker registry:

```bash
# Replace YOUR_USERNAME with your GitHub username or registry path
sed -i 's/YOUR_USERNAME/your-username/g' k8s/services/*.yaml
```

#### 4. Deploy PostgreSQL

```bash
# Create namespace
kubectl create namespace procurement

# Deploy PostgreSQL StatefulSet
kubectl apply -f k8s/postgresql/statefulset.yaml -n procurement

# Wait for PostgreSQL to be ready
kubectl wait --for=condition=ready pod -l app=postgresql -n procurement --timeout=300s

# Initialize databases
kubectl apply -f k8s/postgresql/init-job.yaml -n procurement
```

#### 5. Deploy ConfigMaps and Secrets

```bash
# Update secrets.yaml with your actual values
kubectl apply -f k8s/config/ -n procurement
```

**Important**: Update `k8s/config/secrets.yaml` with secure passwords before deploying!

#### 6. Deploy Services

Deploy in order:

```bash
# 1. Eureka Server
kubectl apply -f k8s/services/eureka-server.yaml -n procurement

# Wait for Eureka to be ready
kubectl wait --for=condition=available deployment/eureka-server -n procurement --timeout=300s

# 2. Config Server
kubectl apply -f k8s/services/config-server.yaml -n procurement

# 3. API Gateway and Microservices
kubectl apply -f k8s/services/api-gateway.yaml -n procurement
kubectl apply -f k8s/services/auth-service.yaml -n procurement
kubectl apply -f k8s/services/procurement-service.yaml -n procurement
kubectl apply -f k8s/services/quotation-service.yaml -n procurement
kubectl apply -f k8s/services/purchase-order-service.yaml -n procurement
kubectl apply -f k8s/services/inventory-service.yaml -n procurement

# 4. Frontend
kubectl apply -f k8s/services/frontend.yaml -n procurement
```

#### 7. Deploy Ingress

```bash
# For local development
kubectl apply -f k8s/ingress/ingress.yaml -n procurement

# For production with TLS
kubectl apply -f k8s/ingress/ingress-tls.yaml -n procurement
```

#### 8. Configure Local DNS (Optional)

Add to `/etc/hosts`:

```
<INGRESS_IP> procurement.local
```

Get ingress IP:

```bash
kubectl get ingress -n procurement
```

### Resource Optimization for Test Environment

All deployments are optimized for a 2 vCPU / 8 GB RAM VPS:

- **JVM Heap**: Limited to 512MB (`-Xmx512m`)
- **Replicas**: 1 per service
- **PostgreSQL**: Minimal resources (256Mi-512Mi memory, 200m-500m CPU)
- **Resource Requests/Limits**: Configured for efficient resource usage

### Verify Deployment

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

### Access the Application

- **Frontend**: http://procurement.local (or your ingress host)
- **API Gateway**: http://procurement.local/api
- **Eureka Dashboard**: Port-forward to access: `kubectl port-forward svc/eureka-server 8761:8761 -n procurement`

### Horizontal Pod Autoscaling (Optional)

```bash
kubectl apply -f k8s/hpa/hpa.yaml -n procurement
```

## 🚀 CI/CD Pipeline

### GitHub Actions Workflow

The CI/CD pipeline (`.github/workflows/ci-cd.yml`) includes:

1. **Build Stage**
   - Checkout code
   - Build Spring Boot JARs
   - Run unit tests
   - Build React frontend

2. **Docker Stage**
   - Build Docker images for all services
   - Tag with commit SHA
   - Push to GitHub Container Registry

3. **Deploy Stage**
   - Apply Kubernetes manifests
   - Deploy PostgreSQL StatefulSet
   - Deploy all microservices
   - Apply Ingress rules

### Setup CI/CD

#### 1. Configure GitHub Secrets

Add the following secrets in GitHub repository settings:

- `KUBECONFIG`: Base64 encoded kubeconfig file
- `SLACK_WEBHOOK_URL`: (Optional) Slack webhook for notifications

#### 2. Update Image Registry

Update `.github/workflows/ci-cd.yml` with your registry path:

```yaml
env:
  REGISTRY: ghcr.io
  IMAGE_PREFIX: your-username/procurement
```

#### 3. Enable GitHub Container Registry

The workflow uses GitHub Container Registry (ghcr.io). Make sure:
- Repository has GitHub Actions enabled
- Workflow has permission to push packages

### Manual Deployment

If not using CI/CD:

```bash
# Build and push images manually
docker build -f eureka-server/Dockerfile -t your-registry/procurement-eureka-server:latest .
docker push your-registry/procurement-eureka-server:latest

# Repeat for all services...
```

### Database Migrations

Flyway migrations run automatically when services start. Ensure:

1. PostgreSQL is running and accessible
2. Databases are created (via init-job.yaml)
3. Services have correct database credentials

### Troubleshooting Kubernetes Deployment

#### Pods not starting

```bash
# Check pod status
kubectl describe pod <pod-name> -n procurement

# Check logs
kubectl logs <pod-name> -n procurement

# Common issues:
# - Image pull errors: Check image registry credentials
# - Database connection: Verify PostgreSQL is running and accessible
# - Resource limits: Check if resources are available
```

#### Services not connecting

```bash
# Verify service endpoints
kubectl get endpoints -n procurement

# Test service connectivity
kubectl run -it --rm debug --image=busybox --restart=Never -- nslookup auth-service
```

#### PostgreSQL issues

```bash
# Check PostgreSQL logs
kubectl logs -l app=postgresql -n procurement

# Verify PVC
kubectl get pvc -n procurement

# Access PostgreSQL directly
kubectl exec -it postgresql-0 -n procurement -- psql -U postgres
```

### Production Considerations

1. **Secrets Management**: Use external secret management (e.g., Sealed Secrets, External Secrets Operator)
2. **TLS/SSL**: Configure proper certificates for Ingress
3. **Monitoring**: Add Prometheus and Grafana
4. **Logging**: Implement centralized logging (e.g., ELK stack)
5. **Backup**: Set up PostgreSQL backups
6. **Resource Limits**: Adjust based on actual load
7. **High Availability**: Increase replicas and use PodDisruptionBudgets

## 📄 License

This project is for educational purposes.

