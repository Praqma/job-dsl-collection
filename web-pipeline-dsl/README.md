# Web pipeline DSL

## Infrastructure

If you want to use docker for your slaves. Docker plugin is required. With the latest version of docker (currently `1.10`) i had to build a snapshot version (`1.16.1-SNAPSHOT`) of the Docker plugin to get it working. Docker is also required if you want to have the preconfigured environment.

The docker plugin requires just a docker host that has internet access to pull images when needed. The docker host acts as a cloud on Jenkins and can spawn slaves on demand. 

The configuration of the cloud is self explanatory.

The job dsl also generates jobs that publish and build our docker image infrastructure for testing our websites.

### Required Jenkins plugins

* [GitHub plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Plugin)
* [Docker plugin](https://wiki.jenkins-ci.org/display/JENKINS/Docker+Plugin)
* [Git plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
* [CloudBees Docker Build and Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Build+and+Publish+plugin)
* [Pretested Integration Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Pretested+Integration+Plugin)
* [xUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/xUnit+Plugin)

### Docker images

The docker images all has the ability for the Docker plugin to connect to it using SSH. The username/password for the user added is jenkins/jenkins. 

Docker images used in the proces:

* [Praqma gh-pages image](https://hub.docker.com/r/praqma/gh-pages/)
* [Praqma linkchecker image](https://hub.docker.com/r/praqma/linkchecker/)

The docker images have the following repositories

* [Praqma gh-pages image](https://github.com/Praqma/docker-gh-pages)
* [Praqma linkchecker image](https://github.com/Praqma/docker-linkchecker) 


## Testing docker images

By convention we've decided to let the docker images be tested by adding a `test.sh` file in the root of the repo. When a commit is pushed, the `test.sh` file is executed, if it returns a non-zero value, the test has failed. 


