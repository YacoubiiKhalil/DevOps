pipeline {
    agent any
    
    environment {
        SONAR_HOST_URL = "http://localhost:9000"
        SONAR_PROJECT_KEY = "student-management"
    }
    
    stages {
        stage('🧹 Nettoyage & Clone') {
            steps {
                cleanWs()
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }

        stage('🔧 Préparation') {
            steps {
                sh '''
                    # Nettoyage complet
                    rm -rf target/
                    chmod -R 755 .

                    # Vérification pom.xml
                    echo "=== VÉRIFICATION POM ==="
                    grep -n "jacoco" pom.xml
                '''
            }
        }

        stage('🚀 Build & Tests avec JaCoCo') {
            steps {
                sh '''
                    echo "=== COMPILATION ET TESTS ==="

                    # Commande UNIQUE et COMPLÈTE
                    mvn clean verify \
                        -Dspring.profiles.active=test \
                        -DskipTests=false \
                        -Dtest=**/*Test.java \
                        -DfailIfNoTests=false

                    echo "=== VÉRIFICATION FICHIERS JACOCO ==="
                    if [ -f "target/jacoco.exec" ]; then
                        echo "✅ jacoco.exec: $(ls -lh target/jacoco.exec)"
                    else
                        echo "❌ jacoco.exec manquant!"
                        find . -name "*.exec" -o -name "jacoco.*" 2>/dev/null
                        exit 1
                    fi

                    if [ -f "target/site/jacoco/jacoco.xml" ]; then
                        echo "✅ jacoco.xml: $(ls -lh target/site/jacoco/jacoco.xml)"
                    else
                        echo "⚠️  Génération du rapport XML..."
                        mvn jacoco:report
                    fi
                '''
            }
        }

        stage('🔍 Analyse SonarQube') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'JenkinsPipelineToken', variable: 'SONAR_TOKEN')]) {
                        sh """
                            echo "=== ANALYSE SONARQUBE ==="

                            # Commande SonarQube CORRIGÉE
                            mvn sonar:sonar \
                              -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                              -Dsonar.host.url=${SONAR_HOST_URL} \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                              -Dsonar.coverage.exclusions=**/model/**,**/entity/**,**/dto/** \
                              -Dsonar.java.binaries=target/classes \
                              -Dsonar.java.test.binaries=target/test-classes \
                              -Dsonar.tests=src/test/java \
                              -Dsonar.test.inclusions=**/*Test.java \
                              -Dsonar.sourceEncoding=UTF-8 \
                              -Dsonar.scm.disabled=true
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            sh '''
                echo "=== ✅ PIPELINE RÉUSSI ==="
                echo "1. Couverture: $(grep -oPm1 'line-coverage.*?\\"\\K[^"]*' target/site/jacoco/jacoco.xml 2>/dev/null || echo 'N/A')%"
                echo "2. Rapport SonarQube: ${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"
            '''
        }
        failure {
            sh '''
                echo "=== ❌ DEBUG ==="
                echo "1. Structure du projet:"
                find . -name "*.java" | grep -E "(Test|test)" | head -10
                echo "\\n2. Résultats des tests:"
                ls -la target/surefire-reports/*.xml 2>/dev/null || echo "Aucun rapport de test"
                echo "\\n3. Log Maven:"
                tail -50 target/surefire-reports/*.txt 2>/dev/null | head -20 || echo "Pas de logs"
            '''
        }
        always {
            archiveArtifacts artifacts: 'target/site/jacoco/*.xml, target/site/jacoco/index.html', allowEmptyArchive: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}