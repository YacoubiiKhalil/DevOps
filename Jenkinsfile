pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'yacoubikha/student-app'
    }
    
    stages {
        stage('Checkout Git') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }
        
        stage('Install Java 17') {
            steps {
                sh '''
                    # Install Java 17 si pas déjà installé
                    if ! command -v java &> /dev/null || ! java -version 2>&1 | grep -q "17"; then
                        echo "Installing Java 17..."
                        sudo apt update
                        sudo apt install -y openjdk-17-jdk
                    fi
                    
                    # Vérifier Java
                    java -version
                    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
                    echo "JAVA_HOME: $JAVA_HOME"
                '''
            }
        }
        
        stage('Build & Test') {
            steps {
                sh '''
                    # Utiliser Maven du système ou installer
                    if ! command -v mvn &> /dev/null; then
                        echo "Installing Maven..."
                        sudo apt install -y maven
                    fi
                    
                    mvn clean verify
                '''
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                sh '''
                    mvn sonar:sonar \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.token=sqa_64a2766f75fe255ca8c8db30e9111a24772df5f2
                '''
            }
        }
        
        stage('Package JAR') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh '''
                    echo "🔨 Building Docker image..."
                    docker build -t yacoubikha/student-app:v4 .
                    docker tag yacoubikha/student-app:v4 yacoubikha/student-app:latest
                    
                    echo "📋 Docker images created:"
                    docker images | grep student-app
                '''
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                sh '''
                    echo "🚀 Pushing to Docker Hub..."
                    
                    # METHOD 1: Direct login (remplacez le password)
                    docker login --username yacoubikha --password-stdin <<< "VOTRE_MOT_DE_PASSE_DOCKERHUB"
                    
                    # Push images
                    docker push yacoubikha/student-app:v4
                    docker push yacoubikha/student-app:latest
                    
                    echo "✅ SUCCESS! Images pushed!"
                    echo "📦 yacoubikha/student-app:v4"
                    echo "📦 yacoubikha/student-app:latest"
                '''
            }
        }
    }
    
    post {
        success {
            echo "🎉 PIPELINE SUCCESSFUL!"
            echo "=========================================="
            echo "📦 Docker Hub: https://hub.docker.com/r/yacoubikha/student-app"
            echo "🔗 SonarQube: http://localhost:9000"
            echo "=========================================="
        }
        failure {
            echo "❌ PIPELINE FAILED!"
        }
        always {
            echo "🧹 Cleaning up..."
            sh 'docker system prune -f || true'
        }
    }
}
