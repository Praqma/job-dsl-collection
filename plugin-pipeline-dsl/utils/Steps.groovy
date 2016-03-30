package utils

class Steps {
    static def mvn(def job, String target) {
        job.with {
            steps {
                shell("mvn " + target)
            }
        }
    }

    static def CopyArtifactsFromJob(def job, String jobName, jobNumber, artifacts) {
        job.with {
            steps {
                copyArtifacts(jobName) {
                    includePatterns(artifacts)
                    buildSelector {
                        buildNumber(jobNumber)
                    }
                }
            }
        }
    }

    static def executeShell(def job, String command) {
        job.with {
            steps {
                shell(command)
            }
        }
    }

    static def executeGroovy(def job, String command) {
        job.with {
            steps {
                groovyCommand(command)
            }
        }
    }

    static def readEnvironmentVariablesFile(def job, String file) {
        job.with {
            steps {
                environmentVariables {
                    propertiesFile(file)
                }
            }
        }
    }
}
