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

                script {
                    echo "=== CONFIGURATION VÉRIFIÉE ==="
                    sh """
                        echo "1. Vérification du pom.xml..."
                        grep -n "argLine" pom.xml
                        echo "\\n2. Utilisateur: \$(whoami)"
                        echo "3. Répertoire: \$(pwd)"
                    """
                }
            }
        }

        stage('🔧 Fix Permissions') {
            steps {
                sh """
                    # Répare les permissions si nécessaire
                    chmod -R 755 . 2>/dev/null || true
                    rm -rf target 2>/dev/null || true
                """
            }
        }

        stage('🚀 Tests avec JaCoCo') {
            steps {
                sh """
                    echo "=== LANCEMENT DES TESTS ==="

                    # Clean et compile
                    mvn clean compile

                    # Exécution des tests (JaCoCo s'active automatiquement)
                    mvn test -Dspring.profiles.active=test

                    echo "\\n=== VÉRIFICATION JACOCO ==="
                    if [ -f "target/jacoco.exec" ]; then
                        echo "✅ jacoco.exec créé"
                        echo "Taille: \$(du -h target/jacoco.exec)"
                    else
                        echo "❌ ERREUR: jacoco.exec manquant!"
                        exit 1
                    fi
                """
            }
        }

        stage('📊 Génération Rapport') {
            steps {
                sh """
                    echo "=== GÉNÉRATION RAPPORT XML ==="

                    # Générer le rapport XML pour SonarQube
                    mvn jacoco:report

                    echo "\\n=== VÉRIFICATION RAPPORT ==="
                    if [ -f "target/site/jacoco/jacoco.xml" ]; then
                        echo "✅ jacoco.xml créé"
                        echo "Premières lignes:"
                        head -5 target/site/jacoco/jacoco.xml
                    else
                        echo "❌ ERREUR: jacoco.xml manquant!"
                        exit 1
                    fi
                """
            }
        }

        stage('🔍 Analyse SonarQube') {
            steps {
                script {
                    // CORRECTION CRITIQUE : 'sonar token' avec espace
                    withCredentials([string(credentialsId: 'sonar token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            echo "=== LANCEMENT SONARQUBE ==="
                            echo "URL: ${SONAR_HOST_URL}"
                            echo "Projet: ${SONAR_PROJECT_KEY}"

                            # Commande COMPLÈTE pour SonarQube
                            mvn sonar:sonar \
                              -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                              -Dsonar.host.url=${SONAR_HOST_URL} \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                              -Dsonar.java.coveragePlugin=jacoco \
                              -Dsonar.dynamicAnalysis=reuseReports \
                              -Dspring.datasource.url=jdbc:h2:mem:testdb \
                              -Dspring.datasource.driver-class-name=org.h2.Driver
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ PIPELINE RÉUSSI !"
            sh """
                echo "=== RÉCAPITULATIF ==="
                echo "1. Couverture générée avec succès"
                echo "2. Rapport disponible sur SonarQube"
                echo "\\n3. Fichiers créés:"
                find . -name "jacoco.*" -type f | xargs ls -la 2>/dev/null || true
            """
        }
        failure {
            echo "❌ PIPELINE ÉCHOUÉ"
            sh """
                echo "=== DEBUG ==="
                echo "1. Fichiers dans target/:"
                ls -la target/ 2>/dev/null || echo "Target non trouvé"
                echo "\\n2. Fichier jacoco.exec:"
                ls -la target/jacoco.exec 2>/dev/null || echo "jacoco.exec non trouvé"
                echo "\\n3. Logs Maven:"
                tail -100 target/surefire-reports/*.txt 2>/dev/null | head -20 || true
            """
        }
        always {
            // Archive les rapports pour consultation
            archiveArtifacts artifacts: 'target/site/jacoco/*.xml, target/site/jacoco/index.html', allowEmptyArchive: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}