pipeline {
    agent any

    tools {
        maven 'M3'
    }

    environment {
        // --- CONFIGURATION DOCKER (Mise à jour d'après votre image) ---
        IMAGE_NAME = "yacoubikha/premiere-image"

        // --- CONFIGURATION SONARQUBE ---
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
                    // Construction avec le tag du numéro de build (ex: :21) et le tag latest
                    sh "docker build -t ${IMAGE_NAME}:${env.BUILD_NUMBER} ."
                    sh "docker tag ${IMAGE_NAME}:${env.BUILD_NUMBER} ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    // Connexion à Docker Hub avec vos identifiants Jenkins
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-id', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        echo "📤 Connexion à Docker Hub en tant que $USER..."
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        
                        echo "📤 Envoi des images..."
                        sh "docker push ${IMAGE_NAME}:${env.BUILD_NUMBER}"
                        sh "docker push ${IMAGE_NAME}:latest"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline, Analyse Sonar et Push Docker réussis !"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        always {
            echo "🧹 Nettoyage des images locales..."
            // Supprime les images du serveur Jenkins pour ne pas le saturer
            sh "docker rmi ${IMAGE_NAME}:${env.BUILD_NUMBER} || true"
            sh "docker rmi ${IMAGE_NAME}:latest || true"
        }
    }
}
