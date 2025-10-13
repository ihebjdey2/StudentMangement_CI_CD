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
        REGISTRY = 'iheb7u7'                // 🔹 ton nom DockerHub
        IMAGE_NAME = 'student-management'       // 🔹 nom de l'image Docker
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials'
        SONARQUBE_ENV = 'SonarQube'             // 🔹 nom du serveur configuré dans Jenkins
        SONAR_TOKEN_ID = 'sonar-token'
        NEXUS_CREDS = 'nexus-creds'
        MVN_SETTINGS = 'global-maven-settings'
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
                    echo "🧠 Vérification des services existants (SonarQube, Nexus)..."
                    # Si SonarQube ou Nexus sont déjà actifs → ne rien faire
                    if [ ! "$(docker ps -q -f name=nexus)" ]; then
                      if [ "$(docker ps -aq -f status=exited -f name=nexus)" ]; then
                        echo "▶️ Redémarrage du conteneur Nexus existant..."
                        docker start nexus
                      else
                        echo "🚀 Création du conteneur Nexus..."
                        docker run -d --name nexus --network devops-net -p 8081:8081 sonatype/nexus3:latest
                      fi
                    fi

                    if [ ! "$(docker ps -q -f name=sonarqube)" ]; then
                      if [ "$(docker ps -aq -f status=exited -f name=sonarqube)" ]; then
                        echo "▶️ Redémarrage du conteneur SonarQube existant..."
                        docker start sonarqube
                      else
                        echo "🚀 Création du conteneur SonarQube..."
                        docker run -d --name sonarqube --network devops-net -p 9000:9000 sonarqube:lts-community
                      fi
                    fi

                    echo "🧱 Préparation du réseau et du déploiement..."
                    docker network create devops-net || true

                    echo "♻️ Lancement du docker-compose (sans recréer Nexus/Sonar)..."
                    docker-compose down || true
                    docker-compose up -d --no-recreate --build
                '''
            }
        }
        
        stage('Monitoring Stack') {
            steps {
                sh '''
                docker compose -f docker-compose.monitoring.yml pull || true
                docker compose -f docker-compose.monitoring.yml up -d
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
