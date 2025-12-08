pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'yacoubikha/student-app'
        SONAR_TOKEN = credentials('sonar-token')
    }
    
    stages {
        stage('Declarative: Tool Install') {
            steps {
                tool name: 'jdk17', type: 'jdk'
                tool name: 'maven-3', type: 'maven'
            }
        }
        
        stage('Récupération Git') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }
        
        stage('Build & Tests') {
            steps {
                withEnv(["JAVA_HOME=${tool 'jdk17'}", "PATH+MAVEN=${tool 'maven-3'}/bin:${env.PATH}"]) {
                    timeout(time: 10, unit: 'MINUTES') {
                        sh 'mvn clean verify'
                    }
                }
            }
        }
        
        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withEnv(["JAVA_HOME=${tool 'jdk17'}", "PATH+MAVEN=${tool 'maven-3'}/bin:${env.PATH}"]) {
                        sh "mvn sonar:sonar -Dsonar.login=${SONAR_TOKEN}"
                    }
                }
            }
        }
        
        stage('Packaging (JAR)') {
            steps {
                withEnv(["JAVA_HOME=${tool 'jdk17'}", "PATH+MAVEN=${tool 'maven-3'}/bin:${env.PATH}"]) {
                    sh 'mvn package -DskipTests'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    echo "🔨 Construction de l'image Docker : ${DOCKER_IMAGE}:v4"
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:v4"
                    
                    // Vérifier
                    sh "docker images | grep ${DOCKER_IMAGE}"
                }
            }
        }
        
        stage('Docker Push') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-id',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        // Login to Docker Hub
                        sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                        
                        // Push all tags
                        sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                        sh "docker push ${DOCKER_IMAGE}:v4"
                        
                        echo "✅ Images poussées avec succès vers Docker Hub!"
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo "🧹 Nettoyage..."
            script {
                
                sh 'mvn clean'
            }
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        success {
            echo "✅ Pipeline exécuté avec succès!"
            echo "📦 Images disponibles sur Docker Hub:"
            echo "   - ${DOCKER_IMAGE}:v4"
            echo "   - ${DOCKER_IMAGE}:latest"
            echo "   - ${DOCKER_IMAGE}:${BUILD_NUMBER}"
        }
    }
}
