job('noops-pretested') {
	logRotator(-1,10)

 	triggers {
      githubPush()
    }
    wrappers {
      timestamps()
    }

    scm {
      git {

        remote {
          url("https://github.com/Praqma/noops.git")
          credentials('100247a2-70f4-4a4e-a9f6-266d139da9db')
        }

        branch("origin/ready/**")

        configure {
          node ->
          node / 'extensions' << 'hudson.plugins.git.extensions.impl.CleanBeforeCheckout' {}
        }
      }

    }

    wrappers {
      pretestedIntegration("SQUASHED",  "master", "origin")
      timestamps()
    }

    publishers {
      pretestedIntegration()
    }
  }
