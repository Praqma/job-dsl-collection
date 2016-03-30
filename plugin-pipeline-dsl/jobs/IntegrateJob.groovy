package jobs
import utils.*

class IntegrateJob implements PipelineJob{
    String name = Globals.PLUGIN_NAME + "_integrate"
    String downstreamJobName
    boolean isDownstreamJobManual = false;
    String nodeLabel = Globals.NODE_LABEL
    String jdkVersion = Globals.JDK

    String deliveryBranch = "*/ready/*"
    String mvnTarget = "clean test"
    String pollingSchedule = "H/15 * * * *"

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

            Scm.gitScm(delegate, Globals.GIT_REPOSITORY_PRAQMA, deliveryBranch, Globals.GIT_CREDENTIALS_ID)
            Triggers.pollScm(delegate, pollingSchedule)

            Steps.mvn(delegate, mvnTarget)

            Wrappers.configurePretestedIntegration(delegate, Globals.GIT_INTEGRATION_BRANCH)
            Wrappers.setBuildName(delegate)
            Wrappers.setTimeout(delegate)
            Wrappers.addTimestamps(delegate)

            Publishers.configurePretestedIntegration(delegate)
            Publishers.publishJUnit(delegate)
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