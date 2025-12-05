pipeline {
    agent any

    tools {
        // Ton outil Maven configuré dans Jenkins (tel que vu dans tes messages précédents)
        maven 'M3'
        // Si tu as configuré un JDK spécifique, décommente la ligne suivante :
        // jdk 'JAVA_HOME' 
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
                    // 'clean verify' compile et lance les tests unitaires
                    sh 'mvn clean verify'
                }
            }
        }

 

        stage('Packaging (JAR)') {
            steps {
                // Génère le .jar dans le dossier target/ sans relancer les tests
                sh 'mvn package -DskipTests'
            }
        }



    post {
        success {
            echo "✅ Pipeline et Push Docker réussis !"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        always {
            echo "🧹 Nettoyage des images Docker locales pour économiser de l'espace..."
            sh "docker rmi ${IMAGE_NAME}:${env.BUILD_NUMBER} || true"
            sh "docker rmi ${IMAGE_NAME}:latest || true"
        }
    }
}
