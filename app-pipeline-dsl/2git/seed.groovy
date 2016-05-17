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
            branch('*/ready/*')
            extensions {
                cleanBeforeCheckout()
                pruneBranches()
            }
        }
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
        downstreamParameterized {
            trigger("$project_name-docs") {
                parameters {
                    gitRevision(true)
                }
            }
        }
    }
}

def docsJob = job("$project_name-docs") {
    defaults(delegate)
    description('updates gh-pages documentation')

    scm {
        git {
            remote {
                github(repo_name)
                credentials(cred_id)
            }
            branch('master')
            extensions {
                wipeOutWorkspace()
            }
        }
    }

    steps {
        shell(  'git checkout master\n' +
                'sha1=$(git subtree split -q --prefix docs master)\n' +
                'git checkout -q gh-pages\n' +
                'git merge --ff-only $sha1')
    }

    publishers {
        git {
            pushOnlyIfSuccess()
            branch('origin', 'gh-pages')
        }
    }
}


/** UTILS **/
def defaults(def job) {
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
