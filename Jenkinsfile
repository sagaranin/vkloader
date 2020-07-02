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
                sh 'docker build --no-cache --force-rm=true -t sagaranin/vkloader .'
            }
        }

        stage('Docker login') {
            steps {
                echo 'Docker login...'
                withCredentials([usernamePassword(credentialsId: 'dockerhub_sagaranin', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh 'echo $PASSWORD | docker login -u $USERNAME --password-stdin'
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
                sh 'docker image rm sagaranin/vkloader'
            }
        }
    }
}