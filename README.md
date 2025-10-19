# SDV Developer Console

A comprehensive Software Defined Vehicle (SDV) Developer Console for managing simulation scenarios, tracks, simulations, and results analysis.

## 🚀 Features

### Core Modules
- **📋 Scenarios Management** - Create and manage vehicle testing scenarios
- **🛣️ Tracks Management** - Design and configure test tracks
- **⚡ Simulations** - Execute and monitor vehicle simulations
- **📊 Results Analytics** - Comprehensive simulation results and analytics

### Key Capabilities
- Real-time simulation monitoring
- Comprehensive results analytics with performance metrics
- Mock data system for development and testing
- GraphQL API integration
- File storage with MinIO S3 compatibility
- PostgreSQL database with pgAdmin interface
- Docker containerization for easy deployment

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    SDV Developer Console                    │
├─────────────────────────────────────────────────────────────┤
│  Frontend (Next.js)     │  Backend Services (Spring Boot)  │
│  ├── Scenarios          │  ├── DCO Gateway (GraphQL)       │
│  ├── Tracks             │  ├── Scenario Library Service    │
│  ├── Simulations        │  ├── Tracks Management Service   │
│  └── Results            │  └── Results Management API      │
├─────────────────────────────────────────────────────────────┤
│  Infrastructure & Storage                                   │
│  ├── PostgreSQL Database                                   │
│  ├── MinIO S3 Storage                                      │
│  └── pgAdmin Database Management                           │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Technology Stack

### Frontend
- **Next.js 12** - React framework
- **TypeScript** - Type-safe development
- **Apollo Client** - GraphQL client
- **SDV UI Components** - Custom UI library

### Backend
- **Spring Boot** - Java backend framework
- **GraphQL** - API query language
- **PostgreSQL** - Primary database
- **MinIO** - S3-compatible object storage

### DevOps
- **Docker & Docker Compose** - Containerization
- **Maven** - Java build tool
- **npm** - Node.js package manager

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Node.js 16+
- Java 17+
- Maven 3.8+

### 1. Clone and Setup
```bash
git clone <your-repo-url>
cd sdv-developer-console
```

### 2. Environment Configuration
```bash
# Configure MinIO credentials (if needed)
# Edit minio/minio_keys.env
MINIO_ROOT_USER=your_access_key
MINIO_ROOT_PASSWORD=your_secret_key
```

### 3. Deploy with Docker
```bash
# Build and start all services
docker-compose up -d --build

# Or use the deployment script
chmod +x 20-deploy-script.sh
./20-deploy-script.sh your_access_key your_secret_key
```

### 4. Access Applications
- **Developer Console UI**: http://localhost:3000
- **GraphQL Playground**: http://localhost:8080/playground
- **Scenario Library API**: http://localhost:8082/openapi/swagger-ui/index.html
- **Tracks Management API**: http://localhost:8081/openapi/swagger-ui/index.html
- **Database Admin (pgAdmin)**: http://localhost:5050
- **MinIO Console**: http://localhost:9001

## 📊 Mock Data System

The application includes a comprehensive mock data system for development and testing:

### Configuration
Each service has configurable mock data in `/services/mockData/`:
```typescript
export const MOCK_CONFIG = {
  USE_MOCK_DATA: true,  // Toggle mock vs real data
  MOCK_DELAY: 500,      // Simulate network delay
  ENABLE_PAGINATION: true
}
```

### Available Mock Data
- **15 Scenarios** - Various testing scenarios (Performance, Safety, Traffic, etc.)
- **12 Tracks** - Different track types (Highway, Urban, Off-road, Weather)
- **8 Simulations** - Running simulations with various statuses
- **10 Results** - Completed results with comprehensive analytics

## 🔧 Development

### Frontend Development
```bash
cd developer-console-ui/app
npm install
npm run dev
```

### Backend Development
```bash
# Build individual services
cd dco-gateway && mvn clean install
cd scenario-library-service && mvn clean install
cd tracks-management-service && mvn clean install
```

### Database Management
- **Database**: `dco_db`
- **Username/Password**: `postgres/postgres`
- **pgAdmin**: admin@default.com / admin

## 📁 Project Structure

```
├── developer-console-ui/          # Next.js Frontend
│   ├── app/                      # Main application
│   ├── components/               # React components
│   ├── services/                 # API services & mock data
│   └── pages/                    # Next.js pages
├── dco-gateway/                  # GraphQL Gateway Service
├── scenario-library-service/     # Scenarios Management API
├── tracks-management-service/    # Tracks Management API
├── postgres/                     # Database initialization
├── minio/                        # Object storage configuration
└── docker-compose.yml           # Container orchestration
```

## 🎯 Key Features

### Results Analytics Dashboard
- Performance metrics (speed, efficiency, safety scores)
- Simulation execution tracking
- Comprehensive data visualization
- Export capabilities for further analysis

### Mock Data Architecture
- Easily switchable between mock and real data
- Realistic test data for all modules
- Configurable delays and pagination
- Development-friendly setup

### Container-First Design
- All services containerized with Docker
- One-command deployment
- Environment-specific configurations
- Scalable architecture

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the [Wiki](../../wiki) for detailed documentation
- Review the API documentation at runtime endpoints

---

**Built with ❤️ for the Eclipse SDV Community**
