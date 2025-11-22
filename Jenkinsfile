pipeline {
    agent any
    
    tools {
        maven 'M3'  // Important: configurer Maven dans Jenkins
    }
    
    stages {
        stage('Récupération Git') {
            steps {
                git branch: 'main', 
                url: 'https://github.com/YacoubiiKhalil/DevOps.git'
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