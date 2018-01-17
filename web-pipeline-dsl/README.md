# Web Pipeline DSL

## Infrastructure

If you want to use Docker for your slaves. Docker plugin is required. With the latest version of Docker (currently `1.10`) i had to build a snapshot version (`1.16.1-SNAPSHOT`) of the Docker plugin to get it working. Docker is also required if you want to have the pre-configured environment.

The Docker plugin requires just a Docker host that has internet access to pull images when needed. The Docker host acts as a cloud on Jenkins and can spawn slaves on demand.

The configuration of the cloud is self explanatory.

The Job DSL also generates jobs that publish and build our Docker image infrastructure for testing our websites.

### Required Jenkins Plugins

* [GitHub Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Plugin)
* [Docker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Docker+Plugin)
* [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
* [CloudBees Docker Build and Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Build+and+Publish+plugin)
* [Pretested Integration Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Pretested+Integration+Plugin)
* [xUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/xUnit+Plugin)

### Docker Images

The Docker images all has the ability for the Docker plugin to connect to it using SSH. The username/password for the user added is jenkins/jenkins.

* [jekyll](https://hub.docker.com/r/praqma/jekyll/)
* ~~[gh-pages](https://hub.docker.com/r/praqma/gh-pages/)~~
* [linkchecker](https://hub.docker.com/r/praqma/linkchecker/)

The Docker images have the following repositories.

* [jekyll](https://github.com/Praqma/jekyll)
* ~~[gh-pages](https://github.com/Praqma/docker-gh-pages)~~
* [linkchecker](https://github.com/Praqma/docker-linkchecker)

## Testing Docker Images

By convention, we've decided to let the docker images be tested by adding a `test.sh` file in the root of the repository. When a commit is pushed, the `test.sh` file is executed, if it returns a non-zero value, the test has failed.
