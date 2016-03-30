package jobs

import utils.*

class SyncJob implements PipelineJob {
    String name = Globals.PLUGIN_NAME + "_sync"
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
        context.job(name){
            label(nodeLabel)
            jdk(jdkVersion)
            Properties.discardOldBuilds(delegate, Globals.MAX_DAYS, Globals.MAX_BUILDS)

            Scm.gitScm(delegate, Globals.GIT_REPOSITORY_PRAQMA, Globals.GIT_INTEGRATION_BRANCH, Globals.GIT_CREDENTIALS_ID)

            Steps.executeShell(delegate,
                    "git checkout $Globals.GIT_INTEGRATION_BRANCH\n" +
                            "git fetch --tags $Globals.GIT_REPOSITORY_PRAQMA\n" +
                            "git push $Globals.GIT_REPOSITORY_JENKINSCI $Globals.GIT_INTEGRATION_BRANCH\n" +
                            "git push $Globals.GIT_REPOSITORY_JENKINSCI --tags\n\n" +
                            "git checkout gh-pages\n" +
                            "git fetch --tags $Globals.GIT_REPOSITORY_PRAQMA\n" +
                            "git push $Globals.GIT_REPOSITORY_JENKINSCI gh-pages\n" +
                            "git push $Globals.GIT_REPOSITORY_JENKINSCI --tags")

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
