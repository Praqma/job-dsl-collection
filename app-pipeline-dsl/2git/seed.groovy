import javaposse.jobdsl.dsl.jobs.FreeStyleJob

def project_name = '2git'
def repo_name = 'Praqma/2git'
def cred_id = "100247a2-70f4-4a4e-a9f6-266d139da9db"

/** Integration **/
def integrationJob = job("$project_name-integrate") {
    defaults(delegate)
    description('pretested integration, \'gradle test\'')

    scm {
        git {
            remote {
                github(repo_name)
                credentials(cred_id)
            }
            branch('ready/**')
        }
    }

    configure {
        it / scm / extensions << 'hudson.plugins.git.extensions.impl.CleanBeforeCheckout' {}
        it / scm / extensions << 'hudson.plugins.git.extensions.impl.PruneStaleBranches' {}
    }

    triggers {
        githubPush()
    }

    wrappers {
        pretestedIntegration('SQUASHED', 'master', 'origin')
    }

    steps {
        gradle("test")
    }

    publishers {
        pretestedIntegration()
    }
}


/** UTILS **/
def defaults(FreeStyleJob job) {
    job.with {
        logRotator(-1, 10)

        wrappers {
            buildName('#${BUILD_NUMBER}-${GIT_REVISION,length=5}')
            timestamps()
            timeout {
                noActivity(360)
            }
        }
    }
}
