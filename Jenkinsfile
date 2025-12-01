// Jenkins Pipeline for Auto Service Backend (Spring Boot)
// Builds, tests, and deploys using Docker Compose

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }

    environment {
        // Docker image configuration
        DOCKER_REGISTRY = 'localhost'  // Local Docker for development
        BACKEND_IMAGE = 'autoservice-backend:latest'
        BACKEND_IMAGE_FULL = "${DOCKER_REGISTRY}:5000/${BACKEND_IMAGE}"
        
        // Repository information
        GITHUB_REPO = 'https://github.com/Chamithjay/auto_service_backend.git'
        GITHUB_BRANCH = 'main'
        
        // Build tools
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk'
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo '========== STAGE: Checkout =========='
                    deleteDir()
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "${GITHUB_BRANCH}"]],
                        userRemoteConfigs: [[url: "${GITHUB_REPO}"]]
                    ])
                    echo "✅ Repository cloned successfully"
                }
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    echo '========== STAGE: Build with Maven =========='
                    sh '''
                        echo "Java version:"
                        java -version
                        echo "Maven version:"
                        mvn -v
                        
                        echo "Building backend application..."
                        mvn clean package -DskipTests
                        
                        if [ -f target/autoservice-backend-*.jar ] || [ -f target/app.jar ]; then
                            echo "✅ Build successful"
                        else
                            echo "❌ Build failed - JAR not found"
                            exit 1
                        fi
                    '''
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                script {
                    echo '========== STAGE: Run Unit Tests =========='
                    sh '''
                        echo "Running Maven tests..."
                        mvn test
                    '''
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo '========== STAGE: Build Docker Image =========='
                    sh '''
                        echo "Building Docker image: ${BACKEND_IMAGE}"
                        docker build -t ${BACKEND_IMAGE} .
                        
                        echo "Docker images:"
                        docker images | grep autoservice-backend
                        
                        echo "✅ Docker image built successfully"
                    '''
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo '========== STAGE: Deploy with Docker Compose =========='
                    sh '''
                        # Navigate to deployment directory
                        cd ../automobile-service-deployment
                        
                        echo "Current directory: $(pwd)"
                        echo "Docker Compose version:"
                        docker-compose --version
                        
                        echo "Starting services with docker-compose..."
                        docker-compose -f docker-compose.yml up -d
                        
                        echo "✅ Docker Compose deployment initiated"
                    '''
                }
            }
        }

        stage('Health Checks') {
            steps {
                script {
                    echo '========== STAGE: Health Checks =========='
                    sh '''
                        echo "Waiting for services to start (30 seconds)..."
                        sleep 30
                        
                        echo "Checking running containers..."
                        docker ps
                        
                        echo "Checking backend health..."
                        BACKEND_HEALTH=$(docker exec autoservice-backend curl -s http://localhost:8080/actuator/health || echo '{"status":"DOWN"}')
                        echo "Backend health: $BACKEND_HEALTH"
                        
                        echo "Checking database connection..."
                        docker exec autoservice-postgres pg_isready -U postgres || echo "Database check result: $?"
                        
                        echo "✅ Health checks completed"
                    '''
                }
            }
        }

        stage('Display Service URLs') {
            steps {
                script {
                    echo '========== STAGE: Service URLs =========='
                    echo '''
                    ✅ Deployment Complete!
                    
                    Services available at:
                    - Frontend: http://localhost
                    - Backend: http://localhost:8080
                    - Backend Health: http://localhost:8080/actuator/health
                    - PostgreSQL: localhost:5432 (postgres/Nipun123)
                    
                    View logs:
                    - docker-compose logs -f
                    - docker logs -f autoservice-backend
                    - docker logs -f autoservice-frontend
                    
                    Stop services:
                    - docker-compose down
                    '''
                }
            }
        }
    }

    post {
        always {
            script {
                echo '========== POST: Cleanup & Summary =========='
                sh '''
                    echo "Docker containers running:"
                    docker ps --format "table {{.Names}}\t{{.Status}}" | grep autoservice || echo "No autoservice containers found"
                    
                    echo "Docker images:"
                    docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | grep autoservice || echo "No autoservice images found"
                '''
            }
        }
        success {
            script {
                echo '✅ Pipeline completed successfully!'
            }
        }
        failure {
            script {
                echo '❌ Pipeline failed. Check logs above for details.'
            }
        }
    }
}
