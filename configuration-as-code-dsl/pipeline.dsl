pipelineJob('Configuration as Code Plugin') {
    definition {
        cps {
            script('''
properties([parameters([booleanParam(defaultValue: false, description: '', name: 'isRelease')])])

node("dockerhost1") {

    stage("checkout") {
        git credentialsId: 'github', url: 'https://github.com/jenkinsci/configuration-as-code-plugin.git'
    } 
       
    stage("build") {
        buildMaven()
    }

    stage("changelog") {
        pac()
    }

    stage("release") {
        if(!params?.isRelease) {
            echo "Release build is not enabled"
        } else {
            echo "Release build enabled. Running release."
            releaseMaven()
        }
    }
}            
            ''')
            sandbox()
        }
    }
}