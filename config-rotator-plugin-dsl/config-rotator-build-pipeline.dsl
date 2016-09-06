projectName = "Config-Rotator-MBA"

buildJobName = "A1.Build-${projectName}_GEN"
staticAnalName = "B1.Static-Analysis-${projectName}_GEN"
docName = "C1.JavaDoc-${projectName}_GEN"
integrationTestName = "D1.IntegrationTest-${projectName}_GEN"
releaseJobName = "E1.Release-${projectName}_GEN"
viewName = "${projectName}-jobs_GEN"
pipelineName = "${projectName}-pipeline_GEN"


job(buildJobName) {
//    name 'MBA - Config Rotatour - Build'
    description 'MBA A1 Config Rotator - Build'
    jdk ('1.8-LATEST')
    label ('cc-nightcrawler')
    scm {
        git 'https://github.com/Praqma/config-rotator-plugin.git'
    }
    triggers {
        scm('*/2 * * * *')
    }
    steps {
        maven 'clean package -Pcobertura -PunitTests'
    }
    publishers {
        archiveJunit('target/surefire-reports/*.xml')
        cobertura('**/coverage.xml') // TODO: Doesn't seem to work in docker jenkins - module missing?
        downstreamParameterized {
            trigger(staticAnalName) {
                condition('UNSTABLE_OR_BETTER') // trigger even if unstable
            }
        }
    }
    // TODO: Missing "Project based security" - default?

}


job(staticAnalName) {
    description 'MBA B1 Config Rotator - Static Analysis'
    label ('cc-nightcrawler')
    logRotator(-1,10)
    scm {
        git 'https://github.com/Praqma/config-rotator-plugin.git'
    }
    // Triggered by pipeline
    steps {
        maven 'clean package -Pstatic'
    }
    publishers {
        findbugs('**/findbugsXml.xml')
        analysisCollector {
            dry()
            findbugs()
            pmd()
            tasks()
            //TODO: Run always missing?
        }
        downstreamParameterized {
            trigger(docName) {
                condition('SUCCESS')
            }
        }

    }
    // TODO: "Scan workspace for open tasks - Task Scanner Plugin"
}

job(docName) {
    description('Job that create the javadoc for Config Rotator Plugin')
    logRotator(-1,10)
    label ('cc-nightcrawler')
    scm {
        git 'https://github.com/Praqma/config-rotator-plugin.git'
    }
    steps {
        maven 'clean javadoc:javadoc'
    }
    publishers {
        archiveJavadoc {
            javadocDir('target/site/apidocs')
        }
        downstreamParameterized {
            trigger(integrationTestName) {
                condition('SUCCESS')
            }
        }

    }
}

job(integrationTestName) {
    logRotator(-1,10)
    label ('cc-nightcrawler')
    scm {
        git 'https://github.com/Praqma/config-rotator-plugin.git'
    }
    steps {
        maven 'clean package -PfunctionalTests'
    }
    publishers {
        archiveArtifacts('target/config-rotator.hpi')
        archiveJunit('target/surefire-reports/TEST*.xml')
        downstreamParameterized {
            trigger(releaseJobName) { // Next job should be manually triggered
                condition('SUCCESS')
            }
        }
    }
}

job(releaseJobName) {
    description('Config rotator plugin release job. I\'ve tagged out jenkinsubuntu machine with the \'release\' tag. So when creating jobs specifically for release. Don\'t poll! Since the process involves alot of steps, i would recommend only\n' +
            'to do the actual release and nothing else (that includes coverage, static analysis...etc). \n' +
            '\n' +
            'Your pom must have the scpexe element and wagon-ssh-external extension.\n' +
            '\n' +
            '\n' +
            'Steps that needs to be ensure in order for a succesful build: \n' +
            '<ul>\n' +
            '  <li>Currently we only have one release slave. Jenkinsubuntu, a virtual machine. This must be online. The user is praqma. If it not online connect to 10.10.1.39 using VNC log in as praqma and start a jnlp client on /ci</li>\n' +
            '  <li>There must be no build errors</li>\n' +
            '  <li>The user identity must have commit access to GitHub</li>\n' +
            '</ul>')
    logRotator(-1, 25)
    jdk('1.8-LATEST')
    label('jenkinsubuntu')
    scm {
        git 'https://github.com/Praqma/config-rotator-plugin.git'
    }
    steps {
        shell('''git checkout master
mvn release:clean release:prepare release:perform -B -Pjenkins ''')
    }
    // TODO: Wipe clean
}

listView(viewName) {
    description("All ${projectName} project related jobs")
    jobs {
        regex(".*-${projectName}.*")
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

buildPipelineView(pipelineName) {
    title("Project ${projectName} CI Pipeline")
    displayedBuilds(50)
    selectedJob("${buildJobName}")
    alwaysAllowManualTrigger()
    showPipelineParametersInHeaders()
    showPipelineParameters()
    showPipelineDefinitionHeader()
    refreshFrequency(60)
}
