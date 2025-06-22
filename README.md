# Plany - Task Management Application

A Spring Boot application for task management with a modern web interface.

## 🚀 Quick Start

### Local Development

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd plany
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - Frontend: http://localhost:8081
   - API: http://localhost:8081/api

### Manual Setup

1. **Build the application**
   ```bash
   cd plany
   mvn clean package
   ```

2. **Run the application**
   ```bash
   java -jar target/plany-0.0.1-SNAPSHOT.jar
   ```

## 🐳 Docker

### Build Image
```bash
docker build -t plany-app .
```

### Run Container
```bash
docker run -p 8081:8081 plany-app
```

## 🌐 Deployment on Render

### Prerequisites
- Render account
- Railway PostgreSQL database (already configured)

### Deployment Steps

1. **Connect your repository to Render**
   - Go to Render Dashboard
   - Click "New +" → "Web Service"
   - Connect your GitHub repository

2. **Configure the service**
   - **Name**: `plany-app`
   - **Environment**: `Docker`
   - **Region**: Choose closest to your users
   - **Branch**: `main` (or your default branch)

3. **Environment Variables**
   ```
   PORT=8081
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=postgresql://postgres:pAiBYcudZpLsxtirnCwPmiHzSPTVhlsn@yamanote.proxy.rlwy.net:40166/railway
   DB_USERNAME=postgres
   DB_PASSWORD=pAiBYcudZpLsxtirnCwPmiHzSPTVhlsn
   ```

4. **Build Command**
   ```bash
   chmod +x build.sh && ./build.sh
   ```

5. **Start Command**
   ```bash
   java -jar plany.jar
   ```

### Alternative: Direct JAR Deployment

If you prefer to deploy the JAR directly:

1. **Build locally**
   ```bash
   cd plany
   mvn clean package -DskipTests
   ```

2. **Upload JAR to Render**
   - Use the JAR file from `plany/target/`
   - Set start command: `java -jar plany-0.0.1-SNAPSHOT.jar`

## 📁 Project Structure

```
plany/
├── src/
│   ├── main/
│   │   ├── java/co/plany/plany/
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── model/         # Entity models
│   │   │   ├── repository/    # Data repositories
│   │   │   ├── service/       # Business logic
│   │   │   └── config/        # Configuration
│   │   └── resources/
│   │       ├── static/        # Frontend files
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Application port | `8081` |
| `DATABASE_URL` | PostgreSQL connection URL | Railway URL |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | Railway password |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` |

### Database

The application uses PostgreSQL with the following tables:
- `usuario` - User accounts
- `tarea` - Tasks
- `categoria` - Categories
- `prioridad` - Priorities
- `estado` - Task status
- `tipo` - Task types
- `recordatorio` - Reminders
- `log_tarea_creada` - Task creation logs

## 🛠️ Development

### Prerequisites
- Java 17
- Maven 3.8+
- PostgreSQL (for local development)
- Docker (optional)

### API Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/tareas` - Get all tasks
- `POST /api/tareas` - Create new task
- `PUT /api/tareas/{id}` - Update task
- `DELETE /api/tareas/{id}` - Delete task

## 📝 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
