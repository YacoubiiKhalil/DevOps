pipeline {
    agent any
    
    environment {
        IMAGE_NAME = "yacoubikha/student-management"
        // ⭐ UTILISE VOS VARIABLES VM
        M2_HOME = '/opt/apache-maven-3.6.3'
        PATH = "${M2_HOME}/bin:${env.PATH}"
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    }
    
    stages {
        stage('📥 Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }
        
        stage('🔨 Setup Environment') {
            steps {
                sh '''
                    echo "🔧 Configuration de l'environnement:"
                    echo "M2_HOME: $M2_HOME"
                    echo "JAVA_HOME: $JAVA_HOME"
                    echo "PATH: $PATH"
                    echo ""
                    echo "📦 Vérification Maven:"
                    which mvn
                    mvn --version
                    echo ""
                    echo "📦 Vérification Java:"
                    java --version
                '''
            }
        }
        
        stage('🏗️ Build Maven') {
            steps {
                sh '''
                    echo "🔨 Construction du JAR avec Maven..."
                    mvn clean package -DskipTests
                    
                    echo "✅ Vérification du JAR généré:"
                    ls -lh target/*.jar
                    echo "Taille: $(du -h target/*.jar | cut -f1)"
                '''
            }
        }
        
        stage('📊 Vérification Fichiers') {
            steps {
                sh '''
                    echo "📁 Structure complète:"
                    echo "Dockerfile:"
                    cat Dockerfile
                    echo ""
                    echo "target/:"
                    ls -la target/ 2>/dev/null || { echo "❌ ERREUR: target/ vide!"; exit 1; }
                '''
            }
        }
        
        stage('🐳 Build Docker') {
            steps {
                sh '''
                    echo "🔨 Construction de l'image Docker..."
                    docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                    docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest
                    
                    echo "📦 Images créées:"
                    docker images | grep ${IMAGE_NAME}
                '''
            }
        }
        
        stage('📤 Push Docker') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-id',
                        passwordVariable: 'DOCKER_TOKEN',
                        usernameVariable: 'DOCKER_USER'
                    )
                ]) {
                    sh '''
                        echo "🔐 Connexion à Docker Hub..."
                        echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USER" --password-stdin
                        
                        echo "📤 Envoi des images..."
                        docker push ${IMAGE_NAME}:${BUILD_NUMBER}
                        docker push ${IMAGE_NAME}:latest
                        
                        echo "✅ Push terminé!"
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo """
            🎉 PIPELINE RÉUSSI !
            ========================
            📦 Image: ${IMAGE_NAME}
            🏷️  Tag: ${BUILD_NUMBER} et latest
            🔗 Docker Hub: https://hub.docker.com/r/yacoubikha/student-management
            📊 Build: ${env.BUILD_URL}
            """
        }
        failure {
            echo "❌ ÉCHEC - Dernière étape en erreur"
            sh '''
                echo "=== DEBUG ==="
                echo "Maven:"
                which mvn 2>/dev/null || echo "Maven non trouvé"
                echo ""
                echo "Java:"
                which java 2>/dev/null || echo "Java non trouvé"
                echo ""
                echo "Fichiers:"
                find . -name "*.jar" -o -name "Dockerfile" | xargs ls -la 2>/dev/null || true
            '''
        }
    }
}
