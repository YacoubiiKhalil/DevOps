pipeline {
    agent any
    
    tools {
        maven 'M3'
    }
    
    stages {
        stage('Récupération Git') {
            steps {
                git branch: 'main', url: 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }
        
        stage('Build et Tests') {
            steps {
                
                sh 'mvn clean test'
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
    
    post {
        always {
            echo 'Build terminé - tests ignorés si échec'
        }
    }
}