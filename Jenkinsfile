pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from repository...'
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                echo 'Building Spring Boot backend...'
                dir('auto_service_backend') {
                    script {
                        if (isUnix()) {
                            sh './mvnw clean package -DskipTests'
                        } else {
                            bat 'mvnw.cmd clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Test Backend') {
            steps {
                echo 'Running backend tests...'
                dir('auto_service_backend') {
                    script {
                        if (isUnix()) {
                            sh './mvnw test'
                        } else {
                            bat 'mvnw.cmd test'
                        }
                    }
                }
            }
        }

        stage('Build Docker Image - Backend') {
            steps {
                echo 'Building Docker image for backend...'
                dir('auto_service_backend') {
                    script {
                        sh 'docker build -t autoservice-backend:latest .'
                    }
                }
            }
        }

        stage('Push Docker Image - Backend') {
            steps {
                echo 'Pushing backend Docker image (optional - configure registry)...'
                // Uncomment to push to a registry
                // sh 'docker tag autoservice-backend:latest your-registry/autoservice-backend:latest'
                // sh 'docker push your-registry/autoservice-backend:latest'
            }
        }
    }

    post {
        success {
            echo '✓ Backend pipeline completed successfully!'
        }
        failure {
            echo '✗ Backend pipeline failed!'
        }
        always {
            junit 'auto_service_backend/target/surefire-reports/*.xml'
        }
    }
}
