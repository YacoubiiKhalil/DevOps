pipeline {
    agent any
    tools {
        maven 'M3'
    }
    environment {
        SONAR_TOKEN = credentials('sonarqube-token')
    }

    stages {
        stage('Récupération Git') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }

        stage('Build, Tests et Rapports') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {

                    sh 'mvn clean verify'
                }
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=sqa_64a2766f75fe255ca8c8db30e9111a24772df5f2 -Dsonar.login=$SONAR_TOKEN'
                }
            }
        }
    }

    post {
        always {
            echo 'Build terminé - couverture des tests vérifiée avec Jacoco'
        }
        success {
            sh 'echo "✅ Couverture Jacoco générée avec succès"'
        }
    }
}