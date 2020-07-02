pipeline {
    agent any
    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Prepare') {
            steps {
                checkout scm
            }
        }

        stage('Build Jar') {
            steps {
                sh 'mvn clean compile package'
            }
        }

        stage('Build container') {
            steps {
                sh 'docker build --no-cache --force-rm=true -t sagaranin/vkloader .'
            }
        }

        stage('Docker login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub_sagaranin', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh 'echo $PASSWORD | docker login -u $USERNAME --password-stdin'
                }
            }
        }

        stage('Docker push') {
            steps {
                sh 'docker push sagaranin/vkloader'
            }
        }

        stage('Clean') {
            steps {
                sh 'mvn clean'
                sh 'docker image rm sagaranin/vkloader'
            }
        }
    }
}