--- Schema: master_schema		
 		
 SET SEARCH_PATH to master_schema;		
 		
 CREATE TABLE PROJECT (		
   id              INT    NOT NULL PRIMARY KEY,		
   name	          VARCHAR(50),		
   owner           VARCHAR(50)  NOT NULL,		
   policy JSON,		
   service_id      VARCHAR(256)		
 );