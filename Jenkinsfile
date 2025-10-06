pipeline {
  agent any

 tools {
        maven 'M3'
        jdk 'jdk17'
    }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn -B -DskipTests=false clean verify'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }

    stage('Package') {
      steps {
        sh 'mvn -B -DskipTests=true package'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
  }
}
