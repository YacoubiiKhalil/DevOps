pipeline {
    agent any

    // Outils configurés dans Jenkins (noms par défaux ou ceux que tu as)
    tools {
        maven 'M2'      // Nom dans Jenkins → Tools → Maven
        jdk 'JDK'       // Nom dans Jenkins → Tools → JDK
    }

    environment {
        IMAGE_NAME = "yacoubikha/student-app"
        SONAR_PROJECT_KEY = "student-management"
        SONAR_K8S_HOST = "192.168.49.2"
        SONAR_K8S_PORT = "31722"
        SONAR_K8S_URL = "http://${SONAR_K8S_HOST}:${SONAR_K8S_PORT}"
        SONAR_K8S_USER = "admin"
        SONAR_K8S_PASS = "admin"
        K8S_NAMESPACE = "devops"
    }

    stages {
        stage('Récupération Git') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }

        stage('Vérifier infrastructure K8s') {
            steps {
                script {
                    echo "🔍 Vérification de l'infrastructure Kubernetes..."
                    sh "kubectl get ns ${K8S_NAMESPACE}"
                    sh """
                        echo "Vérification SonarQube sur K8s..."
                        kubectl get pods -n ${K8S_NAMESPACE} -l app=sonarqube
                        kubectl get svc -n ${K8S_NAMESPACE} sonarqube-service
                    """
                    sh """
                        kubectl get pods -n ${K8S_NAMESPACE}
                        kubectl get svc -n ${K8S_NAMESPACE}
                    """
                }
            }
        }

        stage('Build & Tests') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Analyse SonarQube sur K8s') {
            steps {
                script {
                    echo "📊 Analyse avec SonarQube déployé sur Kubernetes..."
                    echo "URL SonarQube K8s: ${SONAR_K8S_URL}"
                    sh """
                        for i in \$(seq 1 10); do
                            if curl -s ${SONAR_K8S_URL}/api/system/status | grep -q "UP"; then
                                echo "✅ SonarQube K8s prêt"
                                break
                            fi
                            echo "⏳ Attente SonarQube K8s... (\$i/10)"
                            sleep 5
                        done
                    """
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=\${SONAR_PROJECT_KEY} \
                          -Dsonar.host.url=\${SONAR_K8S_URL} \
                          -Dsonar.login=\${SONAR_K8S_USER} \
                          -Dsonar.password=\${SONAR_K8S_PASS} \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Vérification analyse K8s') {
            steps {
                script {
                    echo "🔎 Vérification que l'analyse a été effectuée sur K8s..."
                    sh """
                        sleep 10
                        ANALYSIS=\$(curl -s -u \${SONAR_K8S_USER}:\${SONAR_K8S_PASS} \
                            "\${SONAR_K8S_URL}/api/project_analyses/search?project=\${SONAR_PROJECT_KEY}" 2>/dev/null || echo "{}")
                        if echo "\$ANALYSIS" | grep -q "analyses"; then
                            echo "✅ Analyse effectuée sur SonarQube K8s"
                            echo "🔗 Rapport disponible: \${SONAR_K8S_URL}/dashboard?id=\${SONAR_PROJECT_KEY}"
                        else
                            echo "⚠️  Première analyse - création du projet..."
                            curl -X POST "\${SONAR_K8S_URL}/api/projects/create" \
                                -u \${SONAR_K8S_USER}:\${SONAR_K8S_PASS} \
                                -d "project=\${SONAR_PROJECT_KEY}&name=Student Management"
                        fi
                    """
                }
            }
        }

        stage('Packaging (JAR)') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Déployer sur K8s') {
            steps {
                script {
                    echo "🚀 Déploiement sur Kubernetes..."
                    sh "kubectl apply -f k8s/mysql-deployment.yaml -n \${K8S_NAMESPACE} || true"
                    sh "kubectl apply -f k8s/springboot-deployement.yaml -n \${K8S_NAMESPACE} || true"
                    sh """
                        kubectl rollout status deployment/spring-app -n \${K8S_NAMESPACE} --timeout=300s
                        echo "✅ Application déployée sur K8s"
                        echo "🌐 Accès: http://\${SONAR_K8S_HOST}:30080"
                    """
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    echo "🔨 Construction de l'image Docker : \${IMAGE_NAME}"
                    sh "docker build -t \${IMAGE_NAME}:v5 ."
                    sh "docker tag \${IMAGE_NAME}:v5 \${IMAGE_NAME}:latest"
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'dockerhub-id',
                            passwordVariable: 'DOCKER_PASSWORD',
                            usernameVariable: 'DOCKER_USERNAME'
                        )
                    ]) {
                        sh """
                            echo "\$DOCKER_PASSWORD" | docker login -u "\$DOCKER_USERNAME" --password-stdin
                            docker push \${IMAGE_NAME}:v5
                            docker push \${IMAGE_NAME}:latest
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline terminé avec succès !"
            echo "📊 Analyse SonarQube K8s: \${SONAR_K8S_URL}"
            echo "🚀 Application K8s: http://\${SONAR_K8S_HOST}:30080"
            echo "📦 Docker Hub: \${IMAGE_NAME}:v5"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        always {
            sh 'mvn clean 2>/dev/null || true'
        }
    }
}