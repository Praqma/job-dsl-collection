package jobs

import utils.*

class ReleaseJob implements PipelineJob {
    String name = Globals.PLUGIN_NAME + "_release"
    String downstreamJobName
    boolean isDownstreamJobManual = false;
    String nodeLabel = Globals.NODE_LABEL
    String jdkVersion = Globals.JDK

    String mvnTarget = "release:clean release:prepare release:perform -B"

    @Override
    void setDownstreamJob(String downstreamJobName, boolean manual = false) {
        this.downstreamJobName = downstreamJobName
        this.isDownstreamJobManual = manual
    }

    @Override
    void execute(def context) {
        context.job(name){
            label(nodeLabel)
            jdk(jdkVersion)
            Properties.discardOldBuilds(delegate, Globals.MAX_DAYS, Globals.MAX_BUILDS)

            Scm.gitScm(delegate, Globals.GIT_REPOSITORY_PRAQMA, Globals.GIT_INTEGRATION_BRANCH, Globals.GIT_CREDENTIALS_ID)

            Steps.executeShell(delegate,
                    "# Checks if the changelog was updated for the release " +
                            "./check_for_changelog.rb $Globals.JENKINS_WIKI")
            Steps.mvn(delegate, mvnTarget)

            Wrappers.setBuildName(delegate)
            Wrappers.setTimeout(delegate)
            Wrappers.addTimestamps(delegate)

            Publishers.mail(delegate, Globals.MAIL)
            if(downstreamJobName){
                if(isDownstreamJobManual)
                    Publishers.manualDownstreamJob(delegate, downstreamJobName)
                else
                    Publishers.runDownstreamJob(delegate, downstreamJobName)
            }
        }
    }
}
