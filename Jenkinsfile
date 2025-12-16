pipeline {
    agent any
    
    environment {
        SONAR_HOST_URL = "http://localhost:9000"
        SONAR_PROJECT_KEY = "student-management"
    }
    
    stages {
        stage('📥 Récupération Git') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }
        
        stage('🔨 Tests Unitaires') {
            steps {
                sh '''
                    mvn clean test \
                      -Dtest=SimpleTest \
                      -Dspring.datasource.url=jdbc:h2:mem:testdb \
                      -Dspring.datasource.driver-class-name=org.h2.Driver
                '''
            }
        }
        
        stage('🔍 Analyse SonarQube avec Token') {
            steps {
                script {
                    // ESSAYER AVEC DIFFÉRENTS IDs DE CREDENTIALS
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
                                echo "❌ Échec avec ${credId}: ${e.getMessage()}"
                            }
                        }
                    }
                    
                    if (!success) {
                        error "Aucun credentials valide trouvé. Créez-en un avec ID 'sonarqube-token'"
                    }
                }
            }
        }
    }
}
