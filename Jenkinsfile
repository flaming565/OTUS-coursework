#!groovy
pipeline {
    agent {
        docker {
            image 'android_beauty_diary'
            args '-it --memory=12g --cpus="4"'
        }
    }
    parameters {
        string(
                name: "branch",
                defaultValue: "master",
                description: "Текущая ветка"
        )
    }
    stages {
        stage('clone') {
            steps {
                checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/master']],
                        extensions: [],
                        userRemoteConfigs: [[url: 'https://github.com/flaming565/OTUS-coursework.git']]
                ])
            }
        }
        stage("init") {
            steps {
                sh "chmod +x gradlew"
                sh "./gradlew"
            }
        }
        stage("lint") {
            when {
                expression { return "$branch" == "features/lint_homework" }
            }
            steps {
                sh "./gradlew lintDebug"
            }
        }
        stage("test") {
            steps {
                sh "./gradlew testDebugUnitTest"
            }
        }
        stage("build") {
            steps {
                sh "./gradlew assembleDebug"
            }
        }
    }
    post {
        always {
            archiveArtifacts(artifacts: '**/build/reports/**', allowEmptyArchive: true)
        }
    }
}