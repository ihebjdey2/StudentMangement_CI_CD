pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'jdk17'
    }

    options {
        timestamps()
        skipDefaultCheckout(false)
        ansiColor('xterm') // 🎨 Active les couleurs dans les logs
    }

    environment {
        REGISTRY = 'iheb7u7'
        IMAGE_NAME = 'student-management'
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials'
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN_ID = 'sonar-token'
        NEXUS_CREDS = 'nexus-creds'
        MVN_SETTINGS = 'global-maven-settings'
    }

    stages {

        // 1️⃣ Étape : Récupération du code
        stage('Checkout') {
            steps {
                echo "\033[1;36m🔹 [Checkout] Récupération du code depuis GitHub...\033[0m"
                checkout scm
            }
        }

        // 2️⃣ Étape : Compilation + Tests
        stage('Build & Test') {
            steps {
                echo "\033[1;33m🧪 [Build & Test] Compilation du projet et exécution des tests unitaires...\033[0m"
                sh 'mvn -B clean verify -Dspring.profiles.active=test'
            }
            post {
                success {
                    echo "\033[1;32m✅ Tests passés avec succès !\033[0m"
                }
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // 3️⃣ Étape : Analyse SonarQube
        stage('SonarQube Analysis') {
            steps {
                echo "\033[1;34m🔍 [SonarQube] Analyse de la qualité du code en cours...\033[0m"
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

        // 4️⃣ Étape : Packaging
        stage('Package') {
            steps {
                echo "\033[1;35m📦 [Package] Création du livrable JAR...\033[0m"
                sh 'mvn -B -DskipTests package'
            }
        }

        // 5️⃣ Étape : Archivage
        stage('Archive') {
            steps {
                echo "\033[1;36m🗂️ [Archive] Archivage du JAR généré...\033[0m"
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        // 6️⃣ Étape : Déploiement sur Nexus
        stage('Deploy to Nexus') {
            steps {
                echo "\033[1;33m🚀 [Nexus] Déploiement de l’artéfact sur Nexus Repository...\033[0m"
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
                echo "\033[1;36m🐳 [Docker] Construction et push de l’image Docker vers DockerHub...\033[0m"
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKERHUB_CREDENTIALS}") {
                        def image = docker.build("${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}")
                        image.push()
                        image.push("latest")
                    }
                }
            }
        }

        // 8️⃣ Étape : Déploiement Application
        stage('Deploy Application') {
            steps {
                echo "\033[1;32m🚀 [Deploy] Déploiement de l’application sur Docker Compose...\033[0m"
                sh '''
                    echo "🧠 Vérification du réseau devops-net..."
                    docker network create devops-net || true

                    echo "🧹 Nettoyage de l'ancienne version..."
                    docker stop student-management || true
                    docker rm -f student-management || true

                    echo "🔨 Lancement du nouveau conteneur..."
                    docker compose -f docker-compose.yml up -d --build student-management
                '''
            }
        }

        // 9️⃣ Étape : Monitoring Stack
        stage('Monitoring Stack') {
            steps {
                echo "\033[1;34m📊 [Monitoring] Déploiement de Prometheus + Grafana...\033[0m"
                sh '''
                    cd monitoring
                    echo "📦 Téléchargement des images du stack..."
                    docker compose -f docker-compose.yml pull || true

                    echo "🚀 Démarrage du stack de monitoring..."
                    docker compose -f docker-compose.yml up -d

                    echo "✅ Monitoring stack démarré avec succès !"
                '''
            }
        }
    }

    // 🧩 Résumé final
    post {
        success {
            echo "\n\033[1;32m✅ PIPELINE TERMINÉ AVEC SUCCÈS ! 🎉\033[0m"
            echo "──────────────────────────────────────────────────────"
            echo "📦  Code compilé et testé"
            echo "🔍  Analyse SonarQube effectuée → http://localhost:9000"
            echo "🗂️  Artefact déployé sur Nexus → http://localhost:8081"
            echo "🐳  Image Docker poussée → https://hub.docker.com/r/${REGISTRY}/${IMAGE_NAME}"
            echo "🚀  Application déployée → http://localhost:8089/student/swagger-ui/index.html"
            echo "📈  Monitoring → Grafana: http://localhost:3000 | Prometheus: http://localhost:9090"
            echo "──────────────────────────────────────────────────────\n"
        }
        failure {
            echo "\033[1;31m❌ Le pipeline a échoué. Vérifie les logs Jenkins pour les détails.\033[0m"
        }
    }
}
