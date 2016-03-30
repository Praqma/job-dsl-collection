package utils

class Scm {
    static def gitScm(def job, String repo, String branchName, String credentialsId) {
        job.with {
            scm {
                git {
                    remote {
                        url(repo)
                        credentials(credentialsId)
                    }
                    branch(branchName)
                    configure {
                        it / 'extensions' << 'hudson.plugins.git.extensions.impl.CleanBeforeCheckout' {}
                        it / 'extensions' << 'hudson.plugins.git.extensions.impl.PruneStaleBranches' {}
                    }
                }

            }
        }
    }
}
