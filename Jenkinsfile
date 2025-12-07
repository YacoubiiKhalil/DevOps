pipeline {
    agent any

    tools {
        maven 'M3'
    }

    environment {
        IMAGE_NAME = "yacoubikha/premiere-image"
        SONAR_TOKEN = credentials('sonarqube-token')
        SONAR_PROJECT_KEY = "sqa_64a2766f75fe255ca8c8db30e9111a24772df5f2"
    }

    stages {
        stage('Récupération Git') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }

        stage('Build & Tests') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') { 
                    sh "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }

        stage('Packaging (JAR)') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    echo "🔨 Construction de l'image Docker : ${IMAGE_NAME}"
                    sh "docker build -t ${IMAGE_NAME}:${env.BUILD_NUMBER} ."
                    sh "docker tag ${IMAGE_NAME}:${env.BUILD_NUMBER} ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-id', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        echo "📤 Connexion à Docker Hub..."
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        
                        echo "📤 Envoi de l'image vers Docker Hub..."
                        sh "docker push ${IMAGE_NAME}:${env.BUILD_NUMBER}"
                        sh "docker push ${IMAGE_NAME}:latest"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline terminé avec succès ! Image disponible sur Docker Hub."
            // Optionnel : notification par email/webhook
        }
        failure {
            echo "❌ Le pipeline a échoué."
            // Optionnel : notification d'échec
        }
        always {
            echo "🧹 Nettoyage Docker..."
            // Nettoyage propre
            sh """
                docker rmi ${IMAGE_NAME}:latest 2>/dev/null || echo "Image latest déjà supprimée"
                docker rmi ${IMAGE_NAME}:${env.BUILD_NUMBER} 2>/dev/null || echo "Image ${env.BUILD_NUMBER} déjà supprimée"
            """
            // Nettoyage général (supprime les conteneurs/images non utilisés)
            sh 'docker system prune -f 2>/dev/null || true'
            
            // Nettoyage Maven (optionnel)
            sh 'mvn clean 2>/dev/null || true'
        }
    }
}
