package jobs

class Globals {
    static String PLUGIN_NAME = "foo"
    static String JENKINS_WIKI = "https://wiki.jenkins-ci.org/display/JENKINS/foo+plugin"

    static String GIT_REPOSITORY_PRAQMA = "git@github.com:bar/foo-plugin.git"
    static String GIT_REPOSITORY_JENKINSCI = "git@github.com:jenkinsci/foo-plugin.git"
    static String GIT_INTEGRATION_BRANCH = "master"
    static String GIT_CREDENTIALS_ID = null
    static String NODE_LABEL = null
    static String JDK = "1.8-LATEST"
    static String MAIL = "bar@domain.net"

    static int MAX_BUILDS = 50;
    static int MAX_DAYS = -1;
}
