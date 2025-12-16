pipeline {
    agent any
    
    environment {
        IMAGE_NAME = "yacoubikha/student-management"
        GIT_REPO = "https://github.com/YacoubiiKhalil/DevOps.git"
    }
    
    stages {
        stage('📥 Clone Repository') {
            steps {
                // Clone votre repo Git
                git branch: 'main', url: "${GIT_REPO}"
                
                // Vérification
                sh '''
                    echo "📁 Structure après clone:"
                    ls -la
                    echo ""
                    echo "🔍 Dockerfile trouvé ?"
                    find . -name "Dockerfile" 2>/dev/null || echo "Aucun Dockerfile"
                '''
            }
        }
        
        stage('📍 Cherche Dockerfile') {
            steps {
                script {
                    // Trouve automatiquement le Dockerfile
                    def dockerfile = sh(script: 'find . -name "Dockerfile" -type f | head -1', returnStdout: true).trim()
                    
                    if (dockerfile) {
                        def dockerDir = dockerfile.replace('/Dockerfile', '').replace('./', '')
                        echo "✅ Dockerfile trouvé dans: ${dockerDir}"
                        env.DOCKER_DIR = dockerDir
                    } else {
                        error("❌ ERREUR: Aucun Dockerfile trouvé dans le repository Git")
                    }
                }
            }
        }
        
        stage('📦 Build & Push') {
            steps {
                dir(env.DOCKER_DIR) {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'dockerhub-id',
                            passwordVariable: 'DOCKER_TOKEN',
                            usernameVariable: 'DOCKER_USER'
                        )
                    ]) {
                        sh '''
                            echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USER" --password-stdin
                            docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                            docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest
                            docker push ${IMAGE_NAME}:${BUILD_NUMBER}
                            docker push ${IMAGE_NAME}:latest
                        '''
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo "🎉 SUCCÈS ! Image: ${IMAGE_NAME}:${BUILD_NUMBER}"
            echo "🔗 https://hub.docker.com/r/yacoubikha/student-management"
        }
        failure {
            echo "❌ ÉCHEC - Vérifiez:"
            echo "1. Repository Git accessible"
            echo "2. Dockerfile présent dans le repo"
            echo "3. Credentials Docker Hub configurés"
        }
    }
}
