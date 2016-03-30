package jobs

import utils.*

class JavaDocJob implements PipelineJob {
    String name = Globals.PLUGIN_NAME + "_javadoc"
    String downstreamJobName
    boolean isDownstreamJobManual = false;
    String nodeLabel = Globals.NODE_LABEL
    String jdkVersion = Globals.JDK

    @Override
    void setDownstreamJob(String downstreamJobName, boolean manual = false) {
        this.downstreamJobName = downstreamJobName
        this.isDownstreamJobManual = manual
    }

    @Override
    void execute(def context) {
        def buildName = name + "-build"
        def publishName = name + "-publish"

        context.job(buildName) {
            label(nodeLabel)
            jdk(jdkVersion)
            Properties.discardOldBuilds(delegate, Globals.MAX_DAYS, Globals.MAX_BUILDS)
            Properties.permissionToCopyArtifacts(delegate, publishName)

            Scm.gitScm(delegate, Globals.GIT_REPOSITORY_PRAQMA, Globals.GIT_INTEGRATION_BRANCH, Globals.GIT_CREDENTIALS_ID)

            Steps.executeShell(delegate, '''# Checkout the latest tag
git checkout $(git describe --abbrev=0 --tags)''')
            Steps.executeGroovy(delegate, '''//EnvInject the POM version for later tagging
def workspace = new File(getClass().protectionDomain.codeSource.location.path).parent
def project = new XmlSlurper().parse(new File("$workspace/pom.xml"))
new File("env.var").write("MVN_VERSION=" + project.version.toString())''')
            Steps.readEnvironmentVariablesFile(delegate, 'env.var')
            Steps.mvn(delegate, "clean javadoc:javadoc")
            Steps.executeShell(delegate,
                    $/
# Zip JDoc
cd target/site/apidocs/
zip -r apidocs.zip *

# Copy JDoc to root
cd ../../..
cp target/site/apidocs/apidocs.zip apidocs.zip
/$)

            Wrappers.setBuildName(delegate)
            Wrappers.setTimeout(delegate)
            Wrappers.addTimestamps(delegate)

            Publishers.publishArtifacts(delegate, 'apidocs.zip')
            Publishers.mail(delegate, Globals.MAIL)
            delegate.publishers {
                downstreamParameterized {
                    trigger(publishName) {
                        parameters {
                            propertiesFile('env.var', true)
                            predefinedProp('UPSTREAM_BUILD_NUMBER', '${BUILD_NUMBER}')
                        }
                    }
                }
            }
        }

        context.job(publishName) {
            label(nodeLabel)
            jdk(jdkVersion)
            Properties.discardOldBuilds(delegate, Globals.MAX_DAYS, Globals.MAX_BUILDS)

            Scm.gitScm(delegate, 'https://github.com/Praqma/javadoc.git', 'gh-pages', Globals.GIT_CREDENTIALS_ID)

            Steps.CopyArtifactsFromJob(delegate, buildName, '${UPSTREAM_BUILD_NUMBER}', 'apidocs.zip')
            Steps.executeShell(delegate, """
            # Create directory structure and copy in JDoc
            mkdir -p $Globals.PLUGIN_NAME/\$MVN_VERSION
            cp apidocs.zip $Globals.PLUGIN_NAME/\$MVN_VERSION/apidocs.zip
            cd $Globals.PLUGIN_NAME/\$MVN_VERSION

            # Wipe out the worktree except for the archive and artifacts
            ls | grep -v 'apidocs.zip' | xargs rm -rf

            # Unzip and delete the archive
            unzip apidocs.zip
            rm -f apidocs.zip

            # Move up to plugin documentation root and write redirect file
            cd ..
            {
                echo "<!DOCTYPE html>";
                echo "<meta charset=\\\"utf-8\\\">";
                echo "<title>Redirecting...</title>";
                echo "<link rel=\\\"canonical\\\" href=\\\"\$MVN_VERSION\\\">";
                echo "<meta http-equiv=\\\"refresh\\\" content=\\\"0\\\" url=\\\"\$MVN_VERSION\\\">";
                echo "<h1>Redirecting to latest documentation...</h1>";
                echo "<a href=\\\"\$MVN_VERSION\\\">Click here if you are not redirected.</a>";
                echo "<script>location=\\\"\$MVN_VERSION\\\"</script>";
            } > index.html

            # Commit changes
            git add .
            git commit -m"Added JavaDoc for $Globals.PLUGIN_NAME (\$MVN_VERSION)"
""")

            Wrappers.setBuildName(delegate)
            Wrappers.setTimeout(delegate)
            Wrappers.addTimestamps(delegate)

            delegate.publishers {
                git {
                    branch('origin', 'gh-pages')
                    pushOnlyIfSuccess()
                    tag('origin', "$Globals.PLUGIN_NAME-\${MVN_VERSION}") {
                        create()
                    }
                }
            }
            Publishers.mail(delegate, Globals.MAIL)
            if (downstreamJobName) {
                if (isDownstreamJobManual)
                    Publishers.manualDownstreamJob(delegate, downstreamJobName)
                else
                    Publishers.runDownstreamJob(delegate, downstreamJobName)
            }
        }
    }
}