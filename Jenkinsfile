pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'yacoubikha/student-app'
    }
    
    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }
        
        stage('Checkout Git') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }
        
        stage('Setup Tools') {
            steps {
                script {
                    env.JAVA_HOME = tool name: 'jdk17', type: 'jdk'
                    env.MAVEN_HOME = tool name: 'maven-3', type: 'maven'
                    env.PATH = "${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin:${env.PATH}"
                }
            }
        }
        
        stage('Build & Test') {
            steps {
                sh '''
                    echo "Java version:"
                    java -version
                    echo "Maven version:"
                    mvn -v
                    mvn clean verify
                '''
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn sonar:sonar -Dsonar.token=sqa_64a2766f75fe255ca8c8db30e9111a24772df5f2'
                }
            }
        }
        
        stage('Package JAR') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    echo "🔨 Building Docker image: ${DOCKER_IMAGE}"
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:v4"
                    
                    echo "📋 Docker images created:"
                    sh "docker images | grep ${DOCKER_IMAGE}"
                }
            }
        }
        
        stage('Docker Push') {
            steps {
                script {
                    echo "🚀 Pushing to Docker Hub..."
                    
                    // METHOD 1: Using docker login directly (simpler)
                    sh '''
                        docker login --username yacoubikha --password-stdin <<< "votre_mot_de_passe_dockerhub"
                        
                        docker push yacoubikha/student-app:${BUILD_NUMBER}
                        docker push yacoubikha/student-app:latest
                        docker push yacoubikha/student-app:v4
                        
                        echo "✅ Successfully pushed to Docker Hub!"
                    '''
                    
                    // METHOD 2: If you want to use Jenkins credentials (comment Method 1 and uncomment below)
                    /*
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-id',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                            docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                            docker push ${DOCKER_IMAGE}:latest
                            docker push ${DOCKER_IMAGE}:v4
                        """
                    }
                    */
                }
            }
        }
        
        stage('Verify Push') {
            steps {
                script {
                    echo "🔍 Verifying pushed images..."
                    sh '''
                        echo "Docker Hub images for yacoubikha/student-app:"
                        curl -s "https://hub.docker.com/v2/repositories/yacoubikha/student-app/tags/" | \
                            python3 -c "import sys, json; data=json.load(sys.stdin); \
                            print('\\n'.join([f'{r[\"name\"]}: {r[\"last_updated\"]}' for r in data.get('results', [])]))" || \
                            echo "Could not fetch tags from Docker Hub"
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo "🎉 PIPELINE SUCCESSFUL!"
            echo "=========================================="
            echo "📦 Docker Images pushed:"
            echo "   - yacoubikha/student-app:v4"
            echo "   - yacoubikha/student-app:latest"
            echo "   - yacoubikha/student-app:${BUILD_NUMBER}"
            echo ""
            echo "🔗 SonarQube Report: http://localhost:9000"
            echo "🐳 Docker Hub: https://hub.docker.com/r/yacoubikha/student-app"
            echo "=========================================="
        }
        failure {
            echo "❌ PIPELINE FAILED!"
            echo "Check the logs above for errors."
        }
        always {
            echo "🧹 Cleaning up workspace..."
            sh 'mvn clean || true'
            sh 'docker system prune -f || true'
        }
    }
}
