package utils

class Publishers {

    static def publishArtifacts(def job, String target) {
        job.with {
            publishers {
                archiveArtifacts(target)
            }
        }
    }

    static def publishJUnit(def job) {
        job.with {
            publishers {
                archiveJunit('**/target/surefire-reports/*.xml, target/failsafe-reports/*.xml') {
                    retainLongStdout()
                }
            }
        }
    }

    static def mail(def job, String to) {
        job.with {
            publishers {
                mailer(to, false, false)
            }
        }
    }

    static def configurePretestedIntegration(def job) {
        job.with {
            publishers {
                pretestedIntegration()
            }
        }
    }

    static def runDownstreamJob(def job, String downstreamJobName) {
        job.with {
            publishers {
                downstreamParameterized {
                    trigger(downstreamJobName) {
                        parameters {
                            gitRevision(true)
                        }
                    }
                }
            }
        }
    }

    static def javaNCSS(def job) {
        job.with {
            configure {
                it / publishers << 'hudson.plugins.javancss.JavaNCSSPublisher' {
                    reportFilenamePattern '**/target/javancss-raw-report.xml'
                    targets {
                        'hudson.plugins.javancss.JavaNCSSHealthTarget' {
                            metric(class: 'hudson.plugins.javancss.JavaNCSSHealthMetrics', 'COMMENT_RATIO')
                        }
                        'hudson.plugins.javancss.JavaNCSSHealthTarget' {
                            metric(class: 'hudson.plugins.javancss.JavaNCSSHealthMetrics', 'JAVADOC_RATIO')
                            healthy(75.0)
                            unhealthy(25.0)
                        }
                    }
                }
            }
        }
    }

    static def compilerWarnings(def job, String... parsers) {
        job.with {
            publishers {
                warnings(parsers.toList())
            }
        }

    }

    static def coberturaReport(def job) {
        job.with {
            publishers {
                cobertura('**/target/site/cobertura/coverage.xml')
            }
        }
    }

    static def javaDoc(def job) {
        job.with {
            publishers {
                archiveJavadoc {
                    javadocDir 'target/site/apidocs'
                    keepAll true
                }
            }
        }
    }

    static def findBugsWarnings(def job) {
        job.with {
            publishers {
                findbugs('target/findbugsXml.xml', true) {}
            }
        }
    }

    static def pmdWarnings(def job) {
        job.with {
            publishers {
                pmd('target/pmd.xml') {}
            }
        }
    }

    static def duplicateCode(def job) {
        job.with {
            publishers {
                dry('target/cpd.xml', 50, 25) {
                    useStableBuildAsReference true
                }
            }
        }
    }

    static def checkstyleWarnings(def job) {
        job.with {
            publishers {
                checkstyle('target/checkstyle-result.xml') {
                }
            }
        }
    }

    static def openTasks(def job) {
        job.with {
            publishers {
                tasks('**/*.*', 'target/**', 'todo, fixme', '', '', true) {
                    thresholdLimit 'high'
                    defaultEncoding 'UTF-8'
                }
            }
        }
    }

    static def manualDownstreamJob(def job, String jobName, Map<String,String> properties = null) {
        job.with {
            publishers {
                buildPipelineTrigger(jobName) {
                    parameters {
                        gitRevision(true)
                        if(properties){
                            for(def kv : properties){
                                predefinedProp(kv.key, kv.value)
                            }
                        }
                    }
                }
            }
        }
    }

    static def executeShell(def job, String command) {
        job.with {
            publishers {
                postBuildScripts {
                    steps {
                        shell(command)
                    }
                }
            }
        }
    }
}
