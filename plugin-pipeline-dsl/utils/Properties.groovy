package utils

class Properties {
    static def discardOldBuilds(def job, int maxDays, int maxBuilds) {
        job.with {
            properties {
                logRotator {
                    if (maxDays > -1)
                        daysToKeep(maxDays)
                    if (maxBuilds > -1)
                        numToKeep(maxBuilds)
                }
            }
        }
    }

    static def permissionToCopyArtifacts(def job, String downstreamJobName) {
        job.with {
            properties {
                configure {
                    it / 'properties' << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' {
                        projectNameList {
                            string(downstreamJobName)
                        }
                    }
                }
            }
        }
    }
}
