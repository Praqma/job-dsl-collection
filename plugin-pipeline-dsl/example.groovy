import jobs.*

Globals.PLUGIN_NAME = 'myPlugin'
Globals.JENKINS_WIKI = "https://wiki.jenkins-ci.org/display/JENKINS/My+Plugin"
Globals.GIT_REPOSITORY_PRAQMA = "git@github.com:Praqma/my-plugin.git"
Globals.GIT_REPOSITORY_JENKINSCI = "git@github.com:jenkinsci/my-plugin.git"
Globals.GIT_CREDENTIALS_ID = "1c876df788acc6f1f9"
Globals.MAIL = 'me@praqma.com'

def jobs = []

def integrate = new IntegrateJob()
jobs.add(integrate)
integrate.pollingSchedule = "H 4 1 * *" // custom polling schedule

def test = new TestJob()
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