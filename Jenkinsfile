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

        stage('Build container') {
            steps {
                echo 'Build container...'
                sh 'docker build -t sagaranin/vkloader .'
            }
        }

        stage('Docker login') {
            withCredentials([usernamePassword(credentialsId: 'gitlab_sagaranin', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                steps {
                    echo 'Docker login...'
                    sh 'docker login -u $USERNAME -p $PASSWORD'
                }
            }
        }

        stage('Docker push') {
            steps {
                echo 'Docker push...'
                sh 'docker push sagaranin/vkloader'
            }
        }

        stage('Clean') {
            steps {
                echo 'Clean...'
                sh 'mvn clean'
            }
        }
    }
}