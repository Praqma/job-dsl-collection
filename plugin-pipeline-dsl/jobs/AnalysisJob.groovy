package jobs

import utils.*

class AnalysisJob implements PipelineJob {
    String name = Globals.PLUGIN_NAME + "_analysis"
    String downstreamJobName
    boolean isDownstreamJobManual = false;
    String nodeLabel = Globals.NODE_LABEL
    String jdkVersion = Globals.JDK

    String mvnTarget = "clean cobertura:cobertura findbugs:findbugs checkstyle:checkstyle pmd:pmd pmd:cpd javancss:check javadoc:javadoc jdepend:generate site package"

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

            Steps.mvn(delegate, mvnTarget)

            Wrappers.setBuildName(delegate)
            Wrappers.setTimeout(delegate)
            Wrappers.addTimestamps(delegate)

            Publishers.javaNCSS(delegate)
            Publishers.compilerWarnings(delegate, 'Maven', 'JavaDoc Tool')
            Publishers.coberturaReport(delegate)
            Publishers.findBugsWarnings(delegate)
            Publishers.pmdWarnings(delegate)
            Publishers.duplicateCode(delegate)
            Publishers.checkstyleWarnings(delegate)
            Publishers.openTasks(delegate)
            Publishers.javaDoc(delegate)
            delegate.publishers {
                analysisCollector {
                    checkstyle()
                    dry()
                    findbugs()
                    pmd()
                    tasks()
                    warnings()
                }
                publishHtml {
                    report('target/site') {
                        reportName('Java NCSS HTML Report')
                        reportFiles('javancss.html')
                        keepAll()
                        alwaysLinkToLastBuild()
                    }
                    report('target/site') {
                        reportName('Maven site HTML Report')
                        reportFiles('index.html')
                        keepAll()
                        alwaysLinkToLastBuild()
                    }
                }
            }

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
