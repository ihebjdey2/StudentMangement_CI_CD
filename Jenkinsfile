pipeline {
  agent any

  tools {
    maven 'M3'
    jdk 'jdk17'
  }

  options { timestamps() }

  stages {

    // -------------------------------
    // 1️⃣ Étape : Récupération du code
    // -------------------------------
    stage('Checkout') {
      steps {
        echo '🔹 Étape 1 : Récupération du code depuis GitHub...'
        checkout scm
      }
    }

    // -------------------------------
    // 2️⃣ Étape : Compilation + Tests
    // -------------------------------
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

    // -------------------------------
    // 3️⃣ Étape : Analyse SonarQube
    // -------------------------------
    stage('SonarQube Analysis') {
      steps {
        echo '🔹 Étape 3 : Analyse de qualité avec SonarQube...'
        withSonarQubeEnv('sonarqube') {
          withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
            sh """
              mvn sonar:sonar \
                -Dsonar.projectKey=student-management \
                -Dsonar.host.url=http://sonarqube:9000 \
                -Dsonar.login=${SONAR_TOKEN} \
                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
            """
          }
        }
      }
    }

    // -------------------------------
    // 4️⃣ Étape : Packaging (jar)
    // -------------------------------
    stage('Package') {
      steps {
        echo '🔹 Étape 4 : Création du jar final...'
        sh 'mvn -B -DskipTests=true package'
      }
    }

    // -------------------------------
    // 5️⃣ Étape : Archivage du jar
    // -------------------------------
    stage('Archive') {
      steps {
        echo '🔹 Étape 5 : Archivage du jar généré...'
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }

    // -------------------------------
    // 6️⃣ Étape : Déploiement Nexus
    // -------------------------------
   stage('Deploy to Nexus') {
  steps {
    echo '🔹 Étape 6 : Déploiement du jar sur Nexus Repository...'
    withCredentials([usernamePassword(credentialsId: 'nexus-creds', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
      withMaven(
        maven: 'M3',
        globalMavenSettingsConfig: 'MyGlobalSettings'
      ) {
        sh """
          mvn deploy -DskipTests \
            -Dnexus.username=$NEXUS_USER \
            -Dnexus.password=$NEXUS_PASS
        """
      }
    }
  }
}

  // -------------------------------
  // Post actions globales
  // -------------------------------
  post {
    success {
      echo '✅ Pipeline terminé avec succès (Build + Tests + Sonar + Nexus).'
    }
    failure {
      echo '❌ Échec du pipeline ! Vérifie les logs Jenkins.'
    }
  }
}
