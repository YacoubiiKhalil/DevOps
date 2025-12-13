pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'jdk17'
    }

    environment {
        SONAR_PROJECT_KEY = "student-management"
        SONAR_HOST_URL = "http://sonarqube.devops.svc.cluster.local:9000"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }

        stage('Deploy SonarQube on Kubernetes') {
            steps {
                sh '''
                kubectl create namespace devops 2>/dev/null || true
                kubectl apply -f k8s/sonarqube.yaml -n devops
                kubectl apply -f k8s/sonarqube-service.yaml -n devops
                kubectl rollout status deployment/sonarqube -n devops
                '''
            }
        }

        stage('SonarQube Code Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                    mvn sonar:sonar \
                      -DskipTests \
                      -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                      -Dsonar.host.url=${SONAR_HOST_URL} \
                      -Dsonar.login=$SONAR_TOKEN
                    '''
                }
            }
        }

        stage('Verification') {
            steps {
                sh '''
                echo "📊 Vérification SonarQube:"
                kubectl get pods -n devops
                echo "✅ Analyse envoyée à SonarQube"
                '''
            }
        }
    }

    post {
        success {
            echo "✅ EXIGENCES VALIDÉES"
            echo "1️⃣ SonarQube lancé dans Kubernetes"
            echo "2️⃣ Analyse effectuée via pipeline Jenkins"
            echo "3️⃣ Résultats visibles dans SonarQube"
        }
        failure {
            echo "❌ Pipeline échoué"
        }
    }
}
