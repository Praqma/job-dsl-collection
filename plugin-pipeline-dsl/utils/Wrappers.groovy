package utils

class Wrappers {
    static def setBuildName(def job) {
        job.with {
            wrappers {
                buildName('#${BUILD_NUMBER}-${GIT_BRANCH}(${GIT_REVISION,length=5})')
            }
        }
    }

    static def configurePretestedIntegration(def job, String integrationBranch) {
        job.with {
            wrappers {
                pretestedIntegration('SQUASHED', integrationBranch, 'origin')
            }
        }
    }

    static def setTimeout(def job) {
        job.with {
            wrappers {
                timeout {
                    noActivity(480)
                    failBuild()
                    writeDescription('Build failed due to timeout after 5 minutes')
                }
            }
        }
    }

    static def addTimestamps(def job) {
        job.with {
            wrappers {
                timestamps()
            }
        }
    }
}
