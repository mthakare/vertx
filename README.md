# Micro service to execute a patch workflow that spawns multiple different systems participating in the execution of a  patching process for a typical business service

Requirements 

1. jre 1.8.0 and above.

2. maven 3.3



Steps to deploy, After checkout ..

1. Run command 'mvn clean package'

	1.1 This will download all the dependent packages defined as dependency of this service.

	1.2 Run all defined test suites.

	1.3 Create a jar file 'patch-factory-<version>-full.jar' (patch-factory-0.0.1-full.jar)

2. To deploy this service use following command :
	'java -jar target\patch-factory-0.0.1-full.jar -conf src\main\resources\service-conf.json'


"# vertx" 
