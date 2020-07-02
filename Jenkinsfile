pipeline {
    agent {
        docker { image 'docker:latest' }
    }
    stages {
        stage('Prepare') {
            steps {
                sh 'yum install -y git'
            }
        }
    }
}