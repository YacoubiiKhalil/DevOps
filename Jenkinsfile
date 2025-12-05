pipeline {
    agent any

    tools {
        maven 'M3'
    }

    environment {
        // J'ai remis cette variable car elle est utilisée dans le "post" (nettoyage)
        IMAGE_NAME = "votre-user/votre-image" 
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

        stage('Packaging (JAR)') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    } // <--- C'EST CETTE ACCOLADE QUI MANQUAIT (pour fermer "stages")

    post {
        success {
            echo "✅ Pipeline réussi !"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        always {
            echo "🧹 Nettoyage..."
            // Le "|| true" permet d'éviter que le build échoue si l'image n'existe pas
            sh "docker rmi ${IMAGE_NAME}:${env.BUILD_NUMBER} || true"
            sh "docker rmi ${IMAGE_NAME}:latest || true"
        }
    }
}
