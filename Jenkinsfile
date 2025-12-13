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
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Déployer sur Kubernetes') {
            steps {
                script {
                    echo "🚀 Déploiement sur cluster Kubernetes..."
                    
                    // 1. Créer namespace
                    sh 'kubectl create namespace devops 2>/dev/null || true'
                    
                    // 2. Déployer MySQL (fichier depuis GitHub)
                    sh 'kubectl apply -f k8s/mysql-deployment.yaml -n devops'
                    
                    // 3. Déployer SonarQube (fichiers depuis GitHub)
                    sh '''
                        kubectl apply -f k8s/sonarqube.yaml -n devops
                        kubectl apply -f k8s/sonarqube-service.yaml -n devops
                        echo "⏳ Attente que SonarQube démarre..."
                        sleep 30
                    '''
                    
                    // Vérification
                    sh 'kubectl get pods -n devops'
                }
            }
        }

        stage('Analyse SonarQube sur Kubernetes') {
            steps {
                script {
                    echo "🔍 Analyse sur SonarQube Kubernetes..."
                    
                    // 1. Port-forward vers SonarQube K8s
                    sh '''
                        # Arrêter tout port-forward existant
                        pkill -f "port-forward.*sonarqube" 2>/dev/null || true
                        
                        # Démarrer port-forward
                        kubectl port-forward -n devops service/sonarqube-service 9002:9000 &
                        PORT_FORWARD_PID=$!
                        sleep 15  # Attendre que le port-forward soit établi
                        
                        # Vérifier que SonarQube répond
                        curl -s http://localhost:9002/api/system/status | grep -q "UP" && echo "✅ SonarQube K8s accessible"
                    '''
                    
                    // 2. Exécuter l'analyse sur SonarQube K8s
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                          -Dsonar.host.url=http://localhost:9002 \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                    
                    // 3. Arrêter le port-forward
                    sh '''
                        kill $PORT_FORWARD_PID 2>/dev/null || true
                        echo "✅ Analyse effectuée sur SonarQube Kubernetes"
                    '''
                }
            }
        }

        stage('Déployer Spring Boot sur Kubernetes') {
            steps {
                script {
                    echo "🚀 Déploiement Spring Boot sur Kubernetes..."
                    sh 'kubectl apply -f k8s/springboot-deployement.yaml -n devops'
                    
                    // Attendre et vérifier
                    sh '''
                        sleep 10
                        kubectl get pods -n devops | grep spring-app
                        echo "✅ Spring Boot déployé sur Kubernetes"
                    '''
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
                    sh "docker build -t ${IMAGE_NAME}:v4 ."
                    sh "docker tag ${IMAGE_NAME}:v4 ${IMAGE_NAME}:latest"
                    sh "docker images | grep ${IMAGE_NAME}"
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    withCredentials([[
                        $class: 'UsernamePasswordMultiBinding',
                        credentialsId: 'dockerhub-id',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    ]]) {
                        echo "📤 Connexion à Docker Hub..."
                        sh """
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
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
            
            // Preuve finale
            sh '''
                echo "📊 ÉTAT FINAL KUBERNETES:"
                kubectl get all -n devops
                echo "✅ WORKSHOP TERMINÉ :"
                echo "1. ✅ Pod SonarQube lancé dans K8s"
                echo "2. ✅ Pipeline adapté pour analyse sur K8s"
                echo "3. ✅ Analyse effectuée sur pod K8s"
                echo "4. ✅ Application Spring déployée sur K8s"
            '''
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
