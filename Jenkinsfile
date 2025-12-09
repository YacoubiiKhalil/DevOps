pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'jdk17'  // Ajouté - doit être configuré dans Jenkins
    }

    environment {
        IMAGE_NAME = "yacoubikha/student-app"  // Changé pour student-app
        SONAR_PROJECT_KEY = "student-management"  // Changé - c'est le projectKey, pas le token
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
<<<<<<< HEAD
                withSonarQubeEnv('sonarqube') {
=======
                withSonarQubeEnv('sonarqube') { 
>>>>>>> 35a4c8decbde4c551e85dcdd7f7c63f920f7e017
                    // Le token est géré automatiquement par withSonarQubeEnv
                    sh "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}"
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
                    sh "docker build -t ${IMAGE_NAME}:v4 ."  // Changé : v4 au lieu de BUILD_NUMBER
                    sh "docker tag ${IMAGE_NAME}:v4 ${IMAGE_NAME}:latest"
<<<<<<< HEAD

=======
                    
>>>>>>> 35a4c8decbde4c551e85dcdd7f7c63f920f7e017
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
<<<<<<< HEAD
                            credentialsId: 'dockerhub-id',
                            passwordVariable: 'DOCKER_PASSWORD',
=======
                            credentialsId: 'dockerhub-id', 
                            passwordVariable: 'DOCKER_PASSWORD', 
>>>>>>> 35a4c8decbde4c551e85dcdd7f7c63f920f7e017
                            usernameVariable: 'DOCKER_USERNAME'
                        )
                    ]) {
                        echo "📤 Connexion à Docker Hub..."
                        sh """
                            echo "\${DOCKER_PASSWORD}" | docker login -u "\${DOCKER_USERNAME}" --password-stdin
                        """
<<<<<<< HEAD

=======
                        
>>>>>>> 35a4c8decbde4c551e85dcdd7f7c63f920f7e017
                        echo "📤 Envoi de l'image vers Docker Hub..."
                        sh """
                            docker push ${IMAGE_NAME}:v4
                            docker push ${IMAGE_NAME}:latest
                        """
<<<<<<< HEAD

=======
                        
>>>>>>> 35a4c8decbde4c551e85dcdd7f7c63f920f7e017
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
            // Nettoyage Maven seulement
            sh 'mvn clean 2>/dev/null || true'
<<<<<<< HEAD

=======
            
>>>>>>> 35a4c8decbde4c551e85dcdd7f7c63f920f7e017
            // NE PAS supprimer les images Docker ici - elles sont déjà poussées
            // sh 'docker system prune -f 2>/dev/null || true'
        }
    }
}
