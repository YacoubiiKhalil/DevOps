pipeline {
    agent any

    tools {
        // Le nom doit correspondre à votre configuration dans "Global Tool Configuration"
        maven 'M3'
    }

    environment {
        // --- CONFIGURATION DOCKER ---
        // Nom exact de votre image (vu sur votre capture d'écran)
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
                    // Compile et lance les tests unitaires
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Analyse SonarQube') {
            steps {
                // 'sonarqube' doit être le nom configuré dans Jenkins (Gérer Jenkins > Système)
                withSonarQubeEnv('sonarqube') {
                    sh "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }

        stage('Packaging (JAR)') {
            steps {
                // Génère le fichier .jar dans le dossier target/ sans relancer les tests
                sh 'mvn package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    echo "🔨 Construction de l'image Docker : ${IMAGE_NAME}"
                    // Construction de l'image avec deux tags : le numéro de build (ex: :35) et 'latest'
                    sh "docker build -t ${IMAGE_NAME}:${env.BUILD_NUMBER} ."
                    sh "docker tag ${IMAGE_NAME}:${env.BUILD_NUMBER} ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    // Connexion sécurisée à Docker Hub via les credentials Jenkins
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-id', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        echo "📤 Connexion à Docker Hub..."
                        // Login non-interactif
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
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        always {
            echo "🧹 Nettoyage des images Docker locales..."
            // Supprime les images locales pour économiser de l'espace disque sur le serveur Jenkins
            sh "docker rmi ${IMAGE_NAME}:${env.BUILD_NUMBER} || true"
            sh "docker rmi ${IMAGE_NAME}:latest || true"
        }
    }
}