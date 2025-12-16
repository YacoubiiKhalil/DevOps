pipeline {
    agent any
    
    environment {
        SONAR_HOST_URL = "http://localhost:9000"
        SONAR_PROJECT_KEY = "student-management"
    }
    
    stages {
        // Optionnel : Nettoyage pour éviter les erreurs de permission précédentes
        stage('🧹 Nettoyage Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('📥 Récupération Git') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }

        stage('🔨 Tests & Rapport Couverture') {
            steps {
                // MODIFICATION ICI :
                // 1. On retire "-Dtest=SimpleTest" pour tester tout le projet
                // 2. On ajoute "jacoco:report" pour générer le fichier target/site/jacoco/jacoco.xml
                sh '''
                    mvn clean test jacoco:report \
                      -Dspring.datasource.url=jdbc:h2:mem:testdb \
                      -Dspring.datasource.driver-class-name=org.h2.Driver
                '''
            }
        }

        stage('🔍 Analyse SonarQube') {
            steps {
                script {
                    def credentialIds = ['jenkins-token', 'sonarqube-token', 'sonar-token']
                    def success = false

                    for (credId in credentialIds) {
                        if (!success) {
                            try {
                                echo "Tentative avec credentials: ${credId}"
                                withCredentials([string(credentialsId: credId, variable: 'SONAR_TOKEN')]) {
                                    sh """
                                        mvn sonar:sonar \
                                          -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                                          -Dsonar.host.url=${SONAR_HOST_URL} \
                                          -Dsonar.login=${SONAR_TOKEN} \
                                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                                    """
                                }
                                success = true
                                echo "✅ Succès avec ${credId}"
                            } catch (Exception e) {
                                echo "❌ Échec avec ${credId}"
                            }
                        }
                    }
                    if (!success) {
                        error "Impossible de se connecter à SonarQube avec les tokens fournis."
                    }
                }
            }
        }
    }
}