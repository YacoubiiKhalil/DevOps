pipeline {
    agent any
    
    stages {
        stage('Récupération Git') {
            steps {
                git 'https://github.com/YacoubiiKhalil/DevOps.git'
            }
        }
        
        stage('Lancement tests') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Création livrable') {
            steps {
                sh 'mvn clean package'
            }
        }
    }
}