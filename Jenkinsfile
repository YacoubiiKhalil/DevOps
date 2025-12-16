pipeline {
    agent any
    
    environment {
        SONAR_HOST_URL = "http://localhost:9000"
        SONAR_PROJECT_KEY = "student-management"
    }
    
    stages {
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

        stage('🚀 Tests & Analyse SonarQube') {
            steps {
                script {
                    def credentialIds = ['jenkins-token', 'sonarqube-token', 'sonar-token']
                    def success = false

                    for (credId in credentialIds) {
                        if (!success) {
                            try {
                                withCredentials([string(credentialsId: credId, variable: 'SONAR_TOKEN')]) {
                                    // La commande magique qui fait tout dans l'ordre
                                    sh """
                                        mvn clean verify sonar:sonar \
                                          -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                                          -Dsonar.host.url=${SONAR_HOST_URL} \
                                          -Dsonar.login=${SONAR_TOKEN} \
                                          -Dspring.datasource.url=jdbc:h2:mem:testdb \
                                          -Dspring.datasource.driver-class-name=org.h2.Driver
                                    """
                                }
                                success = true
                            } catch (Exception e) {
                                echo "Passage au token suivant..."
                            }
                        }
                    }
                    if (!success) error "Analyse échouée"
                }
            }
        }
    }
}