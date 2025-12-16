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
        
        stage('🔨 Build & Tests AVEC H2') {
            steps {
                sh '''
                    echo "🧪 Exécution des tests avec H2 (in-memory)..."
                    
                    # FORCER l'utilisation de H2 pour les tests
                    mvn clean verify \
                      -Dspring.datasource.url=jdbc:h2:mem:testdb \
                      -Dspring.datasource.username=sa \
                      -Dspring.datasource.password= \
                      -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect \
                      -DskipTests=false
                    
                    echo "✅ Tests terminés."
                    echo "📊 Rapport JaCoCo généré :"
                    find target/ -name "jacoco.xml" -o -name "jacoco.exec" | xargs ls -la 2>/dev/null || true
                '''
            }
        }
        
        stage('🔍 Analyse SonarQube') {
            steps {
                script {
                    echo "🚀 Lancement de l'analyse SonarQube..."
                    
                    withCredentials([string(credentialsId: 'jenkins-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \
                                -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                                -Dsonar.host.url=${SONAR_HOST_URL} \
                                -Dsonar.login=${SONAR_TOKEN} \
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo """
            ✅ ANALYSE SONARQUBE RÉUSSIE !
            ==============================
            📊 Coverage : Maintenant visible dans SonarQube
            🔗 Accédez à : ${SONAR_HOST_URL}
            📋 Projet : "${SONAR_PROJECT_KEY}"
            
            Objectif TP : Coverage > 80%
            """
        }
        failure {
            echo "❌ Échec de l'analyse"
            sh '''
                echo "=== TROUBLESHOOTING ==="
                echo "1. Fichiers JaCoCo :"
                find . -name "jacoco.*" -type f 2>/dev/null | xargs ls -la || echo "Aucun"
                echo ""
                echo "2. Résumé tests :"
                cat target/surefire-reports/*.txt 2>/dev/null | grep -A5 -B5 "Tests run:" || echo "Pas de rapport"
                echo ""
                echo "3. SonarQube accessible ?"
                curl -s -o /dev/null -w "%{http_code}" http://localhost:9000 || echo "curl échoué"
            '''
        }
    }
}
