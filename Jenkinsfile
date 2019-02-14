withCredentials([
  [
    $class          : 'UsernamePasswordMultiBinding',
    credentialsId   : 'admin',
    passwordVariable: 'ARTIFACTORY_PASSWORD',
    usernameVariable: 'ARTIFACTORY_USERNAME'

    
  ]
])

{

properties([
  parameters([
    string(name: 'buildnumber', defaultValue: '$BUILD_NUMBER', description: 'Docker Image TAG', )
   ])
])

node ('192.168.10.251')
{
    
	
	stage('Sourcecode Checkout') {
				
//below line will work both in jenkins pipelinescript & jenkins pipeline script from SCM		
//checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'anishgithub', url: 'https://github.com/anishpravin/gameoflife.git']]])
checkout scm	
	}
			

 						 
	stage('build gameoflife') {
	sh '''
	mvn --version
	mvn clean install
	'''
	                     }
						 
	stage('push gameoflife.war to artifactory') {
	sh '''
    curl -u$ARTIFACTORY_USERNAME:$ARTIFACTORY_PASSWORD -T $WORKSPACE/gameoflife-web/target/gameoflife.war "http://192.168.10.52:8081/artifactory/gameoflife/$BUILD_NUMBER/gameoflife.war"
	'''
	                                            }
	 
				 
										 
							  
												   
	stage ('build docker image') {
	            sh '''
				docker build -t gameoflife:$BUILD_NUMBER $WORKSPACE/gameoflife-web/ --file $WORKSPACE/gameoflife-web/Dockerfile
			   echo "BUILD_NUMBER is $BUILD_NUMBER"
			   buildnumber=$BUILD_NUMBER
	           echo "buildnumber is $buildnumber=$BUILD_NUMBER"
				   '''
				                }
								
	stage ('push gameoflife docker image to irisdevops123 dockerhub') {
	withDockerRegistry(credentialsId: 'irisautomation-dockerhub', url: 'https://index.docker.io/v1/') {
	            sh '''
				docker tag gameoflife:$BUILD_NUMBER irisdevops123/gameoflife:$BUILD_NUMBER
				docker push irisdevops123/gameoflife:$BUILD_NUMBER				
				'''
				                                                                                        }
										  }
										  
										  
										
		stage ('Deploy sample gameoflife container') {
	
	            sh '''
				docker stop gameoflife
				docker rm gameoflife				
				docker run -d -p 8889:8080 --name gameoflife gameoflife:$BUILD_NUMBER
				'''
				                }
								
		stage ('Test the Sample Container') {
		
		        sh '''
				sleep 5
				curl http://192.168.10.251:8889
				'''
				                            }
										  
										  
	stage ('Push to UCD...') {
       step([$class: 'UCDeployPublisher',
            siteName: 'irissoftware-ucd-trial',
            component: [
                $class: 'com.urbancode.jenkins.plugins.ucdeploy.VersionHelper$VersionBlock',
                componentName: 'gameoflife',
                createComponent: [
                    $class: 'com.urbancode.jenkins.plugins.ucdeploy.ComponentHelper$CreateComponentBlock'
                ],
                delivery: [
                    $class: 'com.urbancode.jenkins.plugins.ucdeploy.DeliveryHelper$Push',
                    pushVersion: '${BUILD_NUMBER}',
                    baseDir: 'workspace/$JOB_NAME/gameoflife-web/target/',
                    fileIncludePatterns: 'gameoflife.war',
                    fileExcludePatterns: '',
                    pushProperties: 'jenkins.server=Local\njenkins.reviewed=false',
                    pushDescription: 'Pushed from Jenkins',
                    pushIncremental: false
                ]
            ]
        ])
                             }

		

		
										  
										  
	/*stage ('Deploy gameoflife container') {

	           build job: 'gameoflife-deployment', parameters: [[$class: 'StringParameterValue', name: 'TAG', value: "$buildnumber"]]
				                }*/
}


}


