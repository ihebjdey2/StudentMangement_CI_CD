pipeline {
  agent any

  tools {
    maven 'M3'
    jdk 'jdk17'
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        echo '🔹 Étape 1 : Récupération du code depuis GitHub...'
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        echo '🔹 Étape 2 : Compilation et tests avec H2...'
        sh 'mvn -B clean verify -Dspring.profiles.active=test'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }
    stage('SonarQube Analysis') {
  steps {
    echo '🔍 Étape 6 : Analyse qualité avec SonarQube + JaCoCo...'
    withSonarQubeEnv('sonar') {
      sh 'mvn -B clean verify sonar:sonar -DskipTests=false'
    }
  }
}


    stage('Package') {
      steps {
        echo '🔹 Étape 3 : Création du jar final...'
        sh 'mvn -B -DskipTests=true package'
      }
    }

    stage('Archive') {
      steps {
        echo '🔹 Étape 4 : Archivage du jar généré...'
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
  }

  

  post {
    success {
      echo '✅ Build terminé avec succès (Java 17 + H2 Test).'
    }
    failure {
      echo '❌ Échec du build ! Vérifie les logs Jenkins.'
    }
  }
}
