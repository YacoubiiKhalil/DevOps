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
        
        stage('🔨 Tests Unitaires SEULEMENT') {
            steps {
                sh '''
                    echo "🧪 Exécution des tests UNITAIRES seulement..."
                    
                    # Exécuter UNIQUEMENT les tests unitaires (pas d'intégration)
                    mvn clean test \
                      -Dtest=SimpleTest \
                      -Dspring.datasource.url=jdbc:h2:mem:testdb \
                      -Dspring.datasource.driver-class-name=org.h2.Driver \
                      -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect
                    
                    echo "✅ Tests unitaires terminés."
                '''
            }
        }
        
        stage('🔍 Analyse SonarQube (sans authentification)') {
            steps {
                script {
                    echo "🚀 Analyse SonarQube en mode public..."
                    
                    # Essayer SANS credentials (si SonarQube est en mode public)
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.exclusions=**/test/**
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo """
            ✅ ANALYSE SONARQUBE RÉUSSIE !
            ==============================
            📊 Coverage : Basé sur 2 tests unitaires
            🔗 SonarQube : ${SONAR_HOST_URL}
            
            Pour votre TP :
            1. Accédez à SonarQube
            2. Cherchez "student-management"
            3. Vérifiez le coverage (> 0% maintenant)
            4. Notez les bugs/code smells à corriger
            """
        }
        failure {
            echo "❌ Tentative sans credentials..."
            
            // CRÉER LES CREDENTIALS AUTOMATIQUEMENT (solution de secours)
            sh '''
                echo "=== CRÉATION CREDENTIALS MANUELLE ==="
                echo "1. Allez dans Jenkins -> Manage Jenkins -> Credentials"
                echo "2. System -> Global credentials -> Add Credentials"
                echo "3. Type: Secret text"
                echo "4. Secret: [VOTRE TOKEN SONARQUBE]"
                echo "5. ID: sonarqube-token"
                echo "6. Description: Token pour student-management"
            '''
        }
    }
}
