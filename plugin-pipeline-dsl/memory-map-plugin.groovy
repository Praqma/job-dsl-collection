import jobs.*

Globals.PLUGIN_NAME = 'memory-map-plugin'
Globals.JENKINS_WIKI = "https://wiki.jenkins-ci.org/display/JENKINS/Memory+Map+Plugin"

Globals.GIT_REPOSITORY_PRAQMA = "https://github.com/Praqma/memory-map-plugin.git"
Globals.GIT_REPOSITORY_JENKINSCI = "https://github.com/jenkinsci/memory-map-plugin.git"
Globals.GIT_CREDENTIALS_ID = "100247a2-70f4-4a4e-a9f6-266d139da9db"

Globals.NODE_LABEL = "jenkinsubuntu"

Globals.MAIL = 'thi@praqma.com'

def jobs = []

def integrate = new IntegrateJob()
integrate.pollingSchedule = "" // custom polling schedule
integrate.mvnTarget = "clean integration-test"
jobs.add(integrate)

def test = new TestJob()
test.mvnTarget = "clean -P usecaseTesting"
jobs.add(test)
integrate.setDownstreamJob(test.name)

def analysis = new AnalysisJob()
jobs.add(analysis)
test.setDownstreamJob(analysis.name)

def release = new ReleaseJob()
jobs.add(release)
analysis.setDownstreamJob(release.name, true) // manual trigger

def javaDoc = new JavaDocJob()
jobs.add(javaDoc)
release.setDownstreamJob(javaDoc.name)

def sync = new SyncJob()
jobs.add(sync)
javaDoc.setDownstreamJob(sync.name)

jobs.each { PipelineJob j ->
    j.execute(this) // Build all jobs
}