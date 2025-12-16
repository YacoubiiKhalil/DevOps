// Jenkinsfile-simple-docker
pipeline {
    agent any
    environment {
        IMAGE_NAME = "yacoubikha/student-management"
    }
    stages {
        stage('Build & Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-id',
                        passwordVariable: 'DOCKER_TOKEN',
                        usernameVariable: 'DOCKER_USER'
                    )
                ]) {
                    sh '''
                        # Connexion
                        echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USER" --password-stdin
                        
                        # Construction
                        docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                        docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest
                        
                        # Push
                        docker push ${IMAGE_NAME}:${BUILD_NUMBER}
                        docker push ${IMAGE_NAME}:latest
                        
                        echo "✅ Succès!"
                    '''
                }
            }
        }
    }
}
