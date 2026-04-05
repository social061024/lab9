pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/social061024/lab9.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t java-ci-cd-app .'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                   docker stop java-ci-cd-app || true
                   docker rm java-ci-cd-app || true
                   docker run -d --name java-ci-cd-app -p 8080:8080 java-ci-cd-app
                '''
            }
        }
    }
}
