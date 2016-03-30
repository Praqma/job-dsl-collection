package jobs

interface PipelineJob {
    String jobName;
    String downstreamJobName;
    boolean isDownstreamJobManual;

    String nodeLabel;
    String jdkVersion;

    void setDownstreamJob(String downstreamJobName, boolean manual)
    void execute(def context)
}
