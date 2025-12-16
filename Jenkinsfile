pipeline {
    agent any
    
    environment {
        // ⭐ CONFIGURATION SONARQUBE
        SONAR_HOST_URL = "http://localhost:9000"      // Lien SonarQube
        SONAR_PROJECT_KEY = "student-management"      // Nom exact du projet
    }
    
    stages {
        stage('📥 Récupération Git') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }
        
        stage('🔨 Build & Tests') {
            steps {
                sh '''
                    echo "🏗️  Construction du projet..."
                    mvn clean verify
                '''
            }
        }
        
        stage('🔍 Analyse SonarQube') {
            steps {
                script {
                    echo "🚀 Lancement de l'analyse SonarQube..."
                    
                    withCredentials([string(credentialsId: 'Jenkins-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \\
                                -Dsonar.projectKey=${SONAR_PROJECT_KEY} \\
                                -Dsonar.host.url=${SONAR_HOST_URL} \\
                                -Dsonar.login=${SONAR_TOKEN} \\
                                -Dsonar.sources=src/main/java \\
                                -Dsonar.java.binaries=target/classes \\
                                -Dsonar.java.libraries=target/*.jar
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
            📊 Résultats disponibles sur : ${SONAR_HOST_URL}
            🔍 Projet analysé : "${SONAR_PROJECT_KEY}"
            
            📋 Métriques à vérifier (comme demandé dans le TP) :
            1. Duplications de code (%)
            2. Bugs (défauts fonctionnels)
            3. Vulnerabilities (failles de sécurité)
            4. Security Hotspots (zones à vérifier)
            5. Code Smells (mauvaises pratiques)
            6. Coverage (couverture de tests)
            """
        }
        failure {
            echo "❌ L'analyse SonarQube a échoué"
        }
    }
}
