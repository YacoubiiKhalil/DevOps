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
                git branch: 'main',
                    url: 'https://github.com/YacoubiiKhalil/DevOps.git',
                    credentialsId: ''  // Si besoin d'authentification
            }
        }

        stage('🔧 Vérification') {
            steps {
                sh '''
                    echo "=== VÉRIFICATION DU PROJET ==="
                    pwd
                    ls -la
                    echo "=== VÉRIFICATION DES TESTS ==="
                    find . -name "*Test.java" -type f
                '''
            }
        }

        stage('🚀 Build & Tests JaCoCo') {
            steps {
                sh '''
                    echo "=== ÉTAPE 1: CLEAN ==="
                    mvn clean -q

                    echo "=== ÉTAPE 2: COMPILE ==="
                    mvn compile -q

                    echo "=== ÉTAPE 3: TESTS AVEC JACOCO ==="
                    # CETTE COMMANDE EST CRITIQUE
                    mvn test \
                        -Dspring.profiles.active=test \
                        -Dspring.datasource.url=jdbc:h2:mem:testdb \
                        -Dspring.datasource.username=sa \
                        -Dspring.datasource.password= \
                        -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect

                    echo "=== VÉRIFICATION JACOCO ==="
                    if [ -f "target/jacoco.exec" ]; then
                        echo "✅ jacoco.exec trouvé ($(ls -lh target/jacoco.exec))"
                        # Voir le contenu
                        java -jar ~/.m2/repository/org/jacoco/org.jacoco.cli/0.8.10/org.jacoco.cli-0.8.10-nodeps.jar \
                            execinfo target/jacoco.exec || true
                    else
                        echo "❌ ERREUR: jacoco.exec non généré!"
                        find . -name "*.exec" 2>/dev/null
                        exit 1
                    fi
                '''
            }
        }

        stage('📊 Rapport JaCoCo') {
            steps {
                sh '''
                    echo "=== GÉNÉRATION RAPPORT JACOCO ==="
                    mvn jacoco:report -q

                    echo "=== VÉRIFICATION RAPPORT ==="
                    if [ -f "target/site/jacoco/jacoco.xml" ]; then
                        echo "✅ Rapport XML généré"
                        # Extraire la couverture
                        COVERAGE=$(grep -o 'line-coverage value="[^"]*"' target/site/jacoco/jacoco.xml | head -1 | cut -d'"' -f2)
                        echo "📈 Couverture des lignes: $COVERAGE"
                    else
                        echo "❌ ERREUR: Rapport XML manquant"
                        exit 1
                    fi
                '''
            }
        }

        stage('🔍 Analyse SonarQube') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'JenkinsPipelineToken', variable: 'SONAR_TOKEN')]) {
                        sh """
                            echo "=== LANCEMENT SONARQUBE ==="

                            # COMMANDE SONAR SIMPLIFIÉE MAIS COMPLÈTE
                            mvn sonar:sonar \
                              -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                              -Dsonar.host.url=${SONAR_HOST_URL} \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                              -Dsonar.java.binaries=target/classes \
                              -Dsonar.java.test.binaries=target/test-classes \
                              -Dsonar.tests=src/test/java \
                              -Dsonar.test.exclusions=**/target/** \
                              -Dsonar.coverage.exclusions=**/model/**,**/entity/**,**/dto/**,**/config/**,**/exception/** \
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
                echo "Rapport SonarQube disponible sur:"
                echo "${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"

                # Afficher un résumé de couverture
                if [ -f "target/site/jacoco/index.html" ]; then
                    echo "Rapport JaCoCo local: $(pwd)/target/site/jacoco/index.html"
                fi
            '''

            // Notification optionnelle
            emailext (
                subject: "✅ Pipeline Réussi - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "La couverture de code a été générée avec succès.\n\nVoir le rapport: ${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}",
                to: 'votre-email@example.com'
            )
        }

        failure {
            sh '''
                echo "=== ❌ ÉCHEC - DEBUG ==="
                echo "1. Fichiers dans target/:"
                ls -la target/ 2>/dev/null || echo "Aucun target"

                echo "\\n2. Fichiers de test:"
                find . -path ./target -prune -o -name "*Test.java" -print

                echo "\\n3. Logs des tests:"
                find target/surefire-reports -name "*.txt" -exec tail -5 {} \\; 2>/dev/null || true

                echo "\\n4. Vérification Maven:"
                mvn -v
            '''
        }

        always {
            // Archive les résultats
            archiveArtifacts artifacts: 'target/site/jacoco/**/*', allowEmptyArchive: true
            junit 'target/surefire-reports/*.xml'

            // Nettoyage
            sh 'rm -rf target/ ~/.m2/repository/tn/esprit/ 2>/dev/null || true'
        }
    }
}