//#######################################DOCKER IMAGE INFRASTRUCTURE#######################################
def currentDockerImages = ['linkchecker', 'gh-pages', 'pac', 'image-size-checker', 'geb']

//Convention: all our docker image repos are prefixed with "docker-"
def githubUrl = 'https://github.com/Praqma/docker-'
def branchName = "master" //"\${BRANCH}"
def releasePraqmaCredentials = '100247a2-70f4-4a4e-a9f6-266d139da9db'
def dockerHostLabel = 'docker'

currentDockerImages.each { image ->
  def cloneUrl = "${githubUrl}"+"${image}"

  //Verification job bame
  def verifyName = "Web_Docker_"+"${image}"+"-verify"

  //Publish job name
  def publishName = "Web_Docker_"+"${image}"+"-publish"

  //Docker repo name
  def dRepoName = "praqma/${image}"

  job(verifyName) {
	label(dockerHostLabel)
	logRotator(-1,10)
    wrappers {
      timestamps()
    }

    triggers {
      githubPush()
    }

    scm {
      git {

        remote {
          url(cloneUrl)
          credentials(releasePraqmaCredentials)
        }

        branch(branchName)

        configure {
          node ->
          node / 'extensions' << 'hudson.plugins.git.extensions.impl.CleanBeforeCheckout' {}
        }
      }
    }

    steps {
      shell("docker build -t praqma/${image}:snapshot .")
      shell('./test.sh')
	   shell("docker rmi praqma/${image}:snapshot")
    }

    publishers {
      buildPipelineTrigger(publishName) {
        parameters{
          gitRevision(false)
        }
      }
	  mailer('', false, false)
    }
  }

  //Publish jobs
  job(publishName) {
    label(dockerHostLabel)
	logRotator(-1,10)
    wrappers {
      timestamps()
    }
    scm {
      git {

        remote {
          url(cloneUrl)
          credentials(releasePraqmaCredentials)
        }

        branch(branchName)
          configure {
            node ->
            node / 'extensions' << 'hudson.plugins.git.extensions.impl.CleanBeforeCheckout' {}
          }
        configure {
          node ->
          node / 'extensions' << 'hudson.plugins.git.extensions.impl.UserIdentity' {
            name("praqma");
            email("support@praqma.net");
          }
        }
      }
    }

    steps {
      dockerBuildAndPublish {
        repositoryName(dRepoName)
        tag('1.${BUILD_NUMBER}')
        registryCredentials('docker-hub-crendential')
        dockerHostURI('unix:///var/run/docker.sock')
        forcePull(false)
        createFingerprints(false)
        skipDecorate()
      }
    }

    publishers {
      git {
        pushOnlyIfSuccess()
        branch('origin', branchName)
        tag('origin', '1.${BUILD_NUMBER}') {
          message('Tagged with 1.${BUILD_NUMBER} using Jenkins')
          create()
        }
      }
      mailer('', false, false)
    }
  }
}
//#########################################################################################################