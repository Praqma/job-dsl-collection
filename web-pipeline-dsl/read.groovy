def thr = Thread.currentThread()
def build = thr?.executable
def envVarsMap = build.parent.builds[0].properties.get("envVars")

def currentDockerImages = [envVarsMap['dockerimage_linkchecker'],
							envVarsMap['dockerimage_gh-pages'],
							envVarsMap['dockerimage_pac']]


def githubUrl = envVarsMap['githubUrl']
def releasePraqmaCredentials = envVarsMap['releasePraqmaCredentials']
def dockerHostLabel = envVarsMap['dockerHostLabel']
def branchName = envVarsMap['branchName']


def readyBranch = envVarsMap['readyBranch']

def websites = [
	(envVarsMap['praqma.com']):(envVarsMap['praqma.com_giturl']),
  (envVarsMap['josra.org']):(envVarsMap['josra.org_giturl']),
  (envVarsMap['new.code-conf.com']):(envVarsMap['new.code-conf.com_giturl']),
  (envVarsMap['lakruzz.com']):(envVarsMap['lakruzz.com_giturl'])
]

def integrationBranches = [
	(envVarsMap['praqma.com']):(envVarsMap['praqma.com_integbranch']),
	(envVarsMap['josra.org']):(envVarsMap['josra.org_integbranch']),
	(envVarsMap['new.code-conf.com']):(envVarsMap['new.code-conf.com_integbranch']),
	(envVarsMap['lakruzz.com']):(envVarsMap['lakruzz.com_integbranch'])
]
