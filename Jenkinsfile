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

        stage('🔧 Configuration Java') {
            steps {
                sh '''
                    echo "=== CONFIGURATION JAVA ==="
                    # Définir JAVA_HOME si nécessaire (comme vous avez fait)
                    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
                    export PATH=$JAVA_HOME/bin:$PATH
                    java -version
                '''
            }
        }

        stage('🚀 Tests avec JaCoCo') {
            steps {
                sh '''
                    echo "=== EXÉCUTION DES TESTS ==="

                    # SIMPLE - comme sur votre machine
                    mvn clean test

                    echo "=== VÉRIFICATION JACOCO ==="
                    if [ -f "target/jacoco.exec" ]; then
                        echo "✅ SUCCÈS: jacoco.exec créé"
                        echo "Taille: $(ls -lh target/jacoco.exec)"
                    else
                        echo "❌ ERREUR: jacoco.exec manquant"
                        exit 1
                    fi
                '''
            }
        }

        stage('📊 Génération Rapport') {
            steps {
                sh '''
                    echo "=== GÉNÉRATION RAPPORT XML ==="

                    mvn jacoco:report

                    if [ -f "target/site/jacoco/jacoco.xml" ]; then
                        echo "✅ Rapport XML généré"
                        # Afficher un aperçu
                        echo "=== APERÇU COUVERTURE ==="
                        grep -E "line-counter|branch-counter" target/site/jacoco/jacoco.xml | head -4
                    else
                        echo "❌ ERREUR: Pas de rapport XML"
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
                            echo "=== ANALYSE SONARQUBE ==="

                            # Commande SIMPLE qui marche
                            mvn sonar:sonar \\
                              -Dsonar.projectKey=${SONAR_PROJECT_KEY} \\
                              -Dsonar.host.url=${SONAR_HOST_URL} \\
                              -Dsonar.login=${SONAR_TOKEN} \\
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \\
                              -Dsonar.java.binaries=target/classes
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            sh '''
                echo "=== ✅✅✅ PIPELINE RÉUSSI ! ✅✅✅ ==="
                echo "FÉLICITATIONS ! La couverture est générée !"
                echo ""
                echo "📊 RAPPORT SONARQUBE:"
                echo "${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"
                echo ""
                echo "📁 FICHIERS:"
                find . -name "jacoco.*" -type f | xargs ls -la 2>/dev/null
            '''
        }
        failure {
            sh '''
                echo "=== ❌ DEBUG ==="
                echo "1. Java:"
                java -version 2>&1
                echo "\\n2. Maven:"
                mvn -v 2>&1
                echo "\\n3. Fichiers:"
                find . -name "*.exec" -o -name "jacoco.*" 2>/dev/null
            '''
        }
        always {
            archiveArtifacts artifacts: 'target/site/jacoco/*.xml, target/site/jacoco/index.html', allowEmptyArchive: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}