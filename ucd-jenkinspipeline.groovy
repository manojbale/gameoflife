node {
    stage ('Clone repository...') {
        checkout([$class: 'GitSCM',
branches: [[name: '*/master']],
doGenerateSubmoduleConfigurations: false,
extensions: [],
submoduleCfg: [],
userRemoteConfigs: [[url: 'https://github.com/ibm-datapower/datapower-configuration-manager/']]]
   	)
    }
    stage ('Build...') {
        env.PATH = "${tool 'Default'}/bin:${env.PATH}"
        sh 'ant'
    }
    
   stage ('Push to UCD...') {
       step([$class: 'UCDeployPublisher',
            siteName: 'Docker IBM UrbanCode Deploy Server',
            component: [
                $class: 'com.urbancode.jenkins.plugins.ucdeploy.VersionHelper$VersionBlock',
                componentName: 'UCD - Pipeline',
                createComponent: [
                    $class: 'com.urbancode.jenkins.plugins.ucdeploy.ComponentHelper$CreateComponentBlock'
                ],
                delivery: [
                    $class: 'com.urbancode.jenkins.plugins.ucdeploy.DeliveryHelper$Push',
                    pushVersion: '${BUILD_NUMBER}',
                    baseDir: '.',
                    fileIncludePatterns: '/var/jenkins_home/workspace/datapower-pipeline/dist/*',
                    fileExcludePatterns: '',
                    pushProperties: 'jenkins.server=Local\njenkins.reviewed=false',
                    pushDescription: 'Pushed from Jenkins',
                    pushIncremental: false
                ]
            ]
        ])
   }
}