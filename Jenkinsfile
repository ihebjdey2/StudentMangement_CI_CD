pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'jdk17'
    }

    options {
        timestamps()
        skipDefaultCheckout(false)
    }

    environment {
        REGISTRY = 'ihebjdey'                   // 🔹 ton nom DockerHub
        IMAGE_NAME = 'student-management'       // 🔹 nom de l'image Docker
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials'
        SONARQUBE_ENV = 'SonarQube'             // 🔹 nom du serveur configuré dans Jenkins
        SONAR_TOKEN_ID = 'sonar-token'
        NEXUS_CREDS = 'nexus-creds'
        MVN_SETTINGS = '45b9f137-add9-4bd7-968e-a424d3e0f31c	'  // id de ton settings.xml dans Jenkins
    }

    stages {

        // 1️⃣ Étape : Récupération du code
        stage('Checkout') {
            steps {
                echo '🔹 Étape 1 : Récupération du code depuis GitHub...'
                checkout scm
            }
        }

        // 2️⃣ Étape : Compilation + Tests
        stage('Build & Test') {
            steps {
                echo '🔹 Étape 2 : Compilation et tests unitaires...'
                sh 'mvn -B clean verify -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // 3️⃣ Étape : Analyse SonarQube
        stage('SonarQube Analysis') {
            steps {
                echo '🔹 Étape 3 : Analyse de qualité avec SonarQube...'
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    withCredentials([string(credentialsId: "${SONAR_TOKEN_ID}", variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \
                              -Dsonar.projectKey=${IMAGE_NAME} \
                              -Dsonar.host.url=http://sonarqube:9000 \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }

        // 4️⃣ Étape : Packaging (JAR)
        stage('Package') {
            steps {
                echo '🔹 Étape 4 : Création du livrable JAR...'
                sh 'mvn -B -DskipTests package'
            }
        }

        // 5️⃣ Étape : Archivage du JAR
        stage('Archive') {
            steps {
                echo '🔹 Étape 5 : Archivage du JAR généré...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        // 6️⃣ Étape : Déploiement sur Nexus
        stage('Deploy to Nexus') {
            steps {
                echo '🔹 Étape 6 : Déploiement sur Nexus...'
                withCredentials([usernamePassword(credentialsId: "${NEXUS_CREDS}", usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    withMaven(maven: 'M3', globalMavenSettingsConfig: "${MVN_SETTINGS}") {
                        sh 'mvn clean deploy -DskipTests'
                    }
                }
            }
        }

        // 7️⃣ Étape : Build & Push Docker Image
        stage('Build & Push Docker Image') {
            steps {
                echo '🔹 Étape 7 : Construction et push de l’image Docker...'
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKERHUB_CREDENTIALS}") {
                        def image = docker.build("${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}")
                        image.push()
                        image.push("latest")
                    }
                }
            }
        }

        // 8️⃣ Étape : Déploiement avec Docker Compose
        stage('Deploy with Docker Compose') {
            steps {
                echo '🔹 Étape 8 : Lancement du déploiement via docker-compose...'
                sh '''
                    docker network create devops-net || true
                    docker-compose down || true
                    docker-compose up -d --build
                '''
            }
        }
    }

    // 🧩 Étape finale : Résumé global
    post {
        success {
            echo '''
            ✅ Pipeline terminé avec succès !
            - Code compilé et testé
            - Analyse SonarQube effectuée
            - Artefact déployé sur Nexus
            - Image Docker poussée sur DockerHub
            - Application déployée via docker-compose

            🌐 Accès à l’application : http://localhost:8089/student/swagger-ui/index.html
            '''
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifie les logs Jenkins pour les détails.'
        }
    }
}
