pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'jdk17'
    }

    environment {
        IMAGE_NAME = "yacoubikha/student-app"
        SONAR_PROJECT_KEY = "student-management"
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
                    // CORRECTION: Exécute les tests pour générer la couverture
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    // CORRECTION: Ajoute le chemin du rapport JaCoCo
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Packaging (JAR)') {
            steps {
                // OK de skip les tests ici, ils sont déjà faits
                sh 'mvn package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    echo "🔨 Construction de l'image Docker : ${IMAGE_NAME}"
                    sh "docker build -t ${IMAGE_NAME}:v4 ."
                    sh "docker tag ${IMAGE_NAME}:v4 ${IMAGE_NAME}:latest"
                    
                    // Vérification
                    sh "docker images | grep ${IMAGE_NAME}"
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'dockerhub-id',
                            passwordVariable: 'DOCKER_PASSWORD',
                            usernameVariable: 'DOCKER_USERNAME'
                        )
                    ]) {
                        echo "📤 Connexion à Docker Hub..."
                        // CORRECTION: Syntaxe des variables
                        sh """
                            echo "\$DOCKER_PASSWORD" | docker login -u "\$DOCKER_USERNAME" --password-stdin
                        """
                        
                        echo "📤 Envoi de l'image vers Docker Hub..."
                        sh """
                            docker push ${IMAGE_NAME}:v4
                            docker push ${IMAGE_NAME}:latest
                        """
                        
                        echo "✅ Images poussées avec succès!"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline terminé avec succès !"
            echo "📦 Images disponibles sur Docker Hub:"
            echo "   - ${IMAGE_NAME}:v4"
            echo "   - ${IMAGE_NAME}:latest"
            echo "🔗 https://hub.docker.com/r/yacoubikha/student-app"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        always {
            echo "🧹 Nettoyage..."
            sh 'mvn clean 2>/dev/null || true'
        }
    }
}
