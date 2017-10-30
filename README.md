

Requirements 

1. jre 1.8.0 and above.

2. maven 3.3



Steps to deploy, After checkout ..

1. Run command 'mvn clean package'

	1.1 This will download all the dependent packages defined as dependency of this service.

	1.2 Run all defined test suites.

	1.3 Create a jar file 'vertx-crud-<version>-full.jar' (vertx-crud-0.0.1-full.jar)

2. To deploy this service use following command :

	(Windows)
	
	'java -jar target\vertx-crud-0.0.1-full.jar -conf src\main\resources\service-conf.json'
	
	(Unix)
	
	'java -jar target/vertx-crud-0.0.1-full.jar -conf src/main/resources/service-conf.json'
	

	
Database configuration

	1. PostgresSQL 9.6
	2. Find database schema defined at src/main/resources/project_schema.sql
	3. Create schema named 'master_schema'
	4. Create a sequence and set 'id' column default value as 'nextval('master_schema.<squuence_name>'::regclass)'




