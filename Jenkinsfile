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
        REGISTRY = 'iheb7u7'
        IMAGE_NAME = 'student-management'
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials'
        SONARQUBE_ENV = 'SonarQube'
        SONAR_TOKEN_ID = 'sonar-token'
        NEXUS_CREDS = 'nexus-creds'
        MVN_SETTINGS = 'global-maven-settings'
    }

    stages {

        // 1️⃣ Étape : Récupération du code source
        stage('Checkout') {
            steps {
                echo "------------------------------------------------------------"
                echo "🔹 [Checkout] Récupération du code depuis GitHub..."
                echo "------------------------------------------------------------"
                checkout scm
            }
        }

        // 2️⃣ Étape : Compilation et Tests
        stage('Build & Test') {
            steps {
                echo "------------------------------------------------------------"
                echo "🧪 [Build & Test] Compilation du projet et exécution des tests unitaires..."
                echo "------------------------------------------------------------"
                sh 'mvn -B clean verify -Dspring.profiles.active=test'

                script {
                    echo "📊 Extraction du résumé des tests..."
                    def reportFile = sh(script: "find . -type f -name 'TEST-*.xml' | head -n 1", returnStdout: true).trim()

                    if (reportFile) {
                        def content = readFile(reportFile)
                        def tests = (content =~ /tests="([0-9]+)"/)[0][1].toInteger()
                        def failures = (content =~ /failures="([0-9]+)"/)[0][1].toInteger()
                        def errors = (content =~ /errors="([0-9]+)"/)[0][1].toInteger()
                        def skipped = (content =~ /skipped="([0-9]+)"/)[0][1].toInteger()
                        def passed = tests - failures - errors - skipped

                        echo "------------------------------------------------------------"
                        echo "🧾 RÉSULTATS DES TESTS UNITAIRES"
                        echo "------------------------------------------------------------"
                        echo "🧩 Total de tests exécutés : ${tests}"
                        echo "✅ Réussis : ${passed}"
                        echo "❌ Échecs : ${failures}"
                        echo "💥 Erreurs : ${errors}"
                        echo "⚠️ Ignorés : ${skipped}"
                        echo "------------------------------------------------------------"

                        if (failures > 0 || errors > 0) {
                            error("❌ Certains tests ont échoué (${failures} failures, ${errors} errors). Pipeline interrompu.")
                        }
                    } else {
                        echo "⚠️ Aucun rapport de test trouvé. Vérifie la configuration Maven Surefire."
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
                success {
                    echo "✅ Tous les tests unitaires sont passés avec succès."
                }
            }
        }

        // 3️⃣ Étape : Analyse de qualité SonarQube
        stage('SonarQube Analysis') {
            steps {
                echo "------------------------------------------------------------"
                echo "🔍 [SonarQube] Analyse de la qualité du code..."
                echo "------------------------------------------------------------"
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

        // 4️⃣ Étape : Création du livrable JAR
        stage('Package') {
            steps {
                echo "------------------------------------------------------------"
                echo "📦 [Package] Création du livrable JAR..."
                echo "------------------------------------------------------------"
                sh 'mvn -B -DskipTests package'
            }
        }

        // 5️⃣ Étape : Archivage du livrable
        stage('Archive') {
            steps {
                echo "------------------------------------------------------------"
                echo "🗂️ [Archive] Archivage du JAR généré..."
                echo "------------------------------------------------------------"
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        // 6️⃣ Étape : Déploiement sur Nexus Repository
        stage('Deploy to Nexus') {
            steps {
                echo "------------------------------------------------------------"
                echo "🚀 [Nexus] Déploiement de l’artéfact sur Nexus..."
                echo "------------------------------------------------------------"
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
                echo "------------------------------------------------------------"
                echo "🐳 [Docker] Construction et publication de l’image Docker..."
                echo "------------------------------------------------------------"
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKERHUB_CREDENTIALS}") {
                        def image = docker.build("${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}")
                        image.push()
                        image.push("latest")
                    }
                }
            }
        }

        // 8️⃣ Étape : Déploiement de l’application
        stage('Deploy Application') {
            steps {
                echo "------------------------------------------------------------"
                echo "🚀 [Deploy] Déploiement de l’application via Docker Compose..."
                echo "------------------------------------------------------------"
                sh '''
                    echo "🧠 Vérification du réseau devops-net..."
                    docker network create devops-net || true

                    echo "🧹 Nettoyage de l'ancienne version..."
                    docker stop student-management || true
                    docker rm -f student-management || true

                    echo "🔨 Lancement de la nouvelle image..."
                    docker compose -f docker-compose.yml up -d --build student-management
                '''
            }
        }

        // 9️⃣ Étape : Monitoring Stack (Prometheus + Grafana)
        stage('Monitoring Stack') {
            steps {
                echo "------------------------------------------------------------"
                echo "📊 [Monitoring] Déploiement du stack de monitoring..."
                echo "------------------------------------------------------------"
                sh '''
                    cd monitoring
                    echo "📦 Téléchargement des images..."
                    docker compose -f docker-compose.yml pull || true

                    echo "🚀 Démarrage du stack..."
                    docker compose -f docker-compose.yml up -d

                    echo "✅ Monitoring stack démarré avec succès !"
                '''
            }
        }
    }

    // 🔚 Résumé global du pipeline
    post {
        success {
            echo ""
            echo "=============================================================="
            echo "✅ PIPELINE TERMINÉ AVEC SUCCÈS 🎯"
            echo "--------------------------------------------------------------"
            echo "📦  Code compilé et testé avec succès"
            echo "🔍  Analyse SonarQube → http://localhost:9000"
            echo "🗂️  Artefact Nexus → http://localhost:8081"
            echo "🐳  Image DockerHub → https://hub.docker.com/r/${REGISTRY}/${IMAGE_NAME}"
            echo "🚀  Application → http://localhost:8089/student/swagger-ui/index.html"
            echo "📈  Monitoring → Grafana: http://localhost:3000 | Prometheus: http://localhost:9090"
            echo "=============================================================="
        }
        failure {
            echo "❌ Le pipeline a échoué. Consulte les logs Jenkins pour plus de détails."
        }
    }
}
