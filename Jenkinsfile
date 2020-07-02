pipeline {
    agent any
    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Prepare') {
            steps {
                echo 'Preparing...'
                checkout scm
            }
        }
        stage('Build Jar') {
            steps {
                echo 'Build Jar...'
                sh 'mvn clean compile package'
            }
        }
    }
}